package cn.charlotte.pit.js

import jdk.dynalink.beans.StaticClass
import org.bukkit.Bukkit
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import java.util.*
import javax.script.ScriptEngineManager

object JSHandler {

    val engine = ScriptEngineManager()

    fun init() {
        engine.registerEngineName("JavaScript", NashornScriptEngineFactory())
        val arrays = StaticClass.forClass(Arrays::class.java)
        val bukkit = StaticClass.forClass(Bukkit::class.java)


    }

}