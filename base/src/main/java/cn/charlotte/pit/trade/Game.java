package cn.charlotte.pit.trade;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.runnable.ClearRunnable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;

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
