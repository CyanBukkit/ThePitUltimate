package net.mizukilab.pit

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.ThePit.setApi
import net.mizukilab.pit.impl.PitInternalImpl

object Loader {

    fun start() {
        ThePit.getInstance().apply {
            setApi(PitInternalImpl)
        }
        PitHook.init()
        ThePit.getInstance().logger.info("""
            
              CyanBukkit 网站 
            www.cyanbukkit.net
            
            插件维护与更新 By CyanBukkit
            
        """.trimIndent())
    }
    @JvmStatic
    fun begin(){
        System.setProperty("env",this.javaClass.name);
        System.setProperty("ent","start");
        println("MagicLicense initialized")
    }
}
