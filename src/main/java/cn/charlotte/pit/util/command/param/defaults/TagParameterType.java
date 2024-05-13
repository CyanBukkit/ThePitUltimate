package cn.charlotte.pit.util.command.param.defaults;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.Tag;
import cn.charlotte.pit.util.command.param.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagParameterType implements ParameterType<Tag> {

    @Override
    public Tag transform(final CommandSender sender, final String source) {
        Tag tag = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (!profile.isLoaded()) {
                continue;
            }
            if (!profile.isNewPlayer()) {
                continue;
            }
            if (!source.equalsIgnoreCase(profile.getProtectTag())) {
                continue;
            }
            tag = new Tag(profile.getProtectTag());
            break;
        }

        if (tag == null) {
            sender.sendMessage(ChatColor.RED + "Tag为 " + source + " 的玩家不存在,请检查你的输入是否有误或已更新.");
            return (null);
        }

        return tag;
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String source) {
        final List<String> completions = new ArrayList<>();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (sender.equals(player)) {
                continue;
            }
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (!profile.isLoaded()) {
                continue;
            }
            if (!profile.isNewPlayer()) {
                continue;
            }
            if (profile.getProtectTag() == null) {
                continue;
            }
            completions.add(profile.getProtectTag());
        }

        return completions;
    }

}