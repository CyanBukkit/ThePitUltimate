package cn.charlotte.pit.enchantment.type.rare

import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.listener.IPlayerBeKilledByEntity
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity
import cn.charlotte.pit.util.cooldown.Cooldown
import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import java.util.*
import kotlin.collections.HashMap

/**
 * @Author: EmptyIrony
 * @Date: 2021/4/29 23:07
 */
class WolfEnchant : AbstractEnchantment(), IPlayerKilledEntity, IPlayerBeKilledByEntity {

    override fun getEnchantName(): String {
        return "小狗战士"
    }

    override fun getMaxEnchantLevel(): Int {
        return 1
    }

    override fun getNbtName(): String {
        return "wolf"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "成为小狗的战士"
    }

    override fun handlePlayerKilled(
        enchantLevel: Int,
        myself: Player?,
        target: Entity?,
        coins: AtomicDouble?,
        experience: AtomicDouble?
    ) {
        val wolf = myself?.world?.spawnEntity(myself.location, EntityType.WOLF) as Wolf
        wolf.owner = myself
    }

    override fun handlePlayerBeKilledByEntity(
        enchantLevel: Int,
        myself: Player?,
        target: Entity?,
        coins: AtomicDouble?,
        experience: AtomicDouble?
    ) {

    }
}