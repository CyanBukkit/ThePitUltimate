package net.mizukilab.pit.license

import cn.charlotte.pit.ThePit
import lombok.SneakyThrows
import org.bukkit.Bukkit
import pku.yim.license.MagicLicense
import pku.yim.license.Response
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.Volatile
import kotlin.system.exitProcess

object MagicLoader {
    private val lock = Object()
    private var exception: Exception? = null

    @Volatile
    private var isLoaded = false

    @JvmStatic
    fun load() {
        Thread {
            try {
                ThePit.getInstance().info("§e正在验证凭证，请稍候…")
                val magicLicense = MagicLicense(ThePit.getInstance())
                val response = magicLicense.authenticate(
                    "121.4.65.200",
                    ThePit.getInstance().description.name,
                    ThePit.getInstance().description.version,
                    false
                )
                ThePit.getInstance().info(
                    if (response == Response.ACCEPT)
                        "§a验证成功，感谢您的支持 §c❤"
                    else
                        response.toString()
                )
                synchronized(lock) {
                    isLoaded = true
                    lock.notifyAll()
                }
            } catch (ex: Exception) {
                synchronized(lock) {
                    exception = ex
                    lock.notifyAll()
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
                exitProcess(0)
            }
        }
    }
}
