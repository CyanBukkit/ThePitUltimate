package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.actionbar.ActionBarManager;
import cn.charlotte.pit.data.PlayerProfile;
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


    ThePit instance = ThePit.getInstance();
    private long tick = 0;
    @Override
    public void run() {
        //trade
        ActionBarManager actionBarManager = instance.getActionBarManager();
        if(actionBarManager != null && tick % 5 == 0){
            actionBarManager.tick();
        }
        if(tick % 10 == 0) {
            //Async Lru Detector
            instance.getItemFactory().lru();
        }
        if(++tick==Long.MIN_VALUE){
            tick = 0; //从头开始
        }
        if(tick > 1200 && tick % 6000 ==0) {
            //AutoSave
            doAutoSave();
            return;
        }
        //Async Io Tracker
        instance.getProfileOperator().tick();
    }
    public void doAutoSave(){
        final long last = System.currentTimeMillis();
        instance.getProfileOperator().doSaveProfiles();


        final long now = System.currentTimeMillis();
        Bukkit.getLogger().info("Auto saved player backups, time: " + (now - last) + "ms");
        Bukkit.getOnlinePlayers().forEach(player -> {

            if (player.hasPermission("pit.admin")) return;
            instance.getProfileOperator().operatorStrict(player).ifPresent(operator -> {
                PlayerProfile playerProfileByUuid = operator.profile();
                if(playerProfileByUuid.getCombatTimer().hasExpired()) {
                    if (player.getLastDamageCause() != null) {
                        player.setLastDamageCause(null); //fix memory leak
                    }
                }
                final long lastActionTimestamp = playerProfileByUuid
                        .getLastActionTimestamp();
                //AntiAFK
                if (now - lastActionTimestamp >= 10 * 60 * 1000) {
                    player.sendMessage("...", true);
                    operator.pending(i -> {
                        playerProfileByUuid.setLastActionTimestamp(now);
                    });
                }
            });

        });
    }
}
