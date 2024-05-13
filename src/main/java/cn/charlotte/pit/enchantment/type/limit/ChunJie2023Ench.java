package cn.charlotte.pit.enchantment.type.limit;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.event.PitStreakKillChangeEvent;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.type.mythic.*;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.music.NBSDecoder;
import cn.charlotte.pit.util.music.PositionSongPlayer;
import cn.charlotte.pit.util.music.Song;
import cn.hutool.core.util.RandomUtil;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import spg.lgdev.handler.MovementHandler;
import spg.lgdev.iSpigot;

import java.util.*;

/**
 * 2023/1/20<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@ArmorOnly
@WeaponOnly
@AutoRegister
public class ChunJie2023Ench extends AbstractEnchantment implements ITickTask, MovementHandler, IPlayerKilledEntity, Listener {
    private static final Song bgm = NBSDecoder.parse(ThePit.getInstance().getClass().getClassLoader().getResourceAsStream("chunjiexuqu.nbs"));
    private static final Map<UUID, PositionSongPlayer> playerMap = new HashMap<>();
    private static final String nbtName = "chunjie_2023_dj_chunjiexuqu";

    public static ChunJie2023Ench instance() {
        return (ChunJie2023Ench) ThePit.getInstance().getEnchantmentFactor().getEnchantmentMap().get(nbtName);
    }

    public ChunJie2023Ench() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Map.Entry<UUID, PositionSongPlayer>> entries = new HashSet<>(playerMap.entrySet());
                for (Map.Entry<UUID, PositionSongPlayer> entry : entries) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                        continue;
                    }
                    if (player.getInventory().getLeggings() == null || getItemEnchantLevel(player.getInventory().getLeggings()) == -1) {
                        if (getItemEnchantLevel(player.getItemInHand()) > 0) {
                            continue;
                        }
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                    }
                }
            }
        }.runTaskTimer(ThePit.getInstance(), 20, 20);

        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (Exception ignore) {
        }
    }

    @Override
    public String getEnchantName() {
        return "&c春节 &9| &f2023";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return nbtName;
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7向周围的玩家播放音乐: &c春节序曲"
                + "/s&7击杀时额外增加&b" + (20.23 * enchantLevel) + "经验" +
                "/s&7击杀时额外增加&6" + (20.23 * enchantLevel) + "硬币" +
                "/s&7击杀玩家后随机获得一个药水效果III(00:02-00:08)" +
                "/s&7每连杀" + enchantLevel * 23 + "个增加&b" + (enchantLevel * 2023) + "经验" +
                "/s&7连杀到达666时随机获得一个神话物品"
                ;
    }


    @Override
    public void handle(int enchantLevel, Player target) {
        PositionSongPlayer songPlayer = playerMap.get(target.getUniqueId());
        if (songPlayer == null) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
            PositionSongPlayer player = new PositionSongPlayer(bgm);
            player.setTargetLocation(target.getLocation());
            player.setAutoDestroy(false);
            player.setLoop(true);
            player.setPlaying(true);
            player.setVolume((byte) 0.08);

            playerMap.put(target.getUniqueId(), player);
        } else {
            target.getWorld().playEffect(target.getLocation().clone().add(0, 3, 0), Effect.NOTE, 1);
        }
    }

    @Override
    public int loopTick(int enchantLevel) {
        return 10;
    }

    @Override
    public void handleUpdateLocation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        PositionSongPlayer songPlayer = playerMap.get(player.getUniqueId());
        if (songPlayer != null) {
            songPlayer.setTargetLocation(player.getPlayer().getLocation());
        }
    }

    @Override
    public void handleUpdateRotation(Player var1, Location var2, Location var3, PacketPlayInFlying var4) {

    }

    @Override
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        experience.getAndAdd(20.23 * enchantLevel);
        coins.getAndAdd(20.23 * enchantLevel);
        sync(() -> {
            List<PotionEffectType> values = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
            values.remove(PotionEffectType.HARM);
            PotionEffectType effectType = RandomUtil.randomEle(values);
            myself.removePotionEffect(effectType);
            PlayerUtil.addPotionEffect(myself, new PotionEffect(effectType, 20 * RandomUtil.randomInt(2, 8), 2), true);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onStreak(PitStreakKillChangeEvent event) {
        Player myself = Bukkit.getPlayer(event.getPlayerProfile().getPlayerUuid());
        if (myself == null || !myself.isOnline()) {
            return;
        }
        //trigger check (every X streak)
        int handLv = getItemEnchantLevel(myself.getItemInHand());
        int legLv = getItemEnchantLevel(myself.getInventory().getLeggings());
        int enchLevel = Math.max(legLv, handLv);
        if (enchLevel <= 0) {
            return;
        }
        int streak = enchLevel * 23;
        if (Math.floor(event.getFrom()) % streak != 0 && Math.floor(event.getTo()) % streak == 0) {
            int exp = enchLevel * 2023;
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
            if (profile.isLoaded()) {
                profile.setExperience(profile.getExperience() + exp);
                profile.applyExperienceToPlayer(myself);
            }
        }
        if (Math.floor(event.getFrom()) < 666 && Math.floor(event.getTo()) == 666) {
            IMythicItem iMythicItem = RandomUtil.randomEle(new IMythicItem[]{new MythicBowItem(), new MythicLeggingsItem(), new MythicSwordItem(), new MagicFishingRod(), new GemSwordItem()
            });
            ItemStack itemStack = iMythicItem.toItemStack();
            myself.playSound(myself.getLocation(), Sound.VILLAGER_YES, 1, 1);
            String name = itemStack.getType().name();
            if (itemStack.hasItemMeta() || itemStack.getItemMeta() != null) {
                if (itemStack.getItemMeta().hasDisplayName()) {
                    name = itemStack.getItemMeta().getDisplayName();
                }
            }
            myself.sendMessage(CC.translate("&d&l限定! &7连杀到达666,获得了: &e" + name));
            myself.getInventory().addItem(itemStack);
        }
    }

}
