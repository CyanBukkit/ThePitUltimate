package cn.charlotte.pit

import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.util.Utils
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.*

fun Player.getPitProfile(): PlayerProfile {
    return PlayerProfile.getPlayerProfileByUuid(this.uniqueId)
}

fun Player.releaseItem() {
    val craftPlayer = this as CraftPlayer
    craftPlayer.handle.bU()
}

fun Player.hasRealMan(): Boolean { //hasRealMan 不存在realMan附魔 //TODO
    return false//(player.getMetadata("real_man").firstOrNull()?.asLong() ?: Long.MIN_VALUE) >= System.currentTimeMillis()
}

fun Player.sendMessage(message: Component) {
    ThePit.getInstance().audiences.player(this).sendMessage(message)
}

val Player.audience: Audience
    get()= ThePit.getInstance().audiences.player(this)

fun ItemStack.setNbt(nbt: CompoundBinaryTag): ItemStack {
    return CraftItemStack.asBukkitCopy(Utils.toNMStackQuick(this).apply {
        val outputStream = ByteArrayOutputStream()
        BinaryTagIO.writer().write(nbt, outputStream)
        DataOutputStream(outputStream).use { dataOutputStream ->
            val buf = outputStream.toByteArray()
            ByteArrayInputStream(buf).use { `in` ->
                DataInputStream(`in`).use { datastructures ->
                    tag = NBTCompressedStreamTools.a(datastructures)
                }
            }
        }
    })
}

val ItemStack.nbt: CompoundBinaryTag
    get() {
        return Utils.toNMStackQuick(this).tag?.let { tag ->
            ByteArrayOutputStream().use { outputStream ->
                DataOutputStream(outputStream).use { dataOutputStream ->
                    NBTCompressedStreamTools.a(tag, (dataOutputStream as DataOutput))
                    val buf = outputStream.toByteArray()
                    ByteArrayInputStream(buf).use { inputStream ->
                        BinaryTagIO.reader().read(inputStream)
                    }
                }
            }
        } ?: CompoundBinaryTag.empty()
    }
