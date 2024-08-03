package cn.charlotte.pit.scoreboard;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.events.IScoreBoardInsert;
import cn.charlotte.pit.events.genesis.team.GenesisTeam;
import cn.charlotte.pit.events.impl.major.RagePitEvent;
import cn.charlotte.pit.perk.type.streak.tothemoon.ToTheMoonMegaStreak;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.RomanUtil;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.scoreboard.AssembleAdapter;
import cn.charlotte.pit.util.time.TimeUtil;
import dev.jnic.annotation.Include;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Scoreboard implements AssembleAdapter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(NewConfiguration.INSTANCE.getDateFormat());
    private final DecimalFormat numFormat = new DecimalFormat("0.0");
    private final DecimalFormat numFormatTwo = new DecimalFormat("0.00");
    private final DecimalFormat df = new DecimalFormat(",###,###,###,###");
    private final List<String> animationTitle =
            Arrays.asList("&d&l神话天坑",
                    "&5&l神&d&l话天坑",
                    "&f&l神&5&l话&d&l天坑",
                    "&f&l神话&5&l天&d&l坑",
                    "&f&l神话天&5&l坑",
                    "&f&l神话天坑",
                    "&d&l神话天坑",
                    "&f&l神话天坑",
                    "&d&l神话天坑",
                    "&f&l神话天坑",
                    "&d&l神话天坑",
                    "&f&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑",
                    "&d&l神话天坑");
    private long lastAnimationTime = 0;
    private int animationTick = 0;

    @Override
    public String getTitle(Player player) {
        String text = animationTitle.get(animationTick);
        if (System.currentTimeMillis() - lastAnimationTime >= 125) {
            animationTick++;
            if (animationTick + 1 >= animationTitle.size()) {
                animationTick = 0;
            }
            lastAnimationTime = System.currentTimeMillis();
        }

        return text;
    }

    @Override
    public List<String> getLines(Player player) {

        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        List<String> lines = new ObjectArrayList<>(16);

        if (!profile.isLoaded()) {
            lines.add("");
            lines.add("&c正在加载您的档案...");
            lines.add("&c请稍等片刻...");
            lines.add("");
            lines.add("&c如等待长时仍在加载,");
            lines.add("&c请重新进入服务器.");
            lines.add("");
            lines.add("&c公告群: ");
            lines.add("&e425831669");
            lines.add("");
            lines.add("&enyacho.cn");
            return lines;
        }

        int prestige = profile.getPrestige();
        int level = profile.getLevel();

        if (NewConfiguration.INSTANCE.getScoreboardShowtime()) {
            lines.add("&7" + dateFormat.format(System.currentTimeMillis()) + " &8" + ThePit.getInstance().getServerId());
        }

        if (ThePit.getInstance().getEventFactory().getActiveEpicEvent() != null) {
            IEvent event = (IEvent) ThePit.getInstance().getEventFactory().getActiveEpicEvent();
//            lines.add("&f事件: &c" + event.getEventName());
            lines.add(" ");
            lines.add("&f事件: &6" + event.getEventName());
            if (event.getEventInternalName().equals("rage_pit")) {
                lines.add("&f剩余: &a" + TimeUtil.millisToTimer(RagePitEvent.getTimer().getRemaining()));
                if (RagePitEvent.getDamageMap().get(player.getUniqueId()) != null) {
                    int damage = (int) (RagePitEvent.getDamageMap().get(player.getUniqueId()).getDamage() / 2);
                    int rank = RagePitEvent.getDamageRank(player);
                    lines.add("&f伤害: &c" + damage + "❤ &7(#" + rank + ")");
                }
                int killed = RagePitEvent.getKilled();

                lines.add("&f全部击杀: " + (killed >= 600 ? "&a" : "&c") + killed + "&7/600");
            } else if (event instanceof IScoreBoardInsert) {

                IScoreBoardInsert insert = (IScoreBoardInsert) event;
                lines.addAll(insert.insert(player));

            }
        } else if (ThePit.getInstance().getEventFactory().getActiveNormalEvent() != null) {
            INormalEvent event = ThePit.getInstance().getEventFactory().getActiveNormalEvent();
            if (event instanceof IScoreBoardInsert) {
                IScoreBoardInsert insert = (IScoreBoardInsert) event;
                lines.add(" ");
                lines.add("&f事件: &a" + ((IEvent) event).getEventName());
                lines.addAll(insert.insert(player));
            }
        }
        lines.add("");

        String genesisTeam = "";
        if (ThePit.getInstance().getPitConfig().isGenesisEnable() && profile.getGenesisData().getTeam() != GenesisTeam.NONE) {
            if (profile.getGenesisData().getTeam() == GenesisTeam.ANGEL) {
                genesisTeam = " &b♆";
            }
            if (profile.getGenesisData().getTeam() == GenesisTeam.DEMON) {
                genesisTeam = " &c♨";
            }
        }
        if (prestige > 0 ) {
            lines.add("&f精通: &e" + RomanUtil.convert(prestige));
        }
        lines.add("&f等级: " + LevelUtil.getLevelTag(prestige, level) + genesisTeam);

        if (level >= 120) {
            lines.add("&f经验值: &b经验值已满!");
        } else {
            lines.add("&f下一级: &b" + numFormatTwo.format((LevelUtil.getLevelTotalExperience(prestige, level + 1) - profile.getExperience())) + " 经验值");
        }

        if (profile.getCurrentQuest() != null) {
            lines.add(" ");
            lines.add("&f击杀玩家: &a" + profile.getCurrentQuest().getCurrent() + "/" + profile.getCurrentQuest().getTotal());
            if (profile.getCurrentQuest().getCurrent() == profile.getCurrentQuest().getTotal()) {
                lines.add("&f剩余时间: &a已完成");
            } else {
                if (profile.getCurrentQuest().getEndTime() > System.currentTimeMillis()) {
                    lines.add("&f剩余时间: &a" + TimeUtil.millisToTimer(profile.getCurrentQuest().getEndTime() - System.currentTimeMillis()));
                } else {
                    lines.add("&f剩余时间: &c已超时");
                }
            }
        }

        lines.add(" ");
        if (profile.getCoins() >= 10000) {
            lines.add("&f硬币: &6" + df.format(profile.getCoins()));
        } else {
            lines.add("&f硬币: &6" + numFormatTwo.format(profile.getCoins()));
        }
        //if Player is in Fight:
        boolean statusToggle = true;
        if (ThePit.getInstance().getEventFactory().getActiveEpicEvent() == null) {
            if (ThePit.getInstance().getEventFactory().getActiveNormalEvent() != null) {
                INormalEvent event = ThePit.getInstance().getEventFactory().getActiveNormalEvent();
                statusToggle = !(event instanceof IScoreBoardInsert);
            }
        } else {
            statusToggle = false;
        }
        if (statusToggle) {
            lines.add(" ");
            final String currentStreak = PlayerUtil.getActiveMegaStreak(player);
            if (currentStreak != null) {
                lines.add("&f状态: " + currentStreak);
            } else {
                lines.add("&f状态: " + (profile.getCombatTimer().hasExpired()
                        ? "&a不在占坑中" : "&c占坑中" + (profile.getCombatTimer().getRemaining() / 1000D <= 5
                        ? "&7 (" + numFormat.format(profile.getCombatTimer().getRemaining() / 1000D) + ")"
                        : (profile.getBounty() != 0
                        ? "&7 (" + numFormat.format(profile.getCombatTimer().getRemaining() / 1000D) + ")"
                        : "")))); // status: 占坑中 (%duration%秒) / 不在占坑中
            }
            if (!profile.getCombatTimer().hasExpired()) {
                lines.add("&f连杀: &a" + numFormat.format(profile.getStreakKills()));
            }

            if (CC.translate("&b月球之旅").equals(currentStreak)) {
                Double storedExp = ToTheMoonMegaStreak.getCache().get(player.getUniqueId());
                if (storedExp == null) {
                    storedExp = 0.0;
                }
                final double streakKills = profile.getStreakKills();
                final double multiple = Math.min(1.0, (streakKills - 100) * 0.005);

                lines.add("&f已储经验: &b" + df.format(storedExp) + "&7 (&a" + numFormat.format(multiple) + "x&7)");
            }
        }
        //if Player have a bounty:
        if (profile.getBounty() != 0) {
            String genesisColor = profile.bountyColor();
            lines.add("&f赏金: " + genesisColor + "&l" + profile.getBounty() + "g");
        }
        //Damage reduce caused by Perks
        if (profile.getStrengthNum() > 0 && !profile.getStrengthTimer().hasExpired()) {
            lines.add("&f力量: &c+" + profile.getStrengthNum() * 4 + "% &7 (" + numFormat.format(profile.getStrengthTimer().getRemaining() / 1000D) + ")");
        }

        boolean gladiator = false;
        for (Map.Entry<Integer, PerkData> entry : profile.getChosePerk().entrySet()) {
            if (entry.getValue().getPerkInternalName().equals("Gladiator")) {
                gladiator = true;
                break;
            }
        }
        if (gladiator && profile.isInArena()) {

            double boost = PlayerUtil.getNearbyPlayers(player.getLocation(), 8).size();

            int sybilLevel = Utils.getEnchantLevel(player.getInventory().getLeggings(), "sybil");
            if (sybilLevel > 0) {
                boost += sybilLevel + 1;
            }

            if (boost > 10) {
                boost = 10;
            }
            if (boost >= 3) {
                lines.add("&f角坑士: &9-" + boost * 3 + "%");
            }

        }

        lines.add(" ");
        if (ThePit.getInstance().getRebootRunnable().getCurrentTask() != null) {
            lines.add("&c房间即将重启! &7(" + TimeUtil.millisToRoundedTime(ThePit.getInstance().getRebootRunnable().getCurrentTask().getEndTime() - System.currentTimeMillis()).replace(" ", "") + "后)");
        }
        if (ThePit.isDEBUG_SERVER()) {
            lines.add("&eTEST " + (ThePit.getInstance().getPitConfig().isDebugServerPublic() ? "&a#Public" : "&c#Private"));
        } else {
            lines.add("&enyacho.cn");
        }
        return lines;
    }
}
