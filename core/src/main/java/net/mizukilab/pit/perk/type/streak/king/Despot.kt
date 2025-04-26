package net.mizukilab.pit.perk.type.streak.king

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.event.PitStreakKillChangeEvent
import cn.charlotte.pit.perk.AbstractPerk
import cn.charlotte.pit.perk.MegaStreak
import cn.charlotte.pit.perk.PerkType
import com.google.common.util.concurrent.AtomicDouble
import net.mizukilab.pit.item.type.perk.Sceptre
import net.mizukilab.pit.parm.AutoRegister
import net.mizukilab.pit.parm.listener.IAttackEntity
import net.mizukilab.pit.parm.listener.IPlayerBeKilledByEntity
import net.mizukilab.pit.parm.listener.IPlayerDamaged
import net.mizukilab.pit.parm.listener.IPlayerKilledEntity
import net.mizukilab.pit.util.PlayerUtil
import net.mizukilab.pit.util.chat.CC
import net.mizukilab.pit.util.chat.MessageType
import net.mizukilab.pit.util.inventory.InventoryUtil
import net.mizukilab.pit.util.item.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Araykal
 * @since 2025/4/27
 */
@AutoRegister
class Despot : AbstractPerk(), MegaStreak, Listener, IPlayerDamaged, IAttackEntity, IPlayerKilledEntity,
    IPlayerBeKilledByEntity {
    override fun getInternalPerkName(): String {
        return "despot_streak"
    }

    override fun getDisplayName(): String {
        return "&c暴君"
    }

    override fun getIcon(): Material {
        return Material.DIAMOND_SWORD
    }

    override fun requireCoins(): Double {
        return 100000.0
    }

    override fun requireRenown(level: Int): Double {
        return 150.0
    }

    override fun requirePrestige(): Int {
        return 20
    }

    override fun requireLevel(): Int {
        return 90
    }

    override fun getDescription(player: Player?): MutableList<String> {
        return mutableListOf(
            "&7激活要求连杀数: &c200 连杀",
            "",
            "&7激活后:",
            "  &a▶ &7对穿戴 &a神&c话&e之&6甲&7 的玩家额外造成 &c50% &7伤害",
            "  &a▶ &7受到穿戴 &a神&c话&e之&6甲 &7的玩家伤害减免 &230%",
            "  &a▶ &7增加 &c2❤ &7生命上限",
            "  &a▶ &7额外获得 &6150% 硬币",
            "  &a▶ &7额外获得 &b30% 经验",
            "  &c▶ &7激活后每击杀5名玩家,受击时",
            "  &7额外受到 &c+0.1❤ &7的&c必中&7伤害 (可堆叠,无上限)",
            "  &c▶ &7激活后每击杀50名玩家",
            "  &7扣除 &c0.5❤ &7生命上限",
            "",
            "&7激活后被击杀时:",
            "  &a▶ &7获得天赋物品 &e暴君权杖"
        )
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onStreak(event: PitStreakKillChangeEvent) {
        val player = Bukkit.getPlayer(event.playerProfile.playerUuid) ?: return
        if (!hasDespot(player)) {
            return
        }
        val isDespotActivated = event.from < 200 && event.to >= 200
        if (isDespotActivated) {
            CC.boardCast(
                MessageType.COMBAT,
                "&c&l超级连杀! " + event.playerProfile.formattedNameWithRoman + " &7激活了 &c&l暴君 &7!"
            )
            Bukkit.getOnlinePlayers().forEach { onlinePlayer: Player? ->
                onlinePlayer!!.playSound(
                    onlinePlayer.location,
                    Sound.WITHER_SPAWN,
                    0.8f,
                    1.5f
                )
            }
            player.maxHealth += 4
        }
    }


    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getPerkType(): PerkType {
        return PerkType.MEGA_STREAK
    }

    override fun onPerkActive(player: Player?) {

    }

    override fun onPerkInactive(player: Player) {
        player.removeMetadata("DespotActivated", ThePit.getInstance())
    }

    private fun hasDespot(player: Player): Boolean {
        return PlayerUtil.isPlayerChosePerk(player, "despot_streak")
    }

    override fun getStreakNeed(): Int {
        return 200
    }


    override fun handlePlayerDamaged(
        enchantLevel: Int,
        myself: Player,
        attacker: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        val profile = PlayerProfile.getPlayerProfileByUuid(myself.uniqueId) ?: return
        if (profile.streakKills > 200 && hasDespot(myself)) {
            val tier = (profile.streakKills - 200).let { (it - it % 5) / 5 }
            val healthReduction = tier * 0.2
            if (myself.health > healthReduction) {
                myself.health = (myself.health - healthReduction).coerceAtLeast(0.1)
            } else {
                cancel.set(true)
                finalDamage.addAndGet(9999.0)
            }
        }
        if (profile.streakKills >= 200) {
            val player = Bukkit.getPlayer(attacker.uniqueId) ?: return
            if (player.inventory.leggings != null && "mythic_leggings" == ItemUtil.getInternalName(player.inventory.leggings)) {
                boostDamage.set(boostDamage.get() - 0.3)
            }
        }
    }


    override fun handleAttackEntity(
        enchantLevel: Int,
        attacker: Player,
        target: Entity,
        damage: Double,
        finalDamage: AtomicDouble,
        boostDamage: AtomicDouble,
        cancel: AtomicBoolean
    ) {
        val profile = PlayerProfile.getPlayerProfileByUuid(attacker.uniqueId) ?: return
        if (profile.streakKills >= 200) {
            val player = Bukkit.getPlayer(target.uniqueId) ?: return
            if (player.inventory.leggings != null && "mythic_leggings" == ItemUtil.getInternalName(player.inventory.leggings)) {
                boostDamage.addAndGet(0.5)
            }
        }
    }

    override fun handlePlayerKilled(
        enchantLevel: Int,
        myself: Player,
        target: Entity,
        coins: AtomicDouble,
        experience: AtomicDouble
    ) {
        val profile = PlayerProfile.getPlayerProfileByUuid(myself.uniqueId) ?: return
        if (profile.streakKills >= 200) {
            val isInNewPhase = profile.streakKills > 200 && (profile.streakKills - 200) % 50 == 0.toDouble()
            if (isInNewPhase) {
                myself.maxHealth -= 1
            }
            experience.addAndGet(experience.get() * 0.3)
            coins.addAndGet(coins.get() * 1.5)
        }

    }

    override fun handlePlayerBeKilledByEntity(
        enchantLevel: Int,
        myself: Player,
        target: Entity,
        coins: AtomicDouble,
        experience: AtomicDouble
    ) {

        val profile = PlayerProfile.getPlayerProfileByUuid(myself.uniqueId) ?: return
        if (profile.streakKills >= 200) {
            Bukkit.getScheduler()
                .runTaskLater(ThePit.getInstance(), {
                    if (InventoryUtil.isInvFull(myself)) {
                        myself.sendMessage(CC.translate("&c&l暴君! &7你的背包已满,死亡奖励并没有给予!"))
                        return@runTaskLater
                    }
                    myself.inventory.addItem(Sceptre().toItemStack())
                    myself.sendMessage(CC.translate("&c&l暴君! &7获得&e权杖"))
                }, 5L)
        }
    }
}