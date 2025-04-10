package cn.charlotte.pit.buff.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.buff.AbstractPitBuff;
import cn.charlotte.pit.event.PitStackBuffEvent;
import cn.charlotte.pit.parm.AutoRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

@AutoRegister
public class SiltedUpBuuff extends AbstractPitBuff implements Listener {

    @Override
    public String getInternalBuffName() {
        return "pin_down_de_buff";
    }

    @Override
    public String getDisplayName() {
        return "&2阻滞";
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList("&7无法受到与被施加 &b速度 &7与 &a跳跃提升 &7效果");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBuffStack(PitStackBuffEvent pitStackBuffEvent) {
        if (pitStackBuffEvent.isCancel() || !pitStackBuffEvent.getBuff().getInternalBuffName().equalsIgnoreCase(getInternalBuffName()))
            return;
        if (getPlayerBuffData(pitStackBuffEvent.getPlayer()).getTier() < 1)
            (new rundebuff(pitStackBuffEvent)).runTaskTimer(ThePit.getInstance(), 0L, 10L);
    }

    class rundebuff extends BukkitRunnable {

        Player player;

        rundebuff(PitStackBuffEvent event) {
            player = event.getPlayer();
        }

        public void run() {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.JUMP);
            if (getPlayerBuffData(player).getTier() < 1) {
                cancel();
            }
        }
    }
}
