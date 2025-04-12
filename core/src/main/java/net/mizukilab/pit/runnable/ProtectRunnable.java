package net.mizukilab.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import net.mizukilab.pit.config.PitConfig;
import net.mizukilab.pit.util.BlockUtil;
import net.mizukilab.pit.util.PlayerUtil;
import net.mizukilab.pit.util.aabb.AABB;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/27 15:16
 */
public class ProtectRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (!profile.isEditingMode() || !PlayerUtil.isStaffSpectating(player)) {
                if (!profile.isInArena()) {
                    PitConfig config = ThePit.getInstance().getPitConfig();
                    final AABB aabb = new AABB(config.getPitLocA().getX(), config.getPitLocA().getY(), config.getPitLocA().getZ(), config.getPitLocB().getX(), config.getPitLocB().getY(), config.getPitLocB().getZ());

                    Location location = player.getLocation();
                    final AABB playerAABB = new AABB(location.getX(), location.getY(), location.getZ(), location.getX() + 0.8, location.getY() + 2, location.getZ() + 0.8);
                    final boolean inArena = !aabb.intersectsWith(playerAABB);
                    if (player.getGameMode() == GameMode.ADVENTURE) {
                        if (BlockUtil.isBlockNearby(player.getLocation(), 5)) {
                            player.setGameMode(GameMode.SURVIVAL);
                            return;
                        }
                    }
                    if (inArena) {
                        profile.setInArena(true);
                        continue;
                    }

                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        player.setGameMode(GameMode.ADVENTURE);
                    }
                } else {
                    if (player.getGameMode() == GameMode.ADVENTURE) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                }
            }
        }
    }
}
