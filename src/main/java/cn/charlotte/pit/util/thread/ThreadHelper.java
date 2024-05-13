package cn.charlotte.pit.util.thread;

import cn.charlotte.pit.ThePit;
import org.bukkit.Bukkit;

/**
 * 2022/7/25<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public interface ThreadHelper {
    default void async(Runnable runnable) {
        Async(runnable);
    }

    static void Async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), runnable);
    }

    default void sync(Runnable runnable) {
        Sync(runnable);
    }

    default void sync(Runnable runnable, int tick) {
        Sync(runnable, tick);
    }

    static void Sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(ThePit.getInstance(), runnable);
    }

    static void Sync(Runnable runnable, int tick) {
        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), runnable, tick);
    }
}
