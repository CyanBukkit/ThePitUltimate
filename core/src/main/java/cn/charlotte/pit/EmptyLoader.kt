package cn.charlotte.pit

import cn.charlotte.pit.ThePit.setApi
import cn.charlotte.pit.impl.PitInternalImpl
import cn.charlotte.pit.impl.PitInternalImpl.loaded

object EmptyLoader {
    private var hook: PitHook? = null

    @JvmStatic
    fun start() {
        ThePit.getInstance().apply {
            setApi(PitInternalImpl)
        }
        hook = PitHook.also { it.init() }
        loaded = true
    }
}
