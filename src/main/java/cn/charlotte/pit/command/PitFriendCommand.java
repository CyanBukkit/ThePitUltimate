package cn.charlotte.pit.command;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.FriendData;
import cn.charlotte.pit.data.sub.FriendNameData;
import cn.charlotte.pit.data.sub.FriendRequestData;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.command.Command;
import cn.charlotte.pit.util.command.param.Parameter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * 2022/7/29<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class PitFriendCommand {
    @Command(
            names = {
                    "pitfriend add",
                    "pf add"
            }
    )
    public void addFriend(Player player, @Parameter(name = "目标玩家") Player target) {
        PlayerProfile myself = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
        if (myself.getFriendData() == null) {
            myself.setFriendData(new FriendData());
        }
        if (targetProfile.getFriendData() == null) {
            targetProfile.setFriendData(new FriendData());
        }

        if (targetProfile.getFriendData().isFriend(player.getUniqueId())) {
            player.sendMessage(CC.translate("&b&l好友! &c该玩家已经是你的好友了!"));
            return;
        }
        if (myself.getFriendData().isRequesting(target.getUniqueId())) {
            accept(player, target);
            return;
        }
        if (myself.getFriendData().isRequested(target.getUniqueId())) {
            player.sendMessage(CC.translate("&b&l好友! &7你已经向 &a" + player.getName() + " &7发起了好友请求!"));
            return;
        }
        FriendRequestData friendRequestData = new FriendRequestData(player.getUniqueId().toString(), target.getUniqueId().toString(), System.currentTimeMillis());
        myself.getFriendData().getFriendRequestDatas().add(friendRequestData);
        targetProfile.getFriendData().getFriendRequestDatas().add(friendRequestData);
        ThePit.getInstance().getAudiences()
                .player(target)
                .sendMessage(
                        Component.text(String.format("§b§l好友! §7你收到了来自 §a%s §7的好友请求, §b点击这里同意§7!", player.getName()))
                                .clickEvent(ClickEvent.runCommand("/pitfriend accept " + player.getName()))
                                .hoverEvent(Component.text("§a点击接受"))
                );
        player.sendMessage(CC.translate("&b&l好友! &7你向 &a" + target.getName() + " &7发起了好友请求!"));
    }

    @Command(
            names = {
                    "pitfriend accept",
                    "pf accept"
            },
            async = true
    )
    public void accept(Player player, @Parameter(name = "目标玩家") Player target) {
        PlayerProfile myself = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
        if (myself.getFriendData() == null) {
            myself.setFriendData(new FriendData());
        }
        if (targetProfile.getFriendData() == null) {
            targetProfile.setFriendData(new FriendData());
        }
        if (myself.getFriendData().isRequesting(target.getUniqueId())) {
            myself.getFriendData().accept(target.getUniqueId(), target.getName());
            targetProfile.getFriendData().accept(player.getUniqueId(), player.getName());
            player.sendMessage(CC.translate(String.format("&b&l好友! &7成功添加 &a%s &7为好友", target.getName())));
            target.sendMessage(CC.translate(String.format("&b&l好友! &7成功添加 &a%s &7为好友", player.getName())));
            myself.getFriendData().save();
            targetProfile.getFriendData().save();
            myself.updateShow();
            return;
        }
        player.sendMessage(CC.translate("&b&l好友! &c对方未对你发起好友请求"));
    }

    @Command(
            names = {
                    "pitfriend remove",
                    "pf remove"
            }
    )
    public void remove(Player player, @Parameter(name = "目标玩家") OfflinePlayer target) {
        PlayerProfile myself = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
        if (myself.getFriendData() == null) {
            myself.setFriendData(new FriendData());
        }
        if (targetProfile.getFriendData() == null) {
            targetProfile.setFriendData(new FriendData());
        }

        if (!myself.getFriendData().isFriend(target.getUniqueId())) {
            player.sendMessage(CC.translate("&b&l好友! &7对方不是你的好友"));
            return;
        }
        myself.getFriendData().remove(target.getUniqueId());
        targetProfile.getFriendData().remove(player.getUniqueId());
        myself.updateShow();
        player.sendMessage(String.format(CC.translate("&b&l好友! &7已将 &a%s &7移出好友列表"), target.getName()));
        if (target.isOnline()) {
            target.getPlayer().sendMessage(String.format(CC.translate("&b&l好友! &a%s &7已将你移出好友列表"), player.getName()));
        }
    }

    @Command(
            names = "pitfriend removerequest",
            permissionNode = "thepit.admin"
    )
    public void removeRequest(Player player, @Parameter(name = "目标玩家") OfflinePlayer target) {
        PlayerProfile myself = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
        if (myself.getFriendData() == null) {
            myself.setFriendData(new FriendData());
        }
        if (targetProfile.getFriendData() == null) {
            targetProfile.setFriendData(new FriendData());
        }

        if (!myself.getFriendData().isRequested(target.getUniqueId())) {
            player.sendMessage(CC.translate("&b&l好友! &c你没有向这位玩家发送好友请求"));
            return;
        }
        myself.getFriendData().removeRequest(target.getUniqueId());
        targetProfile.getFriendData().removeRequest(player.getUniqueId());
        player.sendMessage(String.format(CC.translate("&b&l好友! &7已取消向 %s 的好友申请"), target.getName()));
    }

    @Command(
            names = {
                    "pitfriend list",
                    "pitfriend",
                    "pf"
            }
    )
    public void list(Player player) {
        PlayerProfile myself = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (myself.getFriendData() == null) {
            return;
        }
        Set<FriendNameData> friends = myself.getFriendData().getFriends();
        if (friends.isEmpty()) {
            player.sendMessage(CC.translate("&b&l好友! &7你没有好友"));
            return;
        }
        player.sendMessage(CC.translate("&b&l好友! &7以下为你的好友列表(不分先后顺序)"));
        Audience audience = ThePit.getInstance().getAudiences().player(player);
        for (FriendNameData friend : friends) {
            audience.sendMessage(
                    Component.text("§b§l好友! §e" + friend.getName())
                            .clickEvent(ClickEvent.suggestCommand(friend.getName()))
                            .hoverEvent(
                                    Component.text("§a" + friend.getName())
                            )
            );
        }
    }
}
