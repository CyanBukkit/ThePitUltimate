package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.listener.DataListener;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/2 12:16
 */
public class AutoSaveRunnable extends BukkitRunnable {
    @Override
    public void run() {
        final long last = System.currentTimeMillis();
        PlayerProfile.saveAll();


        final long now = System.currentTimeMillis();
        Bukkit.getLogger().info("Auto saved player backups, time: " + (now - last) + "ms");
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("pit.admin")) return;
            PlayerProfile playerProfileByUuid = PlayerProfile
                    .getPlayerProfileByUuid(player.getUniqueId());
            final long lastActionTimestamp = playerProfileByUuid
                    .getLastActionTimestamp();
            //AntiAFK
            if (now - lastActionTimestamp >= 10 * 60 * 1000) {
                player.sendMessage("=w=, 你好像在挂机哦",true);
                playerProfileByUuid.setLastActionTimestamp(now);
            }
        });
    }
}
