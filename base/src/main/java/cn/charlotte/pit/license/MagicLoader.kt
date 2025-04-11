package cn.charlotte.pit.license

import cn.charlotte.pit.ThePit
import lombok.SneakyThrows
import pku.yim.license.MagicLicense
import kotlin.concurrent.Volatile

object MagicLoader {
    private val lock = Any()
    private var exception: Exception? = null

    @Volatile
    private var isLoaded = false

    @JvmStatic
    fun hook() {
        Thread {
            try {
                ThePit.getInstance().info("§eConnect Authenticate Server...")
                val magicLicense = MagicLicense(ThePit.getInstance())
                val response = magicLicense.authenticate(
                    ThePit.getInstance().description.name,
                    ThePit.getInstance().description.version,
                    "AQEBAQEBAX8=",
                    false
                )
                ThePit.getInstance().info(response.toString())
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
