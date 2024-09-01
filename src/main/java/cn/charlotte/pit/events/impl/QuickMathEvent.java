package cn.charlotte.pit.events.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.medal.impl.challenge.QuickMathsMedal;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.TitleUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.homo.HomoGenerator;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/7 11:08
 */
public class QuickMathEvent implements IEvent, INormalEvent, Listener {
    public String TheEquation;
    public String TheEquationQuests;
    private final Set<UUID> alreadyAnswered = new ConcurrentHashSet<>();

    private long startTime = 0L;
    private int top = 0;
    public QuickMathEvent(String eqq,String ans){
        TheEquationQuests = eqq;
        TheEquation = ans;
    }
    private boolean ended = false;
    public QuickMathEvent(){

    }
    @Override
    public String getEventInternalName() {
        return "quick_math_event";
    }

    @Override
    public String getEventName() {
        return "&9&l速算";
    }

    @Override
    public int requireOnline() {
        return NewConfiguration.INSTANCE.getEventOnlineRequired().get(getEventInternalName());
    }

    @Override
    public void onActive() {
        Cooldown normalEnd = ThePit.getInstance().getEventFactory().getNormalEnd();
        HomoGenerator homoGenerator = HomoGenerator.getGeneratorInst();
        try {
            if(TheEquation == null || TheEquationQuests == null) {
                int homo = ThreadLocalRandom.current().nextInt(1919);
                this.TheEquationQuests = homoGenerator.homo(homo);
                this.TheEquation = String.valueOf(homo);
            }

            } catch (Exception e) {
            e.printStackTrace();
            ThePit.getInstance()
                    .getEventFactory()
                    .safeInactiveEvent(this);
        }
        setTop(0);
            setStartTime(System.currentTimeMillis());
            alreadyAnswered.clear();
            CC.boardCast("&5&l速算! &7前五名在聊天栏发出答案的玩家可以获得 &6+520硬币 &b+100%经验值 &7!");
            CC.boardCast("&5&l速算! &7在聊天栏里写下你的答案: &e" + TheEquationQuests);
            for (Player player : Bukkit.getOnlinePlayers()) {
                TitleUtil.sendTitle(player, "&5&l速算!", ("&e" + TheEquationQuests), 20, 20 * 5, 10);
            }
            Bukkit.getPluginManager()
                    .registerEvents(this, ThePit.getInstance());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().contains(TheEquation)) {
            if (!alreadyAnswered.add(e.getPlayer().getUniqueId())) {
                e.getRecipients().clear();
                e.getRecipients().add(e.getPlayer());
            } else {
                e.setCancelled(true);
                top++;
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(e.getPlayer().getUniqueId());
                if (System.currentTimeMillis() - startTime <= 2.5 * 1000) {
                    new QuickMathsMedal().addProgress(profile, 1);
                }
                CC.boardCast("&5&l速算! &e#" + top + " " + profile.getFormattedName() + " &7在 &e" + TimeUtil.millisToRoundedTime(System.currentTimeMillis() - startTime) + " &7内回答正确!");
                profile.setCoins(profile.getCoins() + 520);
                profile.grindCoins(520);
                profile.setExperience(profile.getExperience() + LevelUtil.getLevelExpRequired(profile.getPrestige() ,profile.getLevel()));
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
        ended = true;
        HandlerList.unregisterAll(this);
        CC.boardCast("&5&l速算! &7活动结束! 正确答案: &e" + TheEquation);
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
}
