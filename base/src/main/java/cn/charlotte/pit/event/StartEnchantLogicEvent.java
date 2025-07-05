package cn.charlotte.pit.event;

import cn.hutool.core.lang.func.Consumer3;
import lombok.Getter;
import lombok.Setter;
import net.mizukilab.pit.item.AbstractPitItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
@Getter
@Setter
public class StartEnchantLogicEvent extends PitEvent implements Cancellable {
    Player player;
    boolean cancelled;
    Consumer3<ItemStack, AbstractPitItem, Player> consumer;
    public StartEnchantLogicEvent(Player player){
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
