package net.mizukilab.pit.map.kingsquests

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.item.AbstractPitItem
import net.mizukilab.pit.map.kingsquests.item.Cherry
import net.mizukilab.pit.map.kingsquests.item.HighGradeEggs
import net.mizukilab.pit.map.kingsquests.item.MiniCake
import net.mizukilab.pit.map.kingsquests.item.PackagedBale
import net.mizukilab.pit.map.kingsquests.item.Sugar
import net.mizukilab.pit.map.kingsquests.item.Wheat
import net.mizukilab.pit.map.kingsquests.item.YummyBread
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object KingsQuests {

    fun enable() {
        Cherry.register()
        HighGradeEggs.register()
        MiniCake.register()
        PackagedBale.register()
        Sugar.register()
        Wheat.register()
        YummyBread.register()
    }

    private fun Any.register() {
        if (this is Listener) {
            Bukkit.getPluginManager().registerEvents(this, ThePit.getInstance())
        }

        if (this is AbstractPitItem) {
            ThePit.getInstance()
                .itemFactor.registerItem(this)
        }
    }

}