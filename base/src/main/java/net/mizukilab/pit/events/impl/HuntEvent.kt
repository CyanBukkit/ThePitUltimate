package net.mizukilab.pit.events.impl

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.event.PitKillEvent
import cn.charlotte.pit.events.AbstractEvent
import cn.charlotte.pit.events.trigger.type.INormalEvent
import cn.charlotte.pit.events.trigger.type.addon.IScoreBoardInsert
import net.minecraft.server.v1_8_R3.EnumParticle
import net.mizukilab.pit.config.NewConfiguration.eventOnlineRequired
import net.mizukilab.pit.util.ParticleBuilder
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

/**
 * @author Araykal
 * @since 2025/2/8
 */
class HuntEvent : AbstractEvent(), INormalEvent, Listener,
    IScoreBoardInsert {
    private val killMap: MutableMap<String, EventData> = mutableMapOf()
    private val KILL_COUNT_THRESHOLD = 15
    private var isActive = false

    override fun getEventInternalName() = "hunt"

    override fun getEventName() = "猎杀"

    override fun requireOnline(): Int = eventOnlineRequired[eventInternalName]!!

    override fun onActive() {
        isActive = true
        CC.boardCast("&c&l猎杀!\n&7五分钟内获得：\n  &b速度 x2\n  &a跳跃提升 x3\n&7效果\n&7每击杀15名玩家将获得 &6声望 +1 &7最高可获得5声望.")
        Bukkit.getPluginManager()
            .registerEvents(this, ThePit.getInstance())
        runEffectTask()
    }

    private fun runEffectTask() {
        object : BukkitRunnable() {
            override fun run() {
                if (isActive) {
                    Bukkit.getOnlinePlayers().forEach {
                        playerEffect(it)
                        ParticleBuilder(it.location.clone().add(0.0, 2.0, 0.0), EnumParticle.REDSTONE)
                            .setCount(3).play()
                    }
                } else {
                    Bukkit.getOnlinePlayers().forEach { removeEffect(it) }
                    cancel()
                }
            }
        }.runTaskTimer(ThePit.getInstance(), 0L, 20L)
    }

    override fun onInactive() {
        HandlerList.unregisterAll(this)
        isActive = false
        CC.boardCast("&c&l猎杀! &7已结束")
    }

    private fun playerEffect(player: Player) {
        PlayerUtil.addPotionEffect(player, PotionEffect(PotionEffectType.SPEED, 114514, 1, true))
        PlayerUtil.addPotionEffect(player, PotionEffect(PotionEffectType.JUMP, 114514, 2, true))
    }

    private fun removeEffect(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        player.removePotionEffect(PotionEffectType.JUMP)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (isActive) {
            event.player.sendMessage(CC.translate("&c&l猎杀!\n&7五分钟内获得：\n  &b速度 x2\n  &a跳跃提升 x3\n&7效果\n&7每击杀8名玩家将获得 &6声望 +1"))
        }
    }

    @EventHandler
    fun onKill(event: PitKillEvent) {
        if (isActive) {
            val profile = PlayerProfile.getPlayerProfileByUuid(event.killer.uniqueId)
            val playerName = event.killer.name
            playerName?.let {
                val eventData = killMap.getOrPut(it) { EventData() }
                if (eventData.prestige >= 5) {
                    return@let
                }
                eventData.kill++

                if (eventData.kill % KILL_COUNT_THRESHOLD == 0) {
                    eventData.prestige++
                    profile?.renown = profile?.renown?.plus(1) ?: 0
                    eventData.kill = 0
                    event.killer.sendMessage(CC.translate("&c&l猎杀! &6声望 +1"))
                    event.killer.playSound(event.killer.location, Sound.NOTE_PLING, 0.5f, 0.5f)
                }
            }
        }
    }

    fun getKillTo(player: Player) = KILL_COUNT_THRESHOLD - killMap.getOrDefault(player.name, EventData()).kill

    override fun insert(player: Player?): MutableList<String> {
        val list = mutableListOf<String>()
        player?.let {
            list.add("&f所需击杀: &b${getKillTo(it)}")
            list.add("&f获得声望: &e${killMap.getOrDefault(it.name, EventData()).prestige}")
        }
        return list
    }

    data class EventData(var kill: Int = 0, var prestige: Int = 0)
}
