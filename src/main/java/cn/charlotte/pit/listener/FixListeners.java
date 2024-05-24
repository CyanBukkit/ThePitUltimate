package cn.charlotte.pit.listener;

import cn.charlotte.pit.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class FixListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && check(clickedBlock.getType()) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
    }

    public boolean check(Material material){
        return material== Material.HOPPER || material== Material.ENDER_CHEST;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void LaunchArrowEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                if (!profile.isInArena()) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }
        }
    }
}
