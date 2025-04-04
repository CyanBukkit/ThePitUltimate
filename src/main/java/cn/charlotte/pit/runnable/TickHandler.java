package cn.charlotte.pit.runnable;
import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.Utils;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/5 0:30
 */
public class TickHandler implements Listener {
    //@Getter
    //private final static ObjectArrayList<TradeRequest> tradeRequests = new ObjectArrayList<>();

    final Map<String, ITickTask> enchantTicks = ThePit.getInstance().getEnchantmentFactor().getTickTasks();

    final Map<String, ITickTask> ticksPerk = ThePit.getInstance().getPerkFactory().getTickTasks();

    private long tick = 0;
    @SneakyThrows
    @EventHandler
    public void onTick(ServerTickEndEvent event) { //PostTick

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

                for (Map.Entry<Integer, PerkData> entry : profile.getChosePerk().entrySet()) {
                    final ITickTask task = entry.getValue().getITickTask(ticksPerk);
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
            PlayerInventory inventory = player.getInventory();
            final ItemStack leggings = inventory.getLeggings();
                if (leggings != null) {
                    profile.leggings = handleIMythicItemTickTasks(leggings,player);
                } else {
                    profile.leggings = null;
                }

                ItemStack itemInHand = inventory.getItemInHand();
                if (itemInHand != null) {
                    Material type = itemInHand.getType();
                    if (type != Material.AIR && type != Material.LEATHER_LEGGINGS && type != Material.PAPER) {
                        profile.heldItem = handleIMythicItemTickTasks(itemInHand, player);

                    } else {
                        profile.heldItem = null;
                    }
                }
        }
        //潜在风险 unsigned!
        if(++tick==Long.MIN_VALUE){
            tick = 0; //从头开始
        }
    }
    public IMythicItem handleIMythicItemTickTasks(ItemStack stack,Player player) {

        final IMythicItem imythicItem = Utils.getMythicItem(stack);

        //U can set it on your profile
        if (imythicItem != null) {
            for (Object2IntMap.Entry<AbstractEnchantment> entry : imythicItem.getEnchantments().object2IntEntrySet()) {
                final ITickTask task = enchantTicks.get(entry.getKey().getNbtName());
                if (task == null) {
                    continue;
                }

                final int level = entry.getIntValue();
                int i = Math.max(1, task.loopTick(level)); //KaMa byZero Fix

                if (tick % i == 0) {
                    task.handle(level, player);
                }
            }
        }
        return imythicItem;
    }

}