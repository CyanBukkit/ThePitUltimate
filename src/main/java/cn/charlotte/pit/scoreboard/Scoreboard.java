package cn.charlotte.pit.scoreboard;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.events.IEpicEvent;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.events.IScoreBoardInsert;
import cn.charlotte.pit.events.genesis.team.GenesisTeam;
import cn.charlotte.pit.events.impl.major.RagePitEvent;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.perk.type.streak.tothemoon.ToTheMoonMegaStreak;
import cn.charlotte.pit.util.FuncsKt;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.RomanUtil;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.random.RandomUtil;
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
            Arrays.asList("&6&l传奇 &0: &e&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&6&l传奇 &0: &e&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗",
                    "&f&l传&6&l奇 &0: &e&l乱斗",
                    "&e&l传&f&l奇 &0: &6&l乱斗",
                    "&6&l传&e&l奇 &0: &f&l乱斗"
            );
    private final List<String> blacksAnimationTitle =
            Arrays.asList("&d&l小鸟乱斗",
                    "&5&l小&d&l鸟乱斗",
                    "&f&l小&5&l鸟&d&l乱斗",
                    "&f&小鸟&5&l乱&d&斗",
                    "&f&l小鸟乱&5&l斗",
                    "&f&l小鸟乱斗",
                    "&d&l小鸟乱斗",
                    "&f&l小鸟乱斗",
                    "&d&l小鸟乱斗",
                    "&f&l小鸟乱斗",
                    "&d&l小鸟乱斗",
                    "&f&l小鸟乱斗",
                    "&d&l小鸟乱斗",
                    "&a&l小鸟乱斗",
                    "&b&l小鸟乱斗",
                    "&c&l小鸟乱斗",
                    "&d&l小鸟乱斗",
                    "&1&l小鸟乱斗",
                    "&2&l小鸟乱斗",
                    "&3&l小鸟乱斗",
                    "&4&l小鸟乱斗",
                    "&5&l小鸟乱斗",
                    "&6&l小鸟乱斗",
                    "&7&l小鸟乱斗",
                    "&8&l小鸟乱斗",
                    "&9&l小鸟乱斗");

    private long lastAnimationTime = 0;
    private int animationTick = 0;

    @Override
    public String getTitle(Player player) {
        List<String> title;
     /*   if (FuncsKt.isSpecial(player)){
            title = blacksAnimationTitle;
        }else {*/
        title = animationTitle;

        String text = title.get(animationTick);
        if (System.currentTimeMillis() - lastAnimationTime >= 250) {
            animationTick++;
            if (animationTick + 1 >= title.size()) {
                animationTick = 0;
            }
            lastAnimationTime = System.currentTimeMillis();
        }

        return text;
    }

    private static final List<String> LOADING = new ObjectArrayList<>(List.of("", "&c正在加载档案...", "&c请稍等片刻...", ""
            , "&c如长时仍在加载,"
            , "&c请重新进入服务器.", "", "&c公告群: &e425831669", "", "&enyacho.cn"));
    private final ObjectArrayList<String> carrierList = new ObjectArrayList<>(16);

    @Override
    public List<String> getLines(Player player) {

        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (!profile.isLoaded()) {
            return LOADING;
        }
        List<String> lines = carrierList;
        lines.clear();

        int prestige = profile.getPrestige();
        int level = profile.getLevel();

        long currentSystemTime = System.currentTimeMillis();
        if (NewConfiguration.INSTANCE.getScoreboardShowtime()) {
            lines.add("&7" +
                    dateFormat.format(currentSystemTime) + " &8" + ThePit.getInstance().getServerId());
        }

        IEpicEvent activeEpicEvent = ThePit.getInstance().getEventFactory().getActiveEpicEvent();
        INormalEvent activeNormalEvent = ThePit.getInstance().getEventFactory().getActiveNormalEvent();
        if (activeEpicEvent != null) {
            IEvent event = (IEvent) activeEpicEvent;
//            lines.add("&f事件: &c" + event.getEventName());
            lines.add(" ");
            lines.add("&f事件: &6" + event.getEventName());
            if (event instanceof RagePitEvent ragePit) {
                lines.add("&f剩余: &a" + TimeUtil.millisToTimer(ragePit.getTimer().getRemaining()));
                if (ragePit.getDamageMap().get(player.getUniqueId()) != null) {
                    int damage = (int) (ragePit.getDamageMap().get(player.getUniqueId()).getDamage() / 2);
                    int rank = ragePit.getDamageRank(player);
                    lines.add("&f伤害: &c" + damage + "❤ &7(#" + rank + ")");
                }
                int killed = ragePit.getKilled();

                lines.add("&f总击杀: " + (killed >= 600 ? "&a" : "&c") + killed + "&7/600");
            } else if (event instanceof IScoreBoardInsert insert) {

                List<String> insert1 = insert.insert(player);
                if (insert1 != null) {
                    lines.addAll(insert1);
                }

            }
        } else if (activeNormalEvent != null) {
            if (activeNormalEvent instanceof IScoreBoardInsert insert) {
                lines.add(" ");
                lines.add("&f事件: &a" + ((IEvent) activeNormalEvent).getEventName());
                List<String> insert1 = insert.insert(player);
                if (insert1 != null) {
                    lines.addAll(insert1);
                }
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
        if (prestige > 0) {
            lines.add("&f精级: &e" + RomanUtil.convert(prestige) + " " + LevelUtil.getLevelTagWithOutAnyPS(level) + genesisTeam);
        } else {
            lines.add("&f等级: " + LevelUtil.getLevelTagWithOutAnyPS(level) + genesisTeam);
        }
        if (level >= 120) {
            lines.add("&f经验: &b满");
        } else {
            lines.add("&f下级: &b" + numFormatTwo.format((LevelUtil.getLevelTotalExperience(prestige, level + 1) - profile.getExperience())) + " Ex");
        }

        if (profile.getCurrentQuest() != null) {
            lines.add(" ");
            lines.add("&f击杀: &a" + profile.getCurrentQuest().getCurrent() + "/" + profile.getCurrentQuest().getTotal());
            if (profile.getCurrentQuest().getCurrent() == profile.getCurrentQuest().getTotal()) {
                lines.add("&f剩时: &a完成");
            } else {
                if (profile.getCurrentQuest().getEndTime() > currentSystemTime) {
                    lines.add("&f剩时: &a"
                            + TimeUtil.millisToTimer(
                            profile.getCurrentQuest().getEndTime() - currentSystemTime));
                } else {
                    lines.add("&f剩时: &c超时");
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
        if (activeEpicEvent == null) {
            if (activeNormalEvent != null) {
                statusToggle = !(activeNormalEvent instanceof IScoreBoardInsert);
            }
        } else {
            statusToggle = false;
        }
        boolean sultKill = false;
        if (statusToggle) {
            final AbstractPerk currentStreak = PlayerUtil.getActiveMegaStreakObj(player);
            boolean b = profile.getCombatTimer().hasExpired();
            if (!b) {
                lines.add(" ");
            }
            if (currentStreak != null) {
                lines.add("&f状态: " + CC.translate(currentStreak.getDisplayName()));
            } else {
                if (!b) {

                    String combatTimerFormatted = numFormat.format(profile.getCombatTimer().getRemaining() / 1000D);
                    lines.add("&f状态: &c凹坑中" + (profile.getCombatTimer().getRemaining() / 1000D <= 5
                            ? "&7 " + combatTimerFormatted
                            : (profile.getBounty() != 0
                            ? "&7 " + combatTimerFormatted
                            : ""))); // status: 占坑中 (%duration%秒) / 不在占坑中
                }
            }
            if (!b) {

                String e;
                if (profile.getBounty() != 0) {
                    String genesisColor = profile.bountyColor();
                    e = "&f连赏: &a" + numFormat.format(profile.getStreakKills()) + " " + genesisColor + "&l" + profile.getBounty() + "g";
                } else {
                    e = "&f连杀: &a" + numFormat.format(profile.getStreakKills());

                }
                lines.add(e);
                sultKill = true;
            }

            if (currentStreak == ToTheMoonMegaStreak.getInstance()) {
                Double storedExp = ToTheMoonMegaStreak.getCache().get(player.getUniqueId());
                if (storedExp == null) {
                    storedExp = 0.0;
                }
                final double streakKills = profile.getStreakKills();
                final double multiple = Math.min(1.0, (streakKills - 100) * 0.005);

                lines.add("&f已储: &b" + df.format(storedExp) + "&7 (&a" + numFormat.format(multiple) + "x&7)");
            }
        }
        //if Player have a bounty:
        if (!sultKill) {
            if (profile.getBounty() != 0) {
                String genesisColor = profile.bountyColor();
                if (profile.getStreakKills() < 1D) {
                    lines.add("&f赏金: " + genesisColor + "&l" + profile.getBounty() + "g");
                } else {
                    lines.add("&f连赏: &a" + numFormat.format(profile.getStreakKills()) + " " + genesisColor + "&l" + profile.getBounty() + "g");

                }
            }
        }
        //Damage reduce caused by Perks
        if (profile.getStrengthNum() > 0 && !profile.getStrengthTimer().hasExpired()) {
            lines.add("&f力量: &c+" + profile.getStrengthNum() * 4 + "% &7 (" + numFormat.format(profile.getStrengthTimer().getRemaining() / 1000D) + ")");
        }

        boolean gladiator = profile.isChoosePerk("Gladiator");

        if (gladiator && profile.isInArena()) {
            int boost = 0;
            try {
                boost = PlayerUtil.getNearbyPlayers(player.getLocation(), 8).size();
            } catch (Exception ignored) {
            }
            int sybilLevel = Utils.getEnchantLevel(profile.leggings, "sybil");
            if (sybilLevel > 0) {
                boost += sybilLevel + 1;
            }

            if (boost > 10) {
                boost = 10;
            }
            if (boost >= 3) {
                lines.add("&f脚坑士: &9-" + boost * 3 + "%");
            }

        }

        lines.add(" ");
        if (ThePit.getInstance().getRebootRunnable().getCurrentTask() != null) {
            lines.add("&c重启! &7" + TimeUtil.millisToRoundedTime(ThePit.getInstance().getRebootRunnable().getCurrentTask().getEndTime() - currentSystemTime).replace(" ", "") + "后");
        }
        if (ThePit.isDEBUG_SERVER()) {
            lines.add("&3测试 " + (ThePit.getInstance().getPitConfig().isDebugServerPublic() ? "&a#Public" : "&c#Private"));
        } else if (FuncsKt.isSpecial(player)) {
        } else {
            lines.add("&enyacho.cn");
        }
        return lines;
    }
}
