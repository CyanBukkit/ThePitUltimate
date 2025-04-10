package cn.charlotte.pit.data.sub

import cn.charlotte.pit.ThePit

class KingsQuestsData {

    var currentKingQuestsUuid: String? = null

    var accepted = false
    var completed = false

    var killedPlayer = 0
    var collectedRenown = 0


    fun checkUpdate() {
        if (currentKingQuestsUuid == null) {
            currentKingQuestsUuid = ThePit.getApi().runningKingsQuestsUuid.toString()
            return
        }

        if (currentKingQuestsUuid != ThePit.getApi().runningKingsQuestsUuid.toString()) {
            currentKingQuestsUuid = ThePit.getApi().runningKingsQuestsUuid.toString()
            accepted = false
            completed = false
            killedPlayer = 0
            collectedRenown = 0
        }
    }

}