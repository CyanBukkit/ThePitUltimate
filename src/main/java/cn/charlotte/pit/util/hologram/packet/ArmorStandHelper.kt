package cn.charlotte.pit.util.hologram.packet

import cn.charlotte.pit.util.hologram.packet.type.WrapperPlayServerEntityTeleport
import net.minecraft.server.v1_8_R3.EntityArmorStand
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity

/**
 * 2024/5/16<br></br>
 * ThePitPlus<br></br>
 *
 * @author huanmeng_qwq
 */
object ArmorStandHelper {

    @JvmStatic
    fun applyLocation(location: Location, armorStand: PacketArmorStand) {
        armorStand.location = location
        val entity = armorStand.entity() as CraftEntity
        entity.teleport(location)
        val entityTeleport = WrapperPlayServerEntityTeleport()
        entityTeleport.entityID = entity.entityId
        entityTeleport.x = location.x
        entityTeleport.y = location.y
        entityTeleport.z = location.z
        entityTeleport.yaw = location.yaw
        entityTeleport.pitch = location.pitch
        armorStand.viewing().forEach {
            entityTeleport.sendPacket(it)
        }
    }

    @JvmStatic
    fun memoryEntity(location: Location): ArmorStand {
        val worldServer = (location.world as CraftWorld).handle
        val entityArmorStand = EntityArmorStand(worldServer, location.x, location.y, location.z)
        return entityArmorStand.bukkitEntity as ArmorStand
    }

    @JvmStatic
    fun setEntityLocation(entity: Entity, to: Location) {
        entity as CraftEntity
        entity.handle.setLocation(to.x, to.y, to.z, to.yaw, to.pitch)
    }
}
