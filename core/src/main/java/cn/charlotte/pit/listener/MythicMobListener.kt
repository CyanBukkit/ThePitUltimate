package cn.charlotte.pit.listener

import cn.charlotte.pit.config.NewConfiguration
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.getPitProfile
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MythicMobListener: Listener {

    @EventHandler
    fun onMMDead(event: MythicMobDeathEvent) {
        val mob = event.mob
        val beKilled = mob.entity.bukkitEntity

        if (beKilled !is LivingEntity) return

        NewConfiguration.mythicMobs[mob.type.internalName] ?: return

        val killer = event.killer
        if (killer !is Player) return

        CombatListener.INSTANCE.handleKill(
            killer,
            killer.getPitProfile(),
            beKilled,
            PlayerProfile.NONE_PROFILE
        )
    }

}