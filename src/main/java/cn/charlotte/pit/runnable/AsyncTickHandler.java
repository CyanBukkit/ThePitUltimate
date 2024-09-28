package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.actionbar.ActionBarManager;
import cn.charlotte.pit.data.operator.PackedOperator;
import cn.charlotte.pit.data.temp.TradeRequest;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Async tick handler
 */
public class AsyncTickHandler extends BukkitRunnable implements Listener {

    private long tick = 0;
    @Override
    public void run() {
        //trade
        ThePit instance = ThePit.getInstance();
        ActionBarManager actionBarManager = instance.getActionBarManager();
        if(actionBarManager != null && tick % 5 == 0){
            actionBarManager.tick();
        }
        if(tick % 10 == 0) {
            //Async Lru Detector
            instance.getItemFactory().lru();
        }
        //Async Io Tracker
        instance.getProfileOperator().tick();
        if(++tick==Long.MIN_VALUE){
            tick = 0; //从头开始
        }
    }
}
