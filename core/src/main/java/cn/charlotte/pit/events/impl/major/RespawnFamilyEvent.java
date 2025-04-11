package cn.charlotte.pit.events.impl.major;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.event.ItemLiveDropEvent;
import cn.charlotte.pit.events.IEpicEvent;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.IScoreBoardInsert;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RespawnFamilyEvent implements IEvent, IEpicEvent, Listener, IScoreBoardInsert {

    private Cooldown timer;
    private BukkitRunnable runnable;

    @Override
    public String getEventInternalName() {
        return "respawn_family";
    }

    @Override
    public String getEventName() {
        return "活全家";
    }

    @Override
    public int requireOnline() {
        return NewConfiguration.INSTANCE.getEventOnlineRequired().get(getEventInternalName());
    }

    @EventHandler
    public void onItemLiveDrop(ItemLiveDropEvent e) {

        e.setCancelled(true);
    }

    @Override
    public void onActive() {
        this.timer = new Cooldown(5, TimeUnit.MINUTES);
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer.hasExpired()) {
                    cancel();
                    if (RespawnFamilyEvent.this.equals(ThePit.getInstance().getEventFactory().getActiveEpicEvent())) {
                        Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                            ThePit.getInstance()
                                    .getEventFactory()
                                    .inactiveEvent(RespawnFamilyEvent.this);
                        });
                    }
                }
            }
        };
        this.runnable.runTaskTimer(ThePit.getInstance(), 20, 10);

        CC.boardCast(MessageType.EVENT, "&a&l大型事件! &7所有人都活全家了, 不掉毛, 不掉命。");
        Bukkit.getPluginManager().registerEvents(this, ThePit.getInstance());
    }

    @Override
    public void onInactive() {
        HandlerList.unregisterAll(this);

    }

    @Override
    public List<String> insert(Player player) {
        return List.of("&f剩余时间: &a" + TimeUtil.millisToTimer(timer.getRemaining()), "&c不存在掉命掉毛现象");
    }
}
