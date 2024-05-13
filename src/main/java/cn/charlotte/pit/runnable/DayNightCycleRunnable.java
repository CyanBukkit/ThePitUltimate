package cn.charlotte.pit.runnable;

import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;

public class DayNightCycleRunnable implements Runnable {
    @Override
    public void run() {
        Bukkit.getWorlds().forEach(world -> {
                    world.setTime(TimeUtil.getMinecraftTick());
                }
        );
    }
}
