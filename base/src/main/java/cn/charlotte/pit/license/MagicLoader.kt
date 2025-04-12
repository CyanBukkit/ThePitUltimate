package cn.charlotte.pit.license

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
    private val lock = Any()
    private var exception: Exception? = null

    @Volatile
    private var isLoaded = false

    @JvmStatic
    fun hook() {
        Thread {
            try {
                val token = ThePit.getInstance().pitConfig.token
                if (token == null || token == "xxx" || try { UUID.fromString(token); false } catch (e: IllegalArgumentException) { true }) {
                    ThePit.getInstance().sendLogs("§c凭证未入，速启设档，于 §econfig.yml §c中录入秘钥，免关伺服。")
                    sleep(60)
                    Bukkit.shutdown()
                    return@Thread
                }
                ThePit.getInstance().info("§e正联伺信，少顷可候…")
                val magicLicense = MagicLicense(ThePit.getInstance())
                val response = magicLicense.authenticate(
                    ThePit.getInstance().description.name,
                    ThePit.getInstance().description.version,
                    token,
                    false
                )
                ThePit.getInstance().info(
                    if (response == Response.ACCEPT)
                        "§a验已成，承君厚谊 §c❤"
                    else
                        "§c验未果，察查尔IP或凭信可安否。"
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
                exitProcess(0)
            }
        }
    }
}
