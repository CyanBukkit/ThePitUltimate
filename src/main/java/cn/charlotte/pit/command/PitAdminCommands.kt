package cn.charlotte.pit.command

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.command.handler.HandHasItem
import cn.charlotte.pit.config.NewConfiguration.load
import cn.charlotte.pit.config.NewConfiguration.loadFile
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.data.TradeData
import cn.charlotte.pit.medal.impl.challenge.hidden.KaboomMedal
import cn.charlotte.pit.runnable.RebootRunnable.RebootTask
import cn.charlotte.pit.sendMessage
import cn.charlotte.pit.util.MythicUtil
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.item.ItemBuilder
import cn.charlotte.pit.util.level.LevelUtil
import cn.charlotte.pit.util.rank.RankUtil
import com.mongodb.client.model.Filters
import dev.jnic.annotation.Include
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.async.Async
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.flag.Flag
import dev.rollczi.litecommands.annotations.permission.Permission
import dev.rollczi.litecommands.annotations.shortcut.Shortcut
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector


/**
 * 2024/5/15<br>
 * ThePitPlus<br>
 * @author huanmeng_qwq
 */
@Include
@Command(name = "pitAdmin")
@Permission("pit.admin")
class PitAdminCommands {

    @Execute(name = "giveItemInHand")
    @Shortcut("give")
    fun giveItemInHand(@Context player: Player, @Arg("target") target: Player) {
        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) {
            player.sendMessage(CC.translate("&c请手持要给予的物品!"))
            return
        }
        target.inventory.addItem(player.itemInHand)
        target.sendMessage(CC.translate("&a一位管理员给予了你一些物品..."))
        player.sendMessage(CC.translate("&a成功给予物品至 " + RankUtil.getPlayerColoredName(target.uniqueId)))
    }

    @Execute(name = "giveAll")
    @Shortcut("giveAll")
    fun giveItemInHandAll(@Context player: Player, @Flag("pvp") pvp: Boolean) {
        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) {
            player.sendMessage(CC.translate("&c请手持要给予的物品!"))
            return
        }
        Bukkit.getOnlinePlayers().forEach { target ->
            if (pvp) {
                if (PlayerProfile.getPlayerProfileByUuid(target.uniqueId).combatTimer.hasExpired()) {
                    return@forEach
                }
            }
            target.inventory.addItem(player.itemInHand)
            target.sendMessage(CC.translate("&a一位管理员给予了你一些物品..."))
            player.sendMessage(CC.translate("&a成功给予物品至 " + RankUtil.getPlayerColoredName(target.uniqueId)))
        }
    }

    @Execute(name = "addSpawn")
    @Async
    fun addSpawn(@Context player: Player): String {
        ThePit.getInstance().pitConfig.spawnLocations.add(player.location)

        ThePit.getInstance().pitConfig.save()

        val num = ThePit.getInstance().pitConfig.spawnLocations.size

        return CC.translate("&a成功!添加第" + num + "个出生点")
    }

    @Execute(name = "loc")
    @Async
    fun dumpLocation(@Context player: Player) {
        val location = player.location
        player.sendMessage(
            Component.text(location.toString()).clickEvent(
                ClickEvent.suggestCommand(location.toString())
            )
        )
    }

    @Execute(name = "hologramLoc")
    @Async
    fun setHologramLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.hologramLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置全息位置!")
    }

    @Execute(name = "keeperLoc")
    @Async
    fun setKeeperLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.keeperNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置看门人NPC位置!")
    }

    @Execute(name = "mail")
    @Async
    fun setMailLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.mailNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置邮件NPC位置!")
    }

    @Execute(name = "genesisDemonLoc")
    @Async
    fun setGenesisDemonLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.genesisDemonNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置恶魔阵营NPC位置!")
    }

    @Execute(name = "genesisAngelLoc")
    @Async
    fun setGenesisAngelLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.genesisAngelNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置天使阵营NPC位置!")
    }

    @Execute(name = "shopNpc")
    @Async
    fun setShopNpcLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.shopNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置商店NPC位置!")
    }

    @Execute(name = "perkNpc")
    @Async
    fun setPerkNpcLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.perkNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置增益NPC位置!")
    }

    @Execute(name = "prestigeNpc")
    @Async
    fun setPrestigeNpcLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.prestigeNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置精通NPC位置!")
    }

    @Execute(name = "debug")
    @Async
    fun debug(@Context player: Player, @Arg("type") type: String): String {
        val pitConfig = ThePit.getInstance().pitConfig
        if (type == "infNpc") {
            pitConfig.infinityNpcLocation = player.location
            pitConfig.save()

            return CC.translate("&a设置成功!")
        }

        if (type.equals("toggle", ignoreCase = true)) {
            pitConfig.isDebugServer = !pitConfig.isDebugServer
            pitConfig.save()

            ThePit.getInstance().rebootRunnable.addRebootTask(
                RebootTask(
                    "服务器配置切换",
                    System.currentTimeMillis() + 10 * 1000
                )
            )

            if (pitConfig.isDebugServer) {
                return CC.translate("&a现在开启了，重启以生效")
            } else {
                return CC.translate("&c现在关闭了，重启以生效")
            }
        }

        if (type == "enchNpc") {
            pitConfig.enchantNpcLocation = player.location
            pitConfig.save()

            return CC.translate("&a设置成功!")
        }

        if (type == "toPublic") {
            pitConfig.isDebugServerPublic = true
            pitConfig.save()

            return CC.translate("&a现在开启了")
        }

        if (type == "toPrivate") {
            pitConfig.isDebugServerPublic = false
            pitConfig.save()

            return CC.translate("&c现在关闭了")
        }
        return "§c未知type"
    }

    @Execute(name = "statusNpc")
    @Async
    fun setStatusNpcLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.statusNpcLocation = player.location

        ThePit.getInstance().pitConfig.save()

        return CC.translate("&a成功设置统计NPC位置!")
    }

    @Execute(name = "leaderHologram")
    fun setLeaderHologramLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.leaderBoardHologram = player.location

        ThePit.getInstance().pitConfig.save()
        return CC.translate("&a成功设置排行榜全息位置!")
    }

    @Execute(name = "helperHolo")
    fun setHelperHologramLocation(@Context player: Player): String {
        ThePit.getInstance().pitConfig.helperHologramLocation = player.location

        ThePit.getInstance().pitConfig.save()
        return CC.translate("&a成功设置指令帮助全息位置!")
    }

    @Execute(name = "pitLoc")
    fun setPitLoc(@Context player: Player, @Arg("type") type: String): String {
        if (type.equals("a", ignoreCase = true)) {
            ThePit.getInstance().pitConfig.pitLocA = player.location
        } else if (type.equals("b", ignoreCase = true)) {
            ThePit.getInstance().pitConfig.pitLocB = player.location
        } else {
            return "§c未知type: a, b"
        }
        ThePit.getInstance().pitConfig.save()
        return "§a已保存"
    }

    @Execute(name = "quest")
    @Async
    fun setQuestNpc(@Context player: Player): String {
        ThePit.getInstance().pitConfig.questNpcLocation = player.location
        ThePit.getInstance().pitConfig.save()
        return CC.translate("&a成功设置任务NPC位置!")
    }

    @Execute(name = "table")
    @Async
    fun setTableNpc(@Context player: Player): String {
        ThePit.getInstance()
            .pitConfig.enchantLocation = player.getTargetBlock(setOf(Material.ENCHANTMENT_TABLE), 100).location
        ThePit.getInstance().pitConfig.save()
        return CC.translate("&a成功设置附魔方块位置: ${ThePit.getInstance().pitConfig.enchantLocation}")
    }

    @Execute(name = "ham")
    fun hamNpc(@Context player: Player): String {
        val config = ThePit.getInstance()
            .pitConfig
        config.hamburgerNpcLocA
            .add(player.location)
        config.save()
        return "§a添加成功: " + config.hamburgerNpcLocA.size
    }

    @Execute(name = "ham clear")
    fun hamClear(@Context player: Player): String {
        val config = ThePit.getInstance()
            .pitConfig
        config.hamburgerNpcLocA.clear()
        config.save()
        return "§a已清空"
    }

    @Execute(name = "spire floor")
    fun spireFloor(@Context player: Player): String {
        val config = ThePit.getInstance()
            .pitConfig
        config.spireFloorLoc
            .add(player.location)
        config.save()

        return "§aNow: " + config.spireFloorLoc.size
    }

    @Execute(name = "spire spawn")
    fun spireSpawn(@Context player: Player) {
        ThePit.getInstance()
            .pitConfig.spireLoc = player.location

        ThePit.getInstance()
            .pitConfig
            .save()
    }

    @Execute(name = "reload")
    fun reloadConfig(@Context sender: CommandSender) {
        sender.sendMessage(CC.translate("&7 重载中..."))
        ThePit.getInstance().pitConfig.load()
        loadFile()
        load()
        sender.sendMessage(CC.translate("&7 重载完成!"))
    }

    @Execute(name = "edit")
    fun changeEdit(@Context player: Player) {
        val profile = PlayerProfile.getPlayerProfileByUuid(player.uniqueId)
        profile.isEditingMode = !profile.isEditingMode
        if (profile.isEditingMode) {
            player.sendMessage(CC.translate("&a你现在可以自由破坏方块"))
        } else {
            player.sendMessage(CC.translate("&c你关闭了自由破坏方块"))
        }
    }

    @Execute(name = "kaboom")
    @Shortcut("kaboom")
    fun kaboom(@Context player: Player) {
        for (target in Bukkit.getOnlinePlayers()) {
            target.velocity = Vector(0, 2, 0)
            target.world.strikeLightningEffect(target.location)
            target.sendMessage(CC.translate("&a&lKaboom!!! " + RankUtil.getPlayerColoredName(player.name) + " &7把你击飞了!"))
            KaboomMedal().addProgress(PlayerProfile.getPlayerProfileByUuid(player.uniqueId), 1)
        }
    }

    @Execute(name = "event")
    @Shortcut("event")
    fun event(@Context player: Player, @Arg("action") /*todo*/action: String): String? {
        val success = ThePit.getApi().openEvent(player, action)
        return if (success) {
            CC.translate("&a成功!")
        } else {
            CC.translate("&c失败, 错误的参数")
        }
    }

    @Execute(name = "testSound")
    fun testSound(@Context player: Player, @Arg("sound") sound: String) {
        ThePit.getInstance().soundFactory
            .playSound(sound, player)
    }

    @Execute(name = "changeItemInHand lives")
    @HandHasItem(mythic = true)
    fun changeLives(@Context player: Player, @Arg("lives") lives: Int) {
        try {
            val stack = ItemBuilder(player.itemInHand).live(lives).build()
            player.itemInHand = MythicUtil.getMythicItem(stack).toItemStack()
        } catch (ignored: Exception) {
            player.sendMessage("Error")
        }
    }

    @Execute(name = "changeItemInHand maxLive")
    @HandHasItem(mythic = true)
    fun changeMaxLive(@Context player: Player, @Arg("maxLive") maxLive: Int) {
        try {
            val stack = ItemBuilder(player.itemInHand).maxLive(maxLive).build()
            player.itemInHand = MythicUtil.getMythicItem(stack).toItemStack()
        } catch (ignored: Exception) {
            player.sendMessage("Error")
        }
    }
    @Execute(name = "changeItemInHand tier")
    @HandHasItem(mythic = true)
    fun changeTier(@Context player: Player, @Arg("tier") tier: Int) {
        try {
            val stack = ItemBuilder(player.itemInHand).maxLive(tier).build()
            player.itemInHand = MythicUtil.getMythicItem(stack).also {
                it.tier = tier
            }.toItemStack()
        } catch (ignored: Exception) {
            player.sendMessage("Error")
        }
    }

    @Execute(name = "trade")
    @Async
    fun trade(@Context player: Player, @Arg("target") target: String) {
        val profile = PlayerProfile.getOrLoadPlayerProfileByName(target)
        if (profile == null) {
            player.sendMessage("§c玩家不存在!")
            return
        }
        val tradeA = ThePit.getInstance()
            .mongoDB
            .tradeCollection
            .find(Filters.eq("playerA", profile.uuid))

        val tradeB = ThePit.getInstance()
            .mongoDB
            .tradeCollection
            .find(Filters.eq("playerB", profile.uuid))

        val data: MutableList<TradeData> = ArrayList()
        for (tradeData in tradeA) {
            data.add(tradeData)
        }
        for (tradeData in tradeB) {
            data.add(tradeData)
        }

        ThePit.api.openTradeTrackMenu(player, profile, data)
    }

    @Execute(name = "change")
    fun change(
        @Context player: Player,
        @Arg("target") target: Player,
        @Arg("type") type: String,
        @Arg("value") amount: Int,
        @Flag("save") save: Boolean
    ) {
        val profile = PlayerProfile.getPlayerProfileByUuid(target.uniqueId)
        if ("coin".equals(type, ignoreCase = true)) {
            profile.coins = amount.toDouble()
            player.sendMessage("§a已修改玩家硬币")
        }
        if ("prestige".equals(type, ignoreCase = true)) {
            profile.setPrestige(amount)
            player.sendMessage("§a已修改玩家精通")
        }
        if ("renown".equals(type, ignoreCase = true)) {
            profile.setRenown(amount)
            player.sendMessage("§a已修改玩家声望")
        }
        if ("streak".equals(type, ignoreCase = true)) {
            profile.streakKills = amount.toDouble()
            player.sendMessage("§a已修改玩家连杀")
        }
        if ("abounty".equals(type, ignoreCase = true)) {
            profile.setActionBounty(amount)
            player.sendMessage("§a已修改玩家行动赏金")
        }
        if ("level".equals(type, ignoreCase = true)) {
            val levelExpRequired = LevelUtil.getLevelTotalExperience(profile.getPrestige(), amount)
            profile.experience = levelExpRequired
            profile.applyExperienceToPlayer(player)
            player.sendMessage("§a已修改玩家等级")
        }
        if ("bounty".equals(type, ignoreCase = true)) {
            profile.setBounty(amount)
            player.sendMessage("§a已修改玩家赏金")
        }
        if ("maxhealth".equals(type, ignoreCase = true)) {
            target.maxHealth = 20.0 + profile.extraMaxHealthValue
            player.sendMessage("§a已更新玩家血量")
        }
        if (save) {
            profile.saveData()
            player.sendMessage("§a已保存玩家数据")
        }
    }

    @Execute(name = "internalName")
    fun internalName(@Context player: Player, @Arg("name") internalName: String) {
        try {
            player.itemInHand = ItemBuilder(player.itemInHand)
                .internalName(internalName)
                .build()
            player.sendMessage("§a已修改手持物品的内部名为: §f$internalName")
        } catch (ignored: java.lang.Exception) {
            player.sendMessage("Error")
        }
    }

}