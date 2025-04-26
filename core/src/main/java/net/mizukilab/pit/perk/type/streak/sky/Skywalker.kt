package net.mizukilab.pit.perk.type.streak.sky

import cn.charlotte.pit.perk.AbstractPerk
import cn.charlotte.pit.perk.MegaStreak
import cn.charlotte.pit.perk.PerkType
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Araykal
 * @since 2025/4/27
 */
class Skywalker : AbstractPerk(), MegaStreak {
    override fun getInternalPerkName(): String {
        return "skywalker"
    }

    override fun getDisplayName(): String {
        return "&b天空行者"
    }

    override fun getIcon(): Material {
        return Material.FEATHER
    }

    override fun requireCoins(): Double {
        return 50000.0
    }

    override fun requireRenown(level: Int): Double {
        return 100.0
    }

    override fun requirePrestige(): Int {
        return 35
    }

    override fun requireLevel(): Int {
        return 110
    }

    override fun getDescription(player: Player?): MutableList<String> {
        return mutableListOf("&7激活要求连杀数: &c50 连杀", " ", "&7激活后:","  &a▶ &7每 &e30秒 可飞行 5秒","  &a▶ &7每击杀5名玩家，受到的伤害&c+5%","  &a▶ &7击杀获得 &6+50% 硬币","  &a▶ &7击杀获得 &b+50% 经验")
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun getPerkType(): PerkType {
        return PerkType.MEGA_STREAK
    }

    override fun onPerkActive(player: Player?) {
    }

    override fun onPerkInactive(player: Player?) {
    }

    override fun getStreakNeed(): Int {
        return 50
    }
}