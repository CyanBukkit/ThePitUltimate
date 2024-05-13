package cn.charlotte.pit.util.scoreboard.v2;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.util.scoreboard.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2022/7/24<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class ScoreboardManager implements Listener {
    private AssembleAdapter adapter;
    private Map<Player, Board> boards;
    private BukkitRunnable runnable;

    public ScoreboardManager(AssembleAdapter adapter) {
        this.adapter = adapter;
        this.boards = new ConcurrentHashMap<>(10);
        Bukkit.getPluginManager().registerEvents(this, ThePit.getInstance());
    }

    public void start() {
        if (runnable != null) {
            runnable.cancel();
        }
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        };
        runnable.runTaskTimerAsynchronously(ThePit.getInstance(), 1, 1);
    }

    public void stop() {
        if (runnable != null) {
            runnable.cancel();
            runnable = null;
        }
    }

    public void add(Player player) {
        boards.put(player, new Board(player));
    }

    public void remove(Player player) {
        Board remove = boards.remove(player);
        if (remove != null) {
            remove.remove();
        }
    }

    public void update() {
        for (Map.Entry<Player, Board> entry : boards.entrySet()) {
            Player player = entry.getKey();
            entry.getValue().send(adapter.getTitle(player), adapter.getLines(player));
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        add(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        remove(e.getPlayer());
    }
}
