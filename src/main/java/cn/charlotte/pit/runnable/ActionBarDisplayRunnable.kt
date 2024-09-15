package cn.charlotte.pit.runnable

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.util.chat.ActionBarUtil
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.toMythicItem
import net.minecraft.server.v1_8_R3.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.inventivetalent.reflection.minecraft.Minecraft
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ActionBarDisplayRunnable {

    private val enchants by lazy {
        ThePit.getInstance()
            .enchantmentFactor
            .actionDisplayEnchants
    }
    @JvmStatic
    val shutdownStr = CC.translate("&c&l服务器关闭中")
    fun start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ThePit.getInstance(),{
                if(!MinecraftServer.getServer().isRunning){
                    for (player in Bukkit.getOnlinePlayers()) {
                        ActionBarUtil.sendActionBar1(player,"system", shutdownStr,2);
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

                    player.itemInHand?.apply {
                        player.handleActionDisplay(this, builder)
                    }

                    for (armorContent in player.inventory.armorContents) {
                        val itemStack = armorContent ?: continue
                        player.handleActionDisplay(itemStack, builder)
                    }
                    if(builder.isNotBlank()) ActionBarUtil.sendActionBar1(player,"skill", CC.translate(builder.toString()),4)
                }
            }, 100L, 20L)
    }

    private fun Player.handleActionDisplay(itemStack: ItemStack, builder: StringBuilder) {
        val item = itemStack.toMythicItem() ?: return
        for (enchantment in item.enchantments) {
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