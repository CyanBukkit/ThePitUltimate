package net.mizukilab.pit.runnable

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import net.mizukilab.pit.item.AbstractPitItem
import net.mizukilab.pit.util.chat.ActionBarUtil
import net.mizukilab.pit.util.chat.CC
import net.minecraft.server.v1_8_R3.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.collections.iterator

object ActionBarDisplayRunnable {

    private val enchants by lazy {
        ThePit.getInstance()
            .enchantmentFactor
            .actionDisplayEnchants
    }

    @JvmStatic
    val shutdownStr: String = CC.translate("&c&l服务器关闭中 ")
    fun start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ThePit.getInstance(), {
            if (!MinecraftServer.getServer().isRunning) {
                for (player in Bukkit.getOnlinePlayers()) {
                    ActionBarUtil.sendActionBar1(player, "system", shutdownStr, 20);
                }
                return@runTaskTimerAsynchronously
            }
            val now = System.currentTimeMillis()

            for (player in Bukkit.getOnlinePlayers()) {
                val metadata = player.getMetadata("showing_damage_data")
                if (metadata.isNotEmpty()) {
                    val value = metadata.firstOrNull()
                    if (value != null) {
                        if (now - value.asLong() <= 1000L) {
                            continue
                        }
                    }
                }

                val builder = StringBuilder()

                PlayerProfile.getPlayerProfileByUuid(player.uniqueId)?.apply {
                    player.handleActionDisplay(this.heldItem, builder)
                    player.handleActionDisplay(this.leggings, builder)

                }


                if (builder.isNotBlank()) ActionBarUtil.sendActionBar1(
                    player,
                    "skill",
                    CC.translate(builder.toString()),
                    4
                )
            }
        }, 100L, 20L)
    }

    private fun Player.handleActionDisplay(itemStack: AbstractPitItem?, builder: StringBuilder) {
        itemStack?.run {
            for (enchantment in enchantments) {
                val displayEnchant = enchants[enchantment.key.nbtName] ?: continue
                builder
                    .append("&b&l")
                    .append(enchantment.key.enchantName)
                    .append(" ")
                    .append(displayEnchant.getText(enchantment.value, player))
                    .append(" ")
            }
        }
    }

}