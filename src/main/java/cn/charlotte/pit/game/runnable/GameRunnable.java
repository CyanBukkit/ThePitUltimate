package cn.charlotte.pit.game.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.data.sub.PlayerOption;
import cn.charlotte.pit.data.temp.TradeRequest;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.quest.AbstractQuest;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.ActionBarUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/5 0:30
 */
public class GameRunnable extends BukkitRunnable {
    @Getter
    private final static List<TradeRequest> tradeRequests = new ArrayList<>();

    private long tick = 0;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ITickTask task : ThePit.getInstance().getPerkFactory().getTickTasks()) {
                AbstractPerk perk = (AbstractPerk) task;
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                for (Map.Entry<Integer, PerkData> entry : profile.getChosePerk().entrySet()) {
                    if (entry.getValue().getPerkInternalName().equals(perk.getInternalPerkName())) {
                        if (tick % task.loopTick(entry.getValue().getLevel()) == 0) {
                            task.handle(entry.getValue().getLevel(), player);
                        }
                    }
                }
                for (PerkData entry : profile.getUnlockedPerk()) {
                    if (entry.getPerkInternalName().equals(perk.getInternalPerkName())) {
                        if (tick % task.loopTick(entry.getLevel()) == 0) {
                            task.handle(entry.getLevel(), player);
                        }
                    }
                }
            }
            for (ITickTask tickTask : ThePit.getInstance().getEnchantmentFactor().getTickTasks()) {
                AbstractEnchantment enchantment = (AbstractEnchantment) tickTask;
                for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    int level = enchantment.getItemEnchantLevel(itemStack);
                    if (level >= 0) {
                        if (tick % tickTask.loopTick(level) == 0) {
                            tickTask.handle(level, player);
                        }
                    }
                }

                ItemStack itemInHand = player.getInventory().getItemInHand();
                if (itemInHand != null && itemInHand.getType() != Material.AIR && itemInHand.getType() != Material.LEATHER_LEGGINGS) {
                    int level = enchantment.getItemEnchantLevel(itemInHand);
                    if (level >= 0) {
                        if (tick % tickTask.loopTick(level) == 0) {
                            tickTask.handle(level, player);
                        }
                    }
                }
            }

            StringBuilder builder = new StringBuilder();
            for (IActionDisplayEnchant enchant : ThePit.getInstance().getEnchantmentFactor().getActionDisplayEnchants()) {
                final AbstractEnchantment enchantment = (AbstractEnchantment) enchant;
                ItemStack itemInHand = player.getInventory().getItemInHand();
                if (itemInHand != null && itemInHand.getType() != Material.AIR && itemInHand.getType() != Material.LEATHER_LEGGINGS) {
                    int level = enchantment.getItemEnchantLevel(itemInHand);
                    if (level > 0) {
                        builder.append("&b&l")
                                .append(enchantment.getEnchantName())
                                .append(" ");
                        if (player.hasMetadata("combo_venom") && player.getMetadata("combo_venom").get(0).asLong() > System.currentTimeMillis() && (enchantment.getRarity() != EnchantmentRarity.DARK_NORMAL && enchantment.getRarity() != EnchantmentRarity.DARK_RARE)) {
                            builder.append("&c&l✖");
                        } else {
                            builder.append(enchant.getText(level, player));
                        }
                        builder.append("&r ");
                    }
                }
                ItemStack itemLeggings = player.getInventory().getLeggings();
                if (itemLeggings != null && itemLeggings.getType() == Material.LEATHER_LEGGINGS) {
                    int level = enchantment.getItemEnchantLevel(itemLeggings);
                    if (level > 0) {
                        builder.append("&b&l")
                                .append(enchantment.getEnchantName())
                                .append(" ");
                        if ((PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) && (enchantment.getRarity() != EnchantmentRarity.DARK_NORMAL && enchantment.getRarity() != EnchantmentRarity.DARK_RARE)) {
                            builder.append("&c&l✖");
                        } else {
                            builder.append(enchant.getText(level, player));
                        }
                        builder.append("&r ");
                    }
                }
            }
            if (PlayerUtil.isVenom(player)) {
                long duration = player.getMetadata("combo_venom").get(0).asLong() - System.currentTimeMillis();
                builder.append("&c&l沉默 &d&l")
                        .append(TimeUtil.millisToSeconds(duration))
                        .append("秒&r ");
            }
            if (builder.length() > 0 && tick % 5 == 0) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                PlayerOption.BarPriority option = profile.getPlayerOption().getBarPriority();
                if (option == PlayerOption.BarPriority.ENCHANT_ONLY || (option == PlayerOption.BarPriority.DAMAGE_PRIORITY && (profile.getCombatTimer().hasExpired() || System.currentTimeMillis() - profile.getCombatTimer().getStart() > 500))) {
                    ActionBarUtil.sendActionBar(player, builder.toString());
                }
            }

            for (ITickTask tickTask : ThePit.getInstance().getQuestFactory().getTickTasks()) {
                AbstractQuest quest = (AbstractQuest) tickTask;
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                if (profile.getCurrentQuest() != null && profile.getCurrentQuest().getInternalName().equals(quest.getQuestInternalName())) {
                    if (tick % tickTask.loopTick(profile.getCurrentQuest().getLevel()) == 0) {
                        tickTask.handle(profile.getCurrentQuest().getLevel(), player);
                    }
                }
            }

            List<TradeRequest> collect = tradeRequests.stream()
                    .filter(tradeRequest -> tradeRequest.getCooldown().hasExpired())
                    .collect(Collectors.toList());

            tradeRequests.removeAll(collect);
        }

        tick++;
    }
}
