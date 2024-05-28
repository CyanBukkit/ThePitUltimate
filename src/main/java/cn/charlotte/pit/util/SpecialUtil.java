package cn.charlotte.pit.util;

import cn.charlotte.pit.data.PlayerProfile;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * 2024/5/28<br>
 * ThePitPlus<br>
 *
 * @author huanmeng_qwq
 */
public class SpecialUtil {
    private static Set<String> SPECIALS = new HashSet<String>();

    static {
        SPECIALS.add("ABC");
    }

    public static boolean isSpecial(String str) {
        return SPECIALS.contains(str);
    }

    public static boolean isSpecial(Player player) {
        return isSpecial(player.getName());
    }

    public static boolean isSpecial(PlayerProfile profile) {
        return isSpecial(profile.getPlayerName());
    }
}
