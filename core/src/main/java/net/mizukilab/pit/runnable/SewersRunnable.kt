package net.mizukilab.pit.runnable

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.config.NewConfiguration
import net.mizukilab.pit.getPitProfile
import net.mizukilab.pit.item.MythicColor
import net.mizukilab.pit.item.type.mythic.MythicLeggingsItem
import net.mizukilab.pit.sendMultiMessage
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
import kotlin.math.cos
import kotlin.math.sin
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
        "mythic_leggings" to 30,
        "milk_buckets" to 10,
        "renown" to 10,
        "speed_eggs" to 10,
    )

    override fun run() {
        val locs = ThePit.getInstance().pitConfig.sewersChestsLocations
        if (locs.isEmpty()) return

        locs.forEach { loc ->
            for (i in 0..360 step 15) {
                val radians = Math.toRadians(i.toDouble())
                val radius = 1.2
                val xOffset = cos(radians) * radius
                val zOffset = sin(radians) * radius
                val particleLoc = loc.clone().add(xOffset, 0.5, zOffset)

                particleLoc.world.playEffect(particleLoc, Effect.HAPPY_VILLAGER, 2, 2)
            }
        }

        if (System.currentTimeMillis() - lastClaimed < 1000 * NewConfiguration.sewersSpawn) return


        var count = 0
        locs.filter { it.block.type == Material.AIR || !it.block.hasMetadata("Sewers_Chest") }
            .forEach { loc ->
                loc.block.apply {
                    type = Material.CHEST
                    setMetadata("Sewers_Chest", FixedMetadataValue(ThePit.getInstance(), true))
                    count++
                }
                CC.boardCast("&9§l下水道! &7箱子点位${count}已刷新: x: ${loc.block.location.x}, y: ${loc.block.location.y}, z: ${loc.block.location.z}")
            }


        existSewersChest = locs
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock.type == Material.CHEST) {
            if (event.clickedBlock.hasMetadata("Sewers_Chest")) {
                claim(event.player, event.clickedBlock.location)
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

        val rewardMessage = when (id) {
            "xp" -> {
                val random = Random.nextInt(99, 8001)
                profile.experience += random.toDouble()
                profile.applyExperienceToPlayer(player)
                "- &b${random}经验"
            }

            "gold" -> {
                val random = Random.nextInt(99, 8001)
                profile.coins += random.toDouble()
                profile.grindCoins(random.toDouble())
                "- &e${random}金币"
            }

            "diamond_chestplate" -> {
                player.inventory.addItem(
                    ItemBuilder(Material.DIAMOND_CHESTPLATE).deathDrop(true).buildWithUnbreakable()
                )
                "- 钻石甲"
            }

            "diamond_leggings" -> {
                player.inventory.addItem(ItemBuilder(Material.DIAMOND_LEGGINGS).deathDrop(true).buildWithUnbreakable())
                "- 钻石裤"
            }

            "diamond_boots" -> {
                player.inventory.addItem(ItemBuilder(Material.DIAMOND_BOOTS).deathDrop(true).buildWithUnbreakable())
                "- 钻石鞋"
            }

            "rubbish" -> {
                player.inventory.addItem(ThePit.getApi().getMythicItemItemStack("rubbish"))
                "- &2下水道废弃物"
            }

            "mythic_leggings" -> {
                player.inventory.addItem(MythicLeggingsItem().apply { color = MythicColor.DARK_GREEN }.toItemStack())
                "- &2下水道之甲"
            }

            "speed_eggs" -> {
                player.inventory.addItem(ThePit.getApi().getMythicItemItemStack("speed_eggs"))
                "- &b速度蛋"
            }

            "renown" -> {
                profile.renown += 1
                "- &e声望 +1"
            }

            "milk_buckets" -> {
                player.inventory.addItem(
                    ThePit.getApi().getMythicItemItemStack("milk")
                )
                "- &f牛奶"
            }

            else -> {
                if (ThePit.getApi().getMythicItemItemStack(id).type != Material.AIR) {
                    val item = ThePit.getApi().getMythicItemItemStack(id)
                    player.inventory.addItem(item)
                    "- ${item.itemMeta.displayName}"
                }
                return
            }
        }
        player.sendMultiMessage("&7获得奖励: /s&7$rewardMessage")
    }
}
