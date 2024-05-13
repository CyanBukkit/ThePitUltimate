package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.TimerTask;

/**
 * @Author: EmptyIrony
 * @Date: 2021/4/20 12:45
 */
public class HolidayChecker extends TimerTask {


    @Override
    public void run() {

        try {
            Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), () -> {
                try {
                    TimeUtil.checkHoliday((result -> {
                        Bukkit.getServer().setWhitelist(!result);
                    }));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
