package net.mizukilab.pit.runnable

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.getPitProfile
import net.mizukilab.pit.util.RandomList
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.item.ItemBuilder
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

object SewersRunnable : BukkitRunnable(), Listener {

    private var existSewersChest: List<Location>? = null

    private var lastClaimed = -1L

    private val randomList = RandomList(
        "xp" to 100,
        "gold" to 100,
        "diamond_chestplate" to 50,
        "diamond_leggings" to 50,
        "diamond_boots" to 50,
        "rubbish" to 50,
        "milk_buckets" to 10,
    )

    override fun run() {
        val locs = ThePit.getInstance().pitConfig.sewersChestsLocations
        if (locs.isEmpty()) {

            return
        }
        locs.forEach { ass ->
            repeat(1) {
                val particleLoc = ass.clone().add(
                    Random.nextDouble(-0.5, 0.5),
                    Random.nextDouble(-0.5, 0.5),
                    Random.nextDouble(-0.5, 0.5)
                )

                particleLoc.world.playEffect(
                    particleLoc,
                    Effect.HAPPY_VILLAGER,
                    2, 2
                )
            }
        }

        if (System.currentTimeMillis() - lastClaimed < 1000 * 20) {
            return
        }
        locs.forEach { ass ->
            val block = ass.block
            if (block.type == Material.AIR) {
                block.type = Material.CHEST
                block.setMetadata("Swers_Chest", FixedMetadataValue(ThePit.getInstance(), true))
                existSewersChest = locs
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val player = e.player
        if (e.clickedBlock.type == Material.CHEST) {
            if (e.clickedBlock.hasMetadata("Swers_Chest")) {
                claim(player, e.clickedBlock.location)
            }
        }
    }

    private fun claim(player: Player, location: Location) {
        location.block.type = Material.AIR
        lastClaimed = System.currentTimeMillis()

        val profile = player.getPitProfile()

        val id = randomList.random() ?: return
        player.playSound(player.location, Sound.LEVEL_UP, 1f, 1f)
        player.sendMessage(CC.translate("&9下水道! &7你领取了下水道奖励."))
        when (id) {
            "xp" -> {
                profile.experience += 100
                profile.applyExperienceToPlayer(player)
            }

            "gold" -> {
                profile.coins += 200
                profile.grindCoins(200.0)
            }

            "diamond_chestplate" -> {
                player.inventory.addItem(
                    ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .deathDrop(true)
                        .canSaveToEnderChest(true)
                        .canTrade(true)
                        .internalName("shopItem")
                        .buildWithUnbreakable()
                )
            }

            "diamond_leggings" -> {
                player.inventory.addItem(
                    ItemBuilder(Material.DIAMOND_LEGGINGS)
                        .deathDrop(true)
                        .canSaveToEnderChest(true)
                        .canTrade(true)
                        .internalName("shopItem")
                        .buildWithUnbreakable()
                )

            }

            "diamond_boots" -> {
                player.inventory.addItem(
                    ItemBuilder(Material.DIAMOND_BOOTS)
                        .deathDrop(true)
                        .canSaveToEnderChest(true)
                        .canTrade(true)
                        .internalName("shopItem")
                        .buildWithUnbreakable()
                )
            }

            "rubbish" -> {
                player.inventory.addItem(
                    ThePit.getApi().getMythicItemItemStack("rubbish")
                )
            }

            "milk_buckets" -> {
                player.inventory.addItem(
                    ItemBuilder(Material.MILK_BUCKET)
                        .deathDrop(false)
                        .canSaveToEnderChest(true)
                        .canDrop(false)
                        .canTrade(true)
                        .lore(
                            "&7死亡后保留",
                            "",
                            "&a生命恢复 I(2:00)",
                            "&7补钙"
                        )
                        .internalName("milk_bucket")
                        .build()
                )
            }

        }
    }
}