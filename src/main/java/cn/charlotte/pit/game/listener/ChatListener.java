package cn.charlotte.pit.game.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.genesis.team.GenesisTeam;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.rank.RankUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/1 11:31
 */
@AutoRegister
public class ChatListener implements Listener {

    public static final HashMap<UUID, Cooldown> cooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!CC.canPlayerSeeMessage(p, MessageType.CHAT)) {
                event.getRecipients().remove(p);
                if (p.getName().equals(player.getName())) {
                    event.setCancelled(true);
                    event.setMessage(event.getMessage() + "[CANCEL]");
                    player.sendMessage(CC.translate("&c你关闭了聊天消息显示,因此你也无法发送聊天消息!"));
                    player.sendMessage(CC.translate("&c使用指令 &f/settings &c调整游戏选项."));
                    return;
                }
            }
        }
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

        if (!cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired() && !PlayerUtil.isStaff(player)) {
            event.setCancelled(true);
            event.setMessage(event.getMessage() + "[CANCEL]");
            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&c慢速模式已开启,再次发送聊天信息前请等待3秒!"));
            player.sendMessage(CC.translate("&c请注意,短时间内重复发送相同聊天信息会被禁止发言!"));
            player.sendMessage(CC.CHAT_BAR);
            return;
        }

        for (Player viewer : event.getRecipients()) {
            String tag = getPlayerTag(profile);

            String rank = RankUtil.getPlayerRank(player.getUniqueId());
            String senderName = player.getName();
            if (profile.isNewPlayer() && !profile.isFriend(viewer.getUniqueId()) && profile.getProtectName() != null) {
                rank = "§k";
                senderName = profile.getProtectName();
                if (profile.getProtectTag() != null) {
                    senderName = profile.getProtectName() + " §r§7" + profile.getProtectTag();
                }
            }

            TextComponent userName = getUserNameCompo(player, profile, viewer, tag, rank, senderName);
            Audience audience = ThePit.getInstance().getAudiences().player(viewer);
            sendMessage(event, player, profile, viewer, userName, audience);
        }

        //额外发送给控制台一份
        {
            String tag = getPlayerTag(profile);

            String rank = RankUtil.getPlayerRank(player.getUniqueId());
            String senderName = player.getName();

            TextComponent userName = getUserNameCompo(player, profile, null, tag, rank, senderName);
            Audience audience = ThePit.getInstance().getAudiences().console();
            sendMessage(event, player, profile, null, userName, audience);
        }
        profile.setLastActionTimestamp(System.currentTimeMillis());
        cooldown.put(player.getUniqueId(), new Cooldown(3, TimeUnit.SECONDS));
        event.setCancelled(true);
    }

    private String getPlayerTag(PlayerProfile profile) {
        String tag = profile.getFormattedLevelTagWithRoman();
        if (ThePit.getInstance().getPitConfig().isGenesisEnable()) {
            if (profile.getGenesisData().getTeam() == GenesisTeam.ANGEL) {
                tag = "&b♆ " + tag;
            }
            if (profile.getGenesisData().getTeam() == GenesisTeam.DEMON) {
                tag = "&c♨ " + tag;
            }
        }
        return tag;
    }

    @NotNull
    private TextComponent getUserNameCompo(Player player, PlayerProfile profile, Player viewer, String tag, String rank, String senderName) {
        TextComponent userName = Component.text(
                CC.translate(tag + " " +
                        rank + RankUtil.getPlayerRankColor(player.getUniqueId(), viewer) + senderName
                )
        );
        if (profile.getPlayerOption().isProfileVisibility() && viewer != null && profile.isFriend(viewer.getUniqueId())) {
            userName = userName.hoverEvent(Component.text(
                    CC.translate(
                            "§e点击查看档案"
                    )
            )).clickEvent(ClickEvent.runCommand("/view " + profile.getPlayerName()));
        }
        return userName;
    }

    private void sendMessage(AsyncPlayerChatEvent event, Player player, PlayerProfile profile, Player viewer, TextComponent userName, Audience audience) {
        audience.sendMessage(
                Component.text()
                        .append(
                                Component.text(
                                        CC.translate(
                                                profile.isSupporter() && profile.getPlayerOption().isSupporterStarDisplay() && !profile.isNicked()
                                                        ? "&e✬ " : ""
                                        )
                                ).hoverEvent(Component.text("§e拥有天坑会员"))

                        )
                        .append(userName)
                        .append(
                                Component.text(

                                        (RankUtil.getPlayerRankColor(player.getUniqueId(), viewer).equalsIgnoreCase(CC.translate("&7")) ||
                                                RankUtil.getPlayerRankColor(player.getUniqueId(), viewer).equalsIgnoreCase(CC.translate("&k")) ?
                                                "§7: " : "§f: ") +
                                                (player.hasPermission("thepit.admin") ? CC.translate(event.getMessage()) : event.getMessage())
                                )
                        )
                        .build()
        );
    }

}
