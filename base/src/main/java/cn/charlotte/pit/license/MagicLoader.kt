package cn.charlotte.pit.license

import cn.charlotte.pit.ThePit
import lombok.SneakyThrows
import org.bukkit.Bukkit
import pku.yim.license.MagicLicense
import pku.yim.license.Response
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.Volatile

object MagicLoader {
    private val lock = Any()
    private var exception: Exception? = null

    @Volatile
    private var isLoaded = false

    @JvmStatic
    fun load() {
        Thread {
            try {
                val token = ThePit.getInstance().pitConfig.token
                if (token == null || token == "xxx" || try { UUID.fromString(token); false } catch (e: IllegalArgumentException) { true }) {
                    ThePit.getInstance().sendLogs("§c未检测到凭证，请尽快在 §econfig.yml §c中填写密钥以避免服务器关闭。")
                    sleep(60)
                    Bukkit.shutdown()
                    return@Thread
                }
                ThePit.getInstance().info("§e正在验证凭证，请稍候…")
                val magicLicense = MagicLicense(ThePit.getInstance())
                val response = magicLicense.authenticate(
                    ThePit.getInstance().description.name,
                    ThePit.getInstance().description.version,
                    token,
                    false
                )
                ThePit.getInstance().info(
                    if (response == Response.ACCEPT)
                        "§a验证成功，感谢您的支持 §c❤"
                    else
                        "§c验证失败，请检查您的IP或凭证是否正确。"
                )
                synchronized(lock) {
                    isLoaded = true
                    (lock as Object).notifyAll()
                }
            } catch (ex: Exception) {
                synchronized(lock) {
                    exception = ex
                    (lock as Object).notifyAll()
                }
            }
        }.start()
    }


    @JvmStatic
    @SneakyThrows
    @Synchronized
    fun ensureIsLoaded() {
        if (!isLoaded) {
            synchronized(lock) {
                (lock as Object).wait()
            }
            if (exception != null) {
                exception!!.printStackTrace()
                System.exit(-114514)
            }
        }
    }
}
