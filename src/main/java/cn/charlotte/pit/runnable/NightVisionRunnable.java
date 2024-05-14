package cn.charlotte.pit.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author: Starry_Killer
 * @Date: 2024/1/3
 */
public class NightVisionRunnable extends BukkitRunnable {
    @Override
    public void run() {
        Bukkit.getWorlds().forEach(world -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 0, false));
                        }
                    }
                }
        );
    }
}
