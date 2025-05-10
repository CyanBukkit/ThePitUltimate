package net.mizukilab.pit.perk.type.streak.addon.uber

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.event.PitStreakKillChangeEvent
import cn.charlotte.pit.event.PotionAddEvent
import cn.charlotte.pit.perk.AbstractPerk
import cn.charlotte.pit.perk.MegaStreak
import cn.charlotte.pit.perk.PerkType
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.enchantment.param.event.PlayerOnly
import net.mizukilab.pit.getPitProfile
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.parm.listener.IPlayerKilledEntity
import net.mizukilab.pit.parm.listener.ITickTask
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.MessageType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Araykal
 * @since 2025/5/4
 */
class UberSteakPlus : AbstractPerk(), MegaStreak, Listener, IPlayerKilledEntity, IAttackEntity, ITickTask {
    override fun getInternalPerkName(): String {
        return "uber_steak_plus"
    }

    override fun getDisplayName(): String {
        return "&d&l超级登封造极"
    }

    override fun getIcon(): Material {
        return Material.GRASS
    }

    override fun requireCoins(): Double {
        return 200000.0
    }

    override fun requireRenown(level: Int): Double {
        return 160.0
    }

    override fun requirePrestige(): Int {
        return 30
    }

    override fun requireLevel(): Int {
        return 110
    }

    override fun getDescription(player: Player): MutableList<String> {
        return mutableListOf("", "")
    }

    override fun getMaxLevel(): Int {
        return 0
    }

    private fun hasUberPlus(player: Player) =
        PlayerUtil.isPlayerChosePerk(player, "uber_steak_plus")

    override fun getPerkType(): PerkType {
        return PerkType.MEGA_STREAK
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onStreak(event: PitStreakKillChangeEvent) {
        val player = Bukkit.getPlayer(event.playerProfile.playerUuid) ?: return
        if (!hasUberPlus(player)) return
        if (event.from < 1000 && event.to >= 1000) {
            CC.boardCast(
                MessageType.COMBAT,
                "&c&l超级连杀! ${event.playerProfile.formattedNameWithRoman} &7激活了 ${this.displayName} &7!"
            )
            Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.WITHER_SPAWN, 0.8f, 1.5f) }

        }
    }

    override fun onPerkActive(player: Player?) {

    }

    override fun onPerkInactive(player: Player?) {

    }

    override fun getStreakNeed(): Int {
        return 1000
    }

    override fun handlePlayerKilled(
        enchantLevel: Int,
        myself: Player,
        target: Entity,
        coins: AtomicDouble,
        experience: AtomicDouble
    ) {
        if (!hasUberPlus(myself)) return
        val profile = ThePit.getInstance().profileOperator.namedIOperator(myself.name).profile()
        if (profile.streakKills >= 1000) {
            coins.addAndGet(1.75)
            experience.addAndGet(1.75)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPotionAdd(event: PotionAddEvent) {
        val player = event.player

        if (player !is Player) return
        if (!hasUberPlus(player)) return
        val profile = player.getPitProfile()
        if (profile.streakKills < 3000) return

        val effect = event.effect
        val ambient = effect.isAmbient

        if (!ambient) {
            return
        }

        if (effect.amplifier > 1){
            event.isCancelled = true
            return
        }

        event.isCancelled = true


        val type = effect.type
        val duration = effect.duration / 3
        val amplifier = effect.amplifier


        player.addPotionEffect(PotionEffect(type, duration, amplifier, false))
    }

    @PlayerOnly
    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble?,
        cancel: AtomicBoolean?
    ) {
        if (!hasUberPlus(attacker)) return
        val profile = ThePit.getInstance().profileOperator.namedIOperator(attacker.name).profile()
        if (profile.streakKills >= 1000) {
            val targetProfile = ThePit.getInstance().profileOperator.namedIOperator(target.name).profile()
            if (targetProfile.bounty >= 100) {
                finalDamage.set(finalDamage.get() - 0.6)
            }
        }
        if (profile.streakKills >= 2000) {
            attacker.maxHealth -= 8
        }
        if (profile.streakKills >= 4000){
            PlayerUtil.addPotionEffect(attacker,PotionEffect(PotionEffectType.CONFUSION,100000,0,false))
        }


    }

    override fun handle(enchantLevel: Int, player: Player) {
        if (!hasUberPlus(player)) return
        val profile = ThePit.getInstance().profileOperator.namedIOperator(player.name).profile()
        if (profile.streakKills >= 5000) {
            PlayerUtil.damage(player, PlayerUtil.DamageType.TRUE, 2.0, false)
        }
    }

    override fun loopTick(enchantLevel: Int): Int {
        return 20
    }
}