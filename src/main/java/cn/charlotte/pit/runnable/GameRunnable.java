package cn.charlotte.pit.runnable;
import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.data.temp.TradeRequest;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.Utils;
import dev.jnic.annotation.Include;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.spigot.async.utils.ResettableLatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/5 0:30
 */
public class GameRunnable extends BukkitRunnable {
    @Getter
    private final static ObjectArrayList<TradeRequest> tradeRequests = new ObjectArrayList<>();

    final Map<String, ITickTask> enchantTicks = ThePit.getInstance().getEnchantmentFactor().getTickTasks();

    final Map<String, ITickTask> ticksPerk = ThePit.getInstance().getPerkFactory().getTickTasks();

    private long tick = 0;
    @SneakyThrows
    @Override
    public void run() {

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

                for (Map.Entry<Integer, PerkData> entry : profile.getChosePerk().entrySet()) {
                    final ITickTask task = ticksPerk.get(entry.getValue().getPerkInternalName());
                    if (task != null) {
                        if (tick % task.loopTick(entry.getValue().getLevel()) == 0) {
                            task.handle(entry.getValue().getLevel(), player);
                        }
                    }
                }

                for (Map.Entry<String, PerkData> entry : profile.getUnlockedPerkMap().entrySet()) {
                    final ITickTask task = ticksPerk.get(entry.getValue().getPerkInternalName());
                    if (task != null) {
                        if (tick % Math.max(0, task.loopTick(entry.getValue().getLevel())) == 0) {
                            task.handle(entry.getValue().getLevel(), player);
                        }
                    }
                }


                //裤子
                final ItemStack leggings = player.getInventory().getLeggings();
                if (leggings != null) {
                    handleIMythicItemTickTasks(leggings,player);
                }

                ItemStack itemInHand = player.getInventory().getItemInHand();
                if (itemInHand != null && itemInHand.getType() != Material.AIR && itemInHand.getType() != Material.LEATHER_LEGGINGS && itemInHand.getType() != Material.PAPER) {
                    handleIMythicItemTickTasks(itemInHand,player);
                }

                tradeRequests.removeIf(next -> next.getCooldown().hasExpired());
        }
        //潜在风险 unsigned!
        if(++tick==Long.MIN_VALUE){
            tick = 0; //从头开始
        }
    }
    public void handleIMythicItemTickTasks(ItemStack stack,Player player) {

        final IMythicItem imythicItem = Utils.getMythicItem(stack);
        if (imythicItem != null) {
            for (Object2IntMap.Entry<AbstractEnchantment> entry : imythicItem.getEnchantments().object2IntEntrySet()) {
                final ITickTask task = enchantTicks.get(entry.getKey().getNbtName());
                if (task == null) {
                    continue;
                }

                final int level = entry.getIntValue();
                int i = Math.max(1, task.loopTick(level)); //KleeLoveLife byZero Fix

                if (tick % i == 0) {
                    task.handle(level, player);
                }
            }
        }
    }

}