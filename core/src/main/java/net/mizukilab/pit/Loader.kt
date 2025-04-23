package net.mizukilab.pit

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.ThePit.setApi
import net.mizukilab.pit.impl.PitInternalImpl
import net.mizukilab.pit.impl.PitInternalImpl.loaded

object Loader {
    private var hook: PitHook? = null

    @JvmStatic
    fun start() {
        ThePit.getInstance().apply {
            setApi(PitInternalImpl)
        }
        hook = PitHook.also { it.init() }
    }
}
