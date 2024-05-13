package cn.charlotte.pit

import cn.charlotte.pit.data.PlayerProfile
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.getPitProfile(): PlayerProfile {
    return PlayerProfile.getPlayerProfileByUuid(this.uniqueId)
}

fun Player.releaseItem() {
    val craftPlayer = this as CraftPlayer
    craftPlayer.handle.bU()
}

fun Player.hasRealMan(): Boolean {
    return (player.getMetadata("real_man").firstOrNull()?.asLong() ?: Long.MIN_VALUE) >= System.currentTimeMillis()
}