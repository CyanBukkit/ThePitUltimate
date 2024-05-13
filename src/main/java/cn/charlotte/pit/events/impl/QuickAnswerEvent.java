package cn.charlotte.pit.events.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.medal.impl.challenge.QuickMathsMedal;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.TitleUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2022/8/1<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class QuickAnswerEvent implements IEvent, INormalEvent, Listener {
    public String ask = "1+1";
    public String answer = "2";
    private final Map<Player, Boolean> alreadyAnswered = new ConcurrentHashMap<>();
    private static BukkitRunnable runnable;

    private long startTime = 0L;
    private int top = 0;

    @Override
    public String getEventInternalName() {
        return "quick_answer_event";
    }

    @Override
    public String getEventName() {
        return "&6&l速答";
    }

    @Override
    public int requireOnline() {
        return 99;
    }

    @Override
    public void onActive() {
        setTop(0);
        setStartTime(System.currentTimeMillis());
        alreadyAnswered.clear();
        CC.boardCast("&6&l速答! &7前五名在聊天栏发出答案的玩家可以获得 &6+350硬币 &b+500经验值 &7!");
        CC.boardCast("&6&l速答! &7在聊天栏里写下你的答案: &e" + ask);
        for (Player player : Bukkit.getOnlinePlayers()) {
            TitleUtil.sendTitle(player, "&6&l速答!", ("&e" + ask), 20, 20 * 5, 10);
        }
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                ThePit.getInstance()
                        .getEventFactory()
                        .inactiveEvent(ThePit.getInstance().getEventFactory().getActiveNormalEvent());
            }
        };
        runnable.runTaskLaterAsynchronously(ThePit.getInstance(), 20 * 60 * 5);
        Bukkit.getPluginManager()
                .registerEvents(this, ThePit.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        if (message.endsWith("[CANCEL]")) {
            return;
        }
        if (top >= 5) {
            return;
        }
        alreadyAnswered.putIfAbsent(e.getPlayer(), false);
        if (alreadyAnswered.get(e.getPlayer())) {
            e.getPlayer().sendMessage(CC.translate("&6&l速答! &7请先等待活动结束"));
            e.setCancelled(true);
            return;
        }
        if (message.equalsIgnoreCase(answer)) {
            if (alreadyAnswered.get(e.getPlayer())) {
                e.getRecipients().clear();
                e.getRecipients().add(e.getPlayer());
            } else {
                alreadyAnswered.put(e.getPlayer(), true);
                e.setCancelled(true);
                ++top;
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(e.getPlayer().getUniqueId());
                if (System.currentTimeMillis() - startTime <= 2.5 * 1000) {
                    new QuickMathsMedal().addProgress(profile, 1);
                }
                CC.boardCast(p -> "&6&l速答! &e#" + top + " " + profile.getFormattedName(p) + " &7在 &e" + TimeUtil.millisToRoundedTime(System.currentTimeMillis() - startTime) + " &7内回答正确!");
                profile.setCoins(profile.getCoins() + 350);
                profile.grindCoins(350);
                profile.setExperience(profile.getExperience() + 500);
                if (top >= 5) {
                    ThePit.getInstance()
                            .getEventFactory()
                            .inactiveEvent(this);
                }
            }
        }
    }

    @Override
    public void onInactive() {
        alreadyAnswered.clear();
        runnable.cancel();
        runnable = null;
        HandlerList.unregisterAll(this);
        CC.boardCast("&6&l速答! &7活动结束! 正确答案: &e" + answer);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getAsk() {
        return ask;
    }

    public QuickAnswerEvent setAsk(String ask) {
        this.ask = ask;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
