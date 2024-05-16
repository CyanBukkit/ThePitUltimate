package cn.charlotte.pit.util.hologram.packet

import org.bukkit.scheduler.BukkitRunnable

object PacketHologramRunnable : BukkitRunnable() {
    val holograms: MutableSet<PacketHologram> = HashSet()
    override fun run() {
        holograms.removeIf {
            !it.spawned
        }
        holograms.forEach {
            it.update()
        }
    }

}