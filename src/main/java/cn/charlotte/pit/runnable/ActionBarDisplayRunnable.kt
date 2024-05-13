package cn.charlotte.pit.runnable

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.util.chat.ActionBarUtil
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.toMythicItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ActionBarDisplayRunnable {

    private val enchants by lazy {
        ThePit.getInstance()
            .enchantmentFactor
            .actionDisplayEnchants
    }

    fun start() {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate({
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

                    ActionBarUtil.sendActionBar(player, CC.translate(builder.toString()))
                }
            }, 5000L, 50L, TimeUnit.MILLISECONDS)
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