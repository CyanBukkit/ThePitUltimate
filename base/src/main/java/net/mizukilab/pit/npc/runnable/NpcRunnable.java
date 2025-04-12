package net.mizukilab.pit.npc.runnable;

import net.mizukilab.pit.npc.AbstractPitNPC;
import net.mizukilab.pit.npc.NpcFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author EmptyIrony
 * @date 2021/1/1 21:09
 */
public class NpcRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (AbstractPitNPC pitNpc : NpcFactory.getPitNpc()) {
                pitNpc.getNpc().setText(player, pitNpc.getNpcTextLine(player));
            }
        }
    }

}
