package cn.charlotte.pit.util.command.param.defaults;

import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    @Override
    public OfflinePlayer transform(final CommandSender sender, final String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals("")))
            return ((Player) sender);

        final OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(source);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "玩家 " + source + " 不存在,请检查你的输入是否有误.");
            return (null);
        }

        return (player);
    }

    @Override
    public List<String> tabComplete(final Player sender, final Set<String> flags, final String source) {
        return new ArrayList<>(1);
    }

}