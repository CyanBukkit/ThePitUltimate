package cn.charlotte.pit.enchantment.type.limit.xzq

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.param.event.PlayerOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.enchantment.type.limit.Limit24520Ench
import cn.charlotte.pit.parm.listener.IAttackEntity
import cn.charlotte.pit.parm.listener.IPlayerDamaged
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity
import cn.charlotte.pit.parm.listener.ITickTask
import cn.charlotte.pit.util.PlayerUtil
import cn.charlotte.pit.util.Utils
import cn.charlotte.pit.util.cooldown.Cooldown
import cn.charlotte.pit.util.item.ItemUtil
import cn.charlotte.pit.util.music.NBSDecoder
import cn.charlotte.pit.util.music.PositionSongPlayer
import cn.charlotte.pit.util.music.Song
import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.server.v1_8_R3.PacketPlayInFlying
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import spg.lgdev.handler.MovementHandler
import spg.lgdev.iSpigot
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class LimitXZQ1Ench : AbstractEnchantment(), ITickTask, MovementHandler, IPlayerDamaged {
    private val playerMap: MutableMap<UUID, PositionSongPlayer> = HashMap()
    private val song: Song =
        NBSDecoder.parse(ThePit.getInstance().javaClass.classLoader.getResourceAsStream("tianwailaiwu.nbs"))

    init {
        object : BukkitRunnable() {
            override fun run() {
                val entries: Set<Map.Entry<UUID, PositionSongPlayer>> = HashSet(playerMap.entries)
                for ((key) in entries) {
                    val player = Bukkit.getPlayer(key)
                    if (player == null || !player.isOnline) {
                        val remove: PositionSongPlayer? = playerMap.remove(key)
                        remove?.isPlaying = false
                        continue
                    }
                    if (player.inventory.leggings == null || getItemEnchantLevel(player.inventory.leggings) == -1) {
                        val remove: PositionSongPlayer? = playerMap.remove(key)
                        remove?.isPlaying = false
                    }
                }
            }
        }.runTaskTimer(ThePit.getInstance(), 20, 20)

        try {
            iSpigot.INSTANCE.addMovementHandler(this)
        } catch (ignore: Exception) {
        }
    }

    override fun getEnchantName() = "§d520 §9| §f2024"

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String = "xzq_dj_1"

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }


    override fun handle(enchantLevel: Int, target: Player) {
        val songPlayer: PositionSongPlayer? = playerMap.get(target.uniqueId)
        if (songPlayer == null) {
            val player = PositionSongPlayer(song)
            player.targetLocation = target.location
            player.autoDestroy = false
            player.isLoop = true
            player.isPlaying = true
            player.volume = 0.08.toInt().toByte()
            playerMap[target.uniqueId] = player
        } else {
            target.world.playEffect(target.location.clone().add(0.0, 3.0, 0.0), Effect.NOTE, 1)
        }
    }

    override fun loopTick(enchantLevel: Int): Int {
        return 10
    }

    override fun handleUpdateLocation(
        player: Player, location: Location?, location1: Location?, packetPlayInFlying: PacketPlayInFlying?
    ) {
        val songPlayer: PositionSongPlayer? = playerMap.get(player.uniqueId)
        if (songPlayer != null) {
            songPlayer.targetLocation = player.player.location
        }
    }

    override fun handleUpdateRotation(var1: Player?, var2: Location?, var3: Location?, var4: PacketPlayInFlying?) {
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7向周围的玩家播放音乐: &b天外来物" + "/s&7受到使用附魔狂暴连击的玩家所造成的伤害-${damage(enchantLevel) * 10}%"
    }

    fun damage(enchantLevel: Int) = when (enchantLevel) {
        1 -> 0.1
        2 -> 0.15
        4 -> 0.2
        else -> enchantLevel * 2.0
    }

    @PlayerOnly
    override fun handlePlayerDamaged(
        enchantLevel: Int,
        myself: Player,
        attacker: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        attacker as Player
        val hasRegularity = Utils.getEnchantLevel(attacker.inventory.leggings, "regularity") > 0
        if (hasRegularity) {
            boostDamage.addAndGet(-damage(enchantLevel))
        }
    }

}