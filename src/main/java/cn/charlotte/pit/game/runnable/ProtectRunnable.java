package cn.charlotte.pit.game.runnable;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.aabb.AxisAlignedBB;
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
            if (!(profile.isEditingMode() && PlayerUtil.isStaffSpectating(player))) {
                if (!profile.isInArena()) {
                    final AxisAlignedBB aabb = new AxisAlignedBB(28, 67, -24, -25, 123, 22);
                    Location location = player.getLocation();
                    final AxisAlignedBB playerAABB = new AxisAlignedBB(location.getX(), location.getY(), location.getZ(), location.getX() + 0.8, location.getY() + 2, location.getZ() + 0.8);
                    final boolean inArena = !aabb.intersectsWith(playerAABB) || player.getLocation().getY() <= 65;
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
