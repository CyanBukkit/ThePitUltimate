package cn.charlotte.pit.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.potion.PotionEffect;

/**
 * 2022/10/18<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@Getter
@Setter
public class PitPotionEffectEvent extends PitEvent implements Cancellable {
    private final Player player;
    private PotionEffect potionEffect;
    private boolean cancelled = false;

    public PitPotionEffectEvent(Player player, PotionEffect potionEffect) {
        this.player = player;
        this.potionEffect = potionEffect;
    }
}
