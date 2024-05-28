package cn.charlotte.pit.runnable

import cn.charlotte.pit.util.isSpecial
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * 2024/5/28<br>
 * ThePitPlus<br>
 * @author huanmeng_qwq
 */
object SpecialPlayerRunnable : BukkitRunnable() {
    override fun run() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val specialPlayers = onlinePlayers.filter { it.isSpecial }
        val normalPlayers = onlinePlayers.filter { !it.isSpecial }
        specialPlayers.forEach { special ->
            normalPlayers.forEach { normal ->
                if (special.canSee(normal)) {
                    special.hidePlayer(normal)
                }
                if (normal.canSee(special)) {
                    normal.hidePlayer(special)
                }
            }
        }
    }
}