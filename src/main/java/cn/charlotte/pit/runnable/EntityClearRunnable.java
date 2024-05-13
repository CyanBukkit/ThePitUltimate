package cn.charlotte.pit.runnable;

import net.minecraft.server.v1_8_R3.EntityArrow;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 2022/7/27<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class EntityClearRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Arrow arrow : world.getEntitiesByClass(Arrow.class)) {
                if (arrow.getTicksLived() >= 20 * 10) {
                    arrow.remove();
                }
            }
        }
    }
}
