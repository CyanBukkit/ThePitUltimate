package cn.charlotte.pit.enchantment.type.ragerare

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.param.item.ArmorOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.AutoRegister
import cn.charlotte.pit.util.PlayerUtil
import cn.charlotte.pit.util.cooldown.Cooldown
import dev.jnic.annotation.Include
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.metadata.FixedMetadataValue

@Include
@AutoRegister
@ArmorOnly
class Regularity : AbstractEnchantment(), Listener {
    override fun getEnchantName(): String {
        return "狂暴连击"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "regularity"
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RAGE_RARE
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7当近战伤害低于&c${
            when (enchantLevel) {
                1 -> 1.1
                2 -> 1.3
                else -> 1.5
            }
        }❤ &7时/ s&7, 将会自动再次攻击./s&7第二次攻击的伤害为第一次攻击的&c${
            when (enchantLevel) {
                1 -> 45
                2 -> 60
                else -> 75
            }
        }%&7."
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun damage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        if (attacker !is Player) return

        val victim = event.entity
        if (victim !is Player) return

        if (PlayerUtil.shouldIgnoreEnchant(attacker, victim)) {
            return
        }

        val level = ThePit.api.getItemEnchantLevel(attacker.inventory.leggings, "regularity")
        if (level < 1) return

        if (event.finalDamage < when (level) {
                1 -> 1.1
                2 -> 1.3
                else -> 1.5
            }
        ) {
            val metadata = victim.getMetadata("regularity_cooldown")
            metadata.firstOrNull()?.asLong()?.let {
                if (System.currentTimeMillis() < it) {
                    return
                }
            }

            if (!victim.isDead) {
                val boost = when (level) {
                    1 -> 45
                    2 -> 60
                    else -> 75
                } * 0.01

                Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), {
                    victim.noDamageTicks = 0
                    victim.damage(event.damage * boost, attacker)
                    victim.setMetadata(
                        "regularity_cooldown",
                        FixedMetadataValue(ThePit.getInstance(), System.currentTimeMillis() + 1000L + 2)
                    )
                }, 5L)
            }
        }
    }
}