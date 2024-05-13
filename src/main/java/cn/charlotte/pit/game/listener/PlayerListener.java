package cn.charlotte.pit.game.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.data.sub.PlayerBanData;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.enchantment.menu.MythicWellMenu;
import cn.charlotte.pit.enchantment.type.normal.PebbleEnchant;
import cn.charlotte.pit.event.PitProfileLoadedEvent;
import cn.charlotte.pit.item.type.ChunkOfVileItem;
import cn.charlotte.pit.item.type.PitCactus;
import cn.charlotte.pit.item.type.mythic.*;
import cn.charlotte.pit.medal.impl.challenge.LuckyDiamondMedal;
import cn.charlotte.pit.medal.impl.challenge.TrickleDownMedal;
import cn.charlotte.pit.menu.admin.item.button.ShopItemButton;
import cn.charlotte.pit.menu.item.cactus.CactusMenu;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.perk.type.prestige.MarathonPerk;
import cn.charlotte.pit.perk.type.prestige.MythicismPerk;
import cn.charlotte.pit.perk.type.prestige.YummyPerk;
import cn.charlotte.pit.perk.type.shop.TrickleDownPerk;
import cn.charlotte.pit.runnable.ProfileLoadRunnable;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.VectorUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import cn.hutool.core.lang.WeightRandom;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 22:00
 */
@AutoRegister
public class PlayerListener implements Listener {
    private final Map<Player, Long> goldenAppleCooldown = new HashMap<>();
    private final Map<Player, Long> firstAidEggCooldown = new HashMap<>();
    private final Random random = new Random();
    private final DecimalFormat numFormat = new DecimalFormat("0.00");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ThePit pit = ThePit.getInstance();
        Player player = event.getPlayer();

        PlayerUtil.clearPlayer(player, true);
        if (pit.getPitConfig().getSpawnLocations().size() != 0) {
            Location location = pit.getPitConfig()
                    .getSpawnLocations()
                    .get(random.nextInt(pit.getPitConfig().getSpawnLocations().size()));

            player.teleport(location);
        } else {
            player.sendMessage(CC.translate("&cNo spawn found "));
        }

        if (ProfileLoadRunnable.getInstance() == null) {
            event.getPlayer().kickPlayer(" ");
            return;
        }

        ProfileLoadRunnable.getInstance().handleJoin(player);
    }

    @EventHandler
    public void onProfileLoadComplete(PitProfileLoadedEvent event) {
        PlayerProfile profile = event.getPlayerProfile();
        Player player = Bukkit.getPlayer(profile.getPlayerUuid());

        if (player == null || !player.isOnline()) {
            return;
        }

        if (ThePit.getBungeeServerName().equals("NULL")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetServer");
            try {
                player.sendPluginMessage(ThePit.getInstance(), "BungeeCord", out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                ThePit.getInstance().getLogger().warning("Failed to send getServerName message to the BungeeCord.");
            }
        }
        if (profile.isBanned()) {
            PlayerBanData banData = profile.getPlayerBanData();
            player.sendMessage(CC.translate("&4⚠ &c你当前已被禁止游玩天坑乱斗!"));
            player.sendMessage(CC.translate("&4⚠ &c此限制将在 &f" + TimeUtil.millisToRoundedTime(banData.getEnd() - System.currentTimeMillis()) + " &c后自动解除."));
            player.sendMessage(CC.translate("&4⚠ &c原因: &f" + profile.getPlayerBanData().getReason()));
            if (player.hasPermission("thepit.admin")) {
                player.sendMessage(CC.translate("&2⚠ &a但你是管理员,因此没有被移出房间."));
            } else {
                player.kickPlayer("You are currently suspended on this Pit server.");
            }
        } else {
            this.welcomePlayer(player);
        }

        PlayerInv playerInv = profile.getInventory();
        playerInv.applyItemToPlayer(player);
        InventoryUtil.supplyItems(player);
        profile.setLastLogoutTime(System.currentTimeMillis());
        profile.applyExperienceToPlayer(player);

        PlayerUtil.clearPlayer(player, false, false);
    }

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            if (PlayerUtil.isStaffSpectating((Player) event.getPlayer())) {
                return;
            }
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(event.getPlayer().getUniqueId());
            if (!profile.isTempInvUsing()) {
                profile.setInventory(PlayerInv.fromPlayerInventory(event.getPlayer().getInventory()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (PlayerUtil.isStaffSpectating(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        event.getItem().remove();
        Player player = event.getPlayer();

        ItemStack stack = event.getItem().getItemStack();
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(stack);

        if (nmsItem.getItem() instanceof ItemArmor) {
            if (ItemUtil.getInternalName(stack) == null) {
                return;
            }
            ItemStack itemStack;
            String internalName = ItemUtil.getInternalName(stack);
            if (internalName.equals("lucky_diamond")) {
                new LuckyDiamondMedal().addProgress(PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()), 1);
                itemStack = new ItemBuilder(stack.getType()).canDrop(false).canSaveToEnderChest(false).deathDrop(true).canTrade(false).removeOnJoin(true).internalName(internalName).lore("&b幸运钻石天赋物品").buildWithUnbreakable();
            } else {
                itemStack = new ItemBuilder(stack.getType()).canDrop(false).canSaveToEnderChest(true).deathDrop(true).internalName(internalName).buildWithUnbreakable();
            }

            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getType() != Material.AIR && itemStack.getType().name().contains("IRON") && item.getType() == itemStack.getType()) {
                    return;
                }
            }

            int slot = InventoryUtil.getArmorSlot(itemStack.getType());
            if (slot == -1) {
                return;
            }
            if (itemStack.getType().name().contains("BOOTS")) {
                if (PlayerUtil.isPlayerChosePerk(player, new MarathonPerk().getInternalPerkName())) {
                    return;
                }
            }
            ItemStack[] armorContents = player.getInventory().getArmorContents();
            if (itemStack.getType().name().contains("IRON")) {
                if (armorContents[slot].getType().name().contains("DIAMOND")) {
                    return;
                }
                //check if player is equipping a mythic pants
                if (slot == 1 && ItemUtil.getInternalName(armorContents[slot]) != null && ItemUtil.getInternalName(armorContents[slot]).equalsIgnoreCase("mythic_leggings")) {
                    return;
                }
                armorContents[slot] = itemStack;
                player.getInventory().setArmorContents(armorContents);

                player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1F, 1F);
            } else if (itemStack.getType().name().contains("DIAMOND")) {
                if (armorContents[slot].getType().name().contains("DIAMOND") || armorContents[slot].getType().name().contains("IRON")) {
                    InventoryUtil.addInvReverse(player.getInventory(), armorContents[slot]);
                } else if (slot == 1 && ItemUtil.getInternalName(armorContents[slot]) != null && ItemUtil.getInternalName(armorContents[slot]).equalsIgnoreCase("mythic_leggings")) {
                    //check if player is equipping a mythic pants
                    InventoryUtil.addInvReverse(player.getInventory(), itemStack);
                    return;
                }
                armorContents[slot] = itemStack;
                player.getInventory().setArmorContents(armorContents);
                player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1F, 1F);
            }
        } else if (stack.getType() == Material.ARROW) {
            ItemBuilder arrowBuilder = new ItemBuilder(Material.ARROW).internalName("default_arrow").defaultItem().canDrop(false).canSaveToEnderChest(false);
            player.getInventory().addItem(arrowBuilder.build());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
        } else if ((stack.getType() == Material.BOW || CraftItemStack.asNMSCopy(stack).getItem() instanceof ItemSword) && !event.isCancelled()) {
            InventoryUtil.addInvReverse(player.getInventory(), event.getItem().getItemStack());
        } else if (stack.getType() == Material.GOLD_INGOT) {
            if (event.getItem().hasMetadata("gold")) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(event.getPlayer().getUniqueId());
                int gold = event.getItem().getMetadata("gold").get(0).asInt();
                if (PlayerUtil.isPlayerChosePerk(player, new TrickleDownPerk().getInternalPerkName())) {
                    gold += 10;
                    PlayerUtil.heal(player, 4);
                    new TrickleDownMedal().addProgress(profile, 10);
                }
                int level = new PebbleEnchant().getItemEnchantLevel(event.getPlayer().getInventory().getLeggings());
                if (PlayerUtil.isVenom(event.getPlayer()) || PlayerUtil.isEquippingSomber(event.getPlayer())) {
                    level = 0;
                }
                if (level > 0) {
                    gold += level * 10;
                    if (level >= 3) {
                        PlayerUtil.heal(player, 2);
                    }
                }
                profile.setCoins(profile.getCoins() + gold);

                profile.setGoldPicked(profile.getGoldPicked() + 1);
                player.sendMessage(CC.translate("&6&l捡起硬币! &7从地上找到了&6 " + gold + " &7硬币!"));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1.8F);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
                event.setCancelled(true);
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(event.getPlayer().getUniqueId());
                for (PerkData perk : profile.getUnlockedPerk()) {
                    if (perk.getPerkInternalName().equals("Mythicism")) {
                        new MythicWellMenu(event.getPlayer()).openMenu(event.getPlayer());
                        return;
                    }
                }
                event.getPlayer().sendMessage(CC.translate("&c你需要达到 " + LevelUtil.getLevelTag(0, 120) + " &c解锁精通玩法并解锁精通天赋 &6" + new MythicismPerk().getDisplayName() + " &c以使用神话之井!"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
            return;
        }
        Player player = e.getPlayer();
        if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.WORKBENCH
                || e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType().name().contains("FURNACE") || e.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
            e.setCancelled(true);
            return;
        }
        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (player.getGameMode() != GameMode.CREATIVE && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3 && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && "golden_head".equals(ItemUtil.getInternalName(item))) {
            e.setCancelled(true);
            if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                e.setUseItemInHand(Event.Result.DENY);
                return;
            }
            player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
            goldenAppleCooldown.put(player, System.currentTimeMillis());
            PlayerUtil.takeOneItemInHand(player);
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 20 * 8, 0), true);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1), true);
            (((CraftPlayer) player).getHandle()).setAbsorptionHearts(6.0F);
            if (PlayerUtil.isPlayerUnlockedPerk(player, new YummyPerk().getInternalPerkName())) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                profile.setCoins(profile.getCoins() + 3);
            }
        } else if (item.getType() == Material.BAKED_POTATO) {
            if ("angry_potato".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    return;
                }
                player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
                goldenAppleCooldown.put(player, System.currentTimeMillis());
                PlayerUtil.takeOneItemInHand(player);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 80, 1), true);
                (((CraftPlayer) player).getHandle()).setAbsorptionHearts(4.0F);
            }
        } else if (item.getType() == Material.MAGMA_CREAM) {
            if ("broken_soul".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    return;
                }
                player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
                goldenAppleCooldown.put(player, System.currentTimeMillis());
                PlayerUtil.takeOneItemInHand(player);
                PlayerUtil.heal(player, 8);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1), true);
                (((CraftPlayer) player).getHandle()).setAbsorptionHearts(6.0F);
            }
        } else if (item.getType() == Material.MONSTER_EGG) {
            if ("first_aid_egg".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - firstAidEggCooldown.getOrDefault(player, 0L) <= 10000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    e.getPlayer().sendMessage(CC.translate("&c在再次使用此物品前,请等待" + TimeUtil.millisToRoundedTime(10000L - (System.currentTimeMillis() - firstAidEggCooldown.getOrDefault(player, 0L))).replace(" ", "") + "!"));
                    return;
                }
                player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
                firstAidEggCooldown.put(player, System.currentTimeMillis());
                PlayerUtil.takeOneItemInHand(player);
                PlayerUtil.heal(player, 5);
            }
        } else if (item.getType() == Material.MUSHROOM_SOUP) {
            if ("perk_tasty_soup_kill".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    return;
                }
                player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
                goldenAppleCooldown.put(player, System.currentTimeMillis());
                PlayerUtil.takeOneItemInHand(player);
                PlayerUtil.heal(player, 2);
                player.removePotionEffect(PotionEffectType.SPEED);
                PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 20 * 7, 0), true);
                (((CraftPlayer) player).getHandle()).setAbsorptionHearts(Math.min(8F, ((CraftPlayer) player).getHandle().getAbsorptionHearts() + 2F));
            }
            if ("perk_tasty_soup_assist".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    return;
                }
                player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
                goldenAppleCooldown.put(player, System.currentTimeMillis());
                PlayerUtil.takeOneItemInHand(player);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1), true);
                player.removePotionEffect(PotionEffectType.SPEED);
                PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 20 * 7, 0), true);
                (((CraftPlayer) player).getHandle()).setAbsorptionHearts(Math.min(8F, ((CraftPlayer) player).getHandle().getAbsorptionHearts() + 2F));
            }
        } else if (item.getType() == Material.CACTUS) {
            if ("cactus".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                if (System.currentTimeMillis() - goldenAppleCooldown.getOrDefault(player, 0L) <= 1000L) {
                    e.setUseItemInHand(Event.Result.DENY);
                    return;
                }
                new CactusMenu().openMenu(player);
            }
        } else if (item.getType() == Material.ENDER_CHEST) {
            if ("reward_uber".equals(ItemUtil.getInternalName(item))) {
                e.setCancelled(true);
                PlayerUtil.takeOneItemInHand(player);
                WeightRandom<ItemStack> weightRandom = WeightRandom.<ItemStack>create()
                        .add(new GemSwordItem().deathDrop(true).toItemStack(), 3.1)
                        .add(new MythicSwordItem().toItemStack(), 8.5)
                        .add(new MythicLeggingsItem().toItemStack(), 8.5)
                        .add(new MythicBowItem().toItemStack(), 8.5)
                        .add(new ItemStack(Material.GOLD_BLOCK, cn.hutool.core.util.RandomUtil.randomInt(1, 8)), 4)
                        .add(PitCactus.toItemStack(), 3.63)
                        .add(new MagicFishingRod().toItemStack(), 0.5)
                        .add(new ShopItemButton(Material.DIAMOND_SWORD, "shopItem").getButtonItem(player), 8)
                        .add(new ItemBuilder(ChunkOfVileItem.toItemStack()).amount(cn.hutool.core.util.RandomUtil.randomInt(5, 10)).build(), 4);
                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < cn.hutool.core.util.RandomUtil.randomInt(2, 3); i++) {
                    ItemStack itemStack = weightRandom.next();
                    while (list.contains(itemStack)) {
                        itemStack = weightRandom.next();
                    }
                    list.add(itemStack);
                }
                for (ItemStack itemStack : list) {
                    if (itemStack.getType() == Material.GOLD_BLOCK) {
                        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                        profile.setRenown(profile.getRenown() + itemStack.getAmount());
                        CC.send(MessageType.MISC, player, "§d§lUber! §e+" + itemStack.getAmount() + "声望");
                    } else {
                        player.getInventory().addItem(itemStack);
                        CC.send(MessageType.MISC, player, "§d§lUber! §a+" +
                                (itemStack.getType() == Material.DIAMOND_SWORD ? "钻石剑" : itemStack.getItemMeta().getDisplayName()) +
                                (itemStack.getAmount() > 1 ? " x" + itemStack.getAmount() : ""));
                    }
                }
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE) {
            (((CraftPlayer) event.getPlayer()).getHandle()).setAbsorptionHearts(8.0F);
            PlayerUtil.takeOneItemInHand(player);
            player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 100, 1), true);
            if (PlayerUtil.isPlayerUnlockedPerk(player, new YummyPerk().getInternalPerkName())) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                profile.setCoins(profile.getCoins() + 3);
            }

            event.setCancelled(true);
        }

        if (event.getItem() != null && "perk_olympus".equals(ItemUtil.getInternalName(event.getItem()))) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            PlayerUtil.takeOneItemInHand(player);
            player.playSound(player.getLocation(), Sound.EAT, 1F, 1F);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 2), true);
            player.removePotionEffect(PotionEffectType.SPEED);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 24 * 20, 0), true);
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1), true);
            profile.setExperience(profile.getExperience() + 27);

            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (PlayerUtil.isStaffSpectating(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (ItemUtil.isDefaultItem(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }

        if (!ItemUtil.canDrop(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
//            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if ("golden_head".equals(ItemUtil.getInternalName(event.getCursor())) && event.getSlotType() != null && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }

        if (event.getClickedInventory() instanceof CraftingInventory) {
            event.setCancelled(true);
        }
    }

    private void welcomePlayer(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.isNicked()) {
            player.sendMessage(CC.translate("&2&l匿名模式! &7你现在对外显示的游戏名为: " + profile.getFormattedNameWithRoman(player)));
        }
        if (PlayerUtil.isPlayerUnlockedPerk(player, "ShowNewPlayer")) {
            profile.getUnlockedPerk().removeIf(e -> e.getPerkInternalName().equals("ShowNewPlayer"));
            int i = 1;
            profile.setRenown(profile.getRenown() + i);
            player.sendMessage("§b§l新手保护功能已下架，您购买的'保护终结者'已转换为§e声望§b§l，您获得了 §e" + i + " 点声望");
        }
    }

    @EventHandler
    public void onThrowTnt(PlayerInteractEvent event) {
        if ("tnt".equals(ItemUtil.getInternalName(event.getItem()))) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                event.setCancelled(true);

                if (event.getPlayer().getItemInHand().getAmount() > 1) {
                    event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                } else {
                    event.getPlayer().setItemInHand(null);
                }

                BlockIterator blockIterator = new BlockIterator(event.getPlayer());
                TNTPrimed tntPrimed = (TNTPrimed) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(30);
                for (int i = 0; i < 20; i++) {
                    blockIterator.next();
                }

                tntPrimed.setMetadata("internal", new FixedMetadataValue(ThePit.getInstance(), "tnt"));
                tntPrimed.setMetadata("shooter", new FixedMetadataValue(ThePit.getInstance(), event.getPlayer().getUniqueId().toString()));

                VectorUtil.entityPush(tntPrimed, blockIterator.next().getLocation(), 25);
            }
        } else if ("red_packet".equals(ItemUtil.getInternalName(event.getItem()))) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                event.setCancelled(true);

                if (event.getPlayer().getItemInHand().getAmount() > 1) {
                    event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                } else {
                    event.getPlayer().setItemInHand(null);
                }

                BlockIterator blockIterator = new BlockIterator(event.getPlayer());
                TNTPrimed tntPrimed = (TNTPrimed) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(30);
                for (int i = 0; i < 20; i++) {
                    blockIterator.next();
                }

                Integer money = ItemUtil.getItemIntData(event.getItem(), "money");
                String sender = ItemUtil.getItemStringData(event.getItem(), "sender");
                if (!event.getPlayer().getUniqueId().toString().equals(sender)) {
                    event.getPlayer().sendMessage(CC.translate("&C你并非红包的主人"));
                    return;
                }

                tntPrimed.setMetadata("money", new FixedMetadataValue(ThePit.getInstance(), money));
                tntPrimed.setMetadata("internal", new FixedMetadataValue(ThePit.getInstance(), "red_packet"));
                tntPrimed.setMetadata("shooter", new FixedMetadataValue(ThePit.getInstance(), event.getPlayer().getUniqueId().toString()));

                VectorUtil.entityPush(tntPrimed, blockIterator.next().getLocation(), 25);
            }
        }
    }

    @EventHandler
    public void onPreExplosion(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            if (event.getEntity().hasMetadata("internal") && event.getEntity().getMetadata("internal").size() > 0 && "red_packet".equals(event.getEntity().getMetadata("internal").get(0).asString())) {
                Location location = event.getEntity().getLocation();
                float radius = event.getRadius();

                List<Player> players = PlayerUtil.getNearbyPlayers(location, radius);
                Map<Player, Double> distanceMap = new HashMap<>();
                double distanceSum = players.stream().mapToDouble(player -> {
                    double distance = player.getLocation().distance(location);
                    distanceMap.put(player, distance);
                    return distance;
                }).sum();

                int money = event.getEntity().getMetadata("money").get(0).asInt();
                String shooter = event.getEntity().getMetadata("shooter").get(0).asString();

                for (Map.Entry<Player, Double> entry : distanceMap.entrySet()) {
                    Double distance = entry.getValue();
                    Player player = entry.getKey();


                    double value = 1 - (distance / (distanceSum));
                    int given = (int) (money * value);
                    PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(UUID.fromString(shooter));
                    String name = profile.getPlayerName();

                    player.sendMessage(CC.translate("&c&l红包! &7你接受到了来自 &6" + name + " &7的红包 &6+" + given + "硬币&7(#" + numFormat.format(value * 100) + "%)"));
                    PlayerProfile profileByUuid = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                    profileByUuid.setCoins(profileByUuid.getCoins() + given);
                }

            }
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (event.getEntity().hasMetadata("shooter") && event.getEntity().getMetadata("shooter").size() > 0) {
            event.setYield(0);
            event.blockList().clear();
            if (event.getEntity().hasMetadata("internal") && event.getEntity().getMetadata("internal").size() > 0 && (event.getEntity().getMetadata("internal").get(0).asString().equalsIgnoreCase("tnt_enchant_item") || event.getEntity().getMetadata("internal").get(0).asString().equalsIgnoreCase("insta_boom_enchant_item"))) {
                return;
            }
            List<EntityFallingBlock> fallingBlocks = new ArrayList<>();
            List<Player> players = new ArrayList<>();

            for (Block b : event.blockList()) {

                if (RandomUtil.random.nextBoolean()) {
                    continue;
                }

                double x = -2.0d + (Math.random() * 4.0d);

                double y = -3.0d + (Math.random() * 6.0d);

                double z = -2.0d + (Math.random() * 4.0d);

                Location location = event.getLocation();
                WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
                int blockX = location.getBlockX();
                int blockY = location.getBlockY();
                int blockZ = location.getBlockZ();

                EntityFallingBlock fallingBlock = new EntityFallingBlock(world, blockX, blockY, blockZ, net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).fromLegacyData(0));
                fallingBlock.motX = x;
                fallingBlock.motY = y;
                fallingBlock.motZ = z;
                fallingBlock.velocityChanged = true;

                fallingBlocks.add(fallingBlock);

                PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(fallingBlock, 70, net.minecraft.server.v1_8_R3.Block.getCombinedId(fallingBlock.getBlock()));
                PacketPlayOutEntityVelocity vectorPacket = new PacketPlayOutEntityVelocity(fallingBlock);


                for (Player player : PlayerUtil.getNearbyPlayers(event.getLocation(), 30)) {
                    players.add(player);
                    EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                    entityPlayer.playerConnection.sendPacket(spawnPacket);
                    entityPlayer.playerConnection.sendPacket(vectorPacket);
                }
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(ThePit.getInstance(), () -> {
                List<Integer> list = fallingBlocks.stream()
                        .map(Entity::getId)
                        .collect(Collectors.toList());

                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy();
                int[] a = new int[list.size()];

                for (int i = 0; i < list.size(); i++) {
                    a[i] = list.get(i);
                }

                try {
                    Field field = packet.getClass().getDeclaredField("a");
                    field.setAccessible(true);
                    field.set(packet, a);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Player player : players) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }

            }, 20 * 5);
        }
    }


}
