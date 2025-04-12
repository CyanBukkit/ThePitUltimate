package net.mizukilab.pit.enchantment.type.aqua

import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity
import net.mizukilab.pit.util.cooldown.Cooldown


class HeadOnBlow : AbstractEnchantment() {
    override fun getEnchantName(): String {
        return "当头一棒"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "head-on_blow"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.FISH_NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "甩出鱼钩击退玩家力度增加20%"
    }
}