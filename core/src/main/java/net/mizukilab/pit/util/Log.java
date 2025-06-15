package net.mizukilab.pit.util;

import net.mizukilab.pit.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Log {
    public static void WriteLine(String arg){
        Player player = Bukkit.getPlayer("KleeLoveLife");
        if(player != null){
            player.sendMessage(CC.translate("&cDEBUG: " + arg));
        }
    }
}
