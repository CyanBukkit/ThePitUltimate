package cn.charlotte.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.util.ServerAddress;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.level.LevelUtil;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * 2022/8/10<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class TipRunnable extends BukkitRunnable {
    static List<String> messages = Lists.newArrayList(
            "{coin}",
            "官方交流群: " + "903167283",
            "发现了Bug? 点击加入天坑乱斗交流群反馈!",
            "天坑会员时效与§6MVP§c+§a会员一致",
            "官方交流群: " +  "903167283",
            "低于" + LevelUtil.getLevelTag(0, PlayerProfile.PROTECT_LEVEL) + "§a属于新手保护阶段哦~",
            "购买会员(§6MVP§c+§a)后可在商店右下角领取天坑会员"
    );
    private int index;

    @Override
    public void run() {
        if (index >= messages.size()) {
            index = 0;
        }
        String s = messages.get(index);
        ++index;
        if ("{coin}".equals(s)) {
            s = "&6+10.0 硬币";
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                if (!profile.isLoaded()) {
                    continue;
                }
                if (!profile.getCombatTimer().hasExpired()) {
                    continue;
                }
                profile.setCoins(profile.getCoins() + 10);
                profile.grindCoins(10);
                CC.send(null, player, "&a&lTip! &a" + s);
            }
            return;
        } else if (s.contains("群")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ThePit.getInstance().getAudiences().player(player)
                        .sendMessage(
                                Component.text(CC.translate("&a&lTip! &a" + s))
                                        .clickEvent(ClickEvent.suggestCommand("903167283"))
                                        .hoverEvent(Component.text("点击获得群号"))
                        );
            }
            return;
        }
        CC.boardCast("&a&lTip! &a" + s);
    }
}
