package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.actionbar.ActionBarManager;
import cn.charlotte.pit.data.temp.TradeRequest;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
        ActionBarManager actionBarManager = ThePit.getInstance().getActionBarManager();
        if(actionBarManager != null && tick % 5 == 0){
            actionBarManager.tick();
        }
        if(++tick==Long.MIN_VALUE){
            tick = 0; //从头开始
        }
    }
}
