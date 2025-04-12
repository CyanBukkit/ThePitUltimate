package net.mizukilab.pit.trade;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.perk.AbstractPerk;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.mizukilab.pit.parm.AutoRegister;
import net.mizukilab.pit.runnable.ClearRunnable;

import java.util.Set;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 21:59
 */
@AutoRegister
@Getter
//What is the game used for
public class Game {

    private final Set<AbstractPerk> disabledPerks = new ObjectOpenHashSet<>();


    public void initRunnable() {
        new ClearRunnable()
                .runTaskTimer(ThePit.getInstance(), 20, 20);

        new TradeMonitorRunnable()
                .runTaskTimer(ThePit.getInstance(), 20, 20);
    }


}
