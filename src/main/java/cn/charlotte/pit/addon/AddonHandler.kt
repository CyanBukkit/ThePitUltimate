package cn.charlotte.pit.addon

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.addon.impl.EnchantBook
import cn.charlotte.pit.addon.impl.GachaPool
import cn.charlotte.pit.addon.impl.GiveItemCommand

object AddonHandler {

    private val addons = ArrayList<Addon>()

    fun start() {
        addons.addAll(
            listOf(GiveItemCommand(), GachaPool, EnchantBook)
        )
        for (addon in addons) {
            addon.enable()
        }
    }

}