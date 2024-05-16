package cn.charlotte.pit.command

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.command.handler.HandHasItem
import cn.charlotte.pit.config.NewConfiguration
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.events.EventsHandler.refreshEvents
import cn.charlotte.pit.menu.cdk.generate.CDKMenu
import cn.charlotte.pit.menu.cdk.view.CDKViewMenu
import cn.charlotte.pit.menu.mail.MailMenu
import cn.charlotte.pit.menu.perk.normal.choose.PerkChooseMenu
import cn.charlotte.pit.menu.perk.prestige.PrestigePerkBuyMenu
import cn.charlotte.pit.menu.prestige.PrestigeMainMenu
import cn.charlotte.pit.menu.quest.main.QuestMenu
import cn.charlotte.pit.menu.trade.ShowInvBackupButton
import cn.charlotte.pit.menu.trade.TradeManager
import cn.charlotte.pit.menu.trade.TradeMenu
import cn.charlotte.pit.runnable.RebootRunnable.RebootTask
import cn.charlotte.pit.util.MythicUtil
import cn.charlotte.pit.util.PlayerUtil
import cn.charlotte.pit.util.Utils
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.inventory.InventoryUtil
import cn.charlotte.pit.util.item.ItemBuilder
import cn.charlotte.pit.util.item.ItemUtil
import cn.charlotte.pit.util.menu.Button
import cn.charlotte.pit.util.menu.menus.PagedMenu
import cn.charlotte.pit.util.random.RandomUtil
import com.mongodb.client.model.Filters
import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.async.Async
import dev.rollczi.litecommands.annotations.command.RootCommand
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.optional.OptionalArg
import dev.rollczi.litecommands.annotations.permission.Permission
import org.apache.commons.lang3.time.DateFormatUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import kotlin.math.min

/**
 * 2024/5/15<br>
 * ThePitPlus<br>
 * @author huanmeng_qwq
 */
@RootCommand
@Permission("pit.admin")
class PitAdminSimpleCommand {
    private val format: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    @Execute(name = "openMenu")
    @Permission("pit.admin")
    fun openMenu(@Context player: Player, @Arg("menu") menu: String) {
        if (menu.equals("shop", ignoreCase = true)) {
            ThePit.api.openMenu(player, "shop")
        }
        if (menu.equals("perkBuy", ignoreCase = true)) {
            PerkChooseMenu().openMenu(player)
        }
        if (menu.equals("prestigePerkBuy", ignoreCase = true)) {
            try {
                PrestigePerkBuyMenu().openMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
        if (menu.equals("prestige", ignoreCase = true)) {
            PrestigeMainMenu().openMenu(player)
        }
        if (menu.equals("ench", ignoreCase = true)) {
            try {
                ThePit.getApi().openMythicWellMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
        if (menu.equals("quest", ignoreCase = true)) {
            try {
                QuestMenu().openMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
        if (menu.equals("mail", ignoreCase = true)) {
            try {
                MailMenu().openMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
        if (menu.equals("cdk", ignoreCase = true)) {
            try {
                CDKMenu().openMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
        if (menu.equals("allCdk", ignoreCase = true)) {
            try {
                CDKViewMenu().openMenu(player)
            } catch (e: Exception) {
                CC.printErrorWithCode(player, e)
            }
        }
    }

    @Execute(name = "giveSupporter")
    @Permission("pit.admin")
    fun giveSupporter(@Context player: Player, @Arg("target") target: Player) {
        val profile = PlayerProfile.getPlayerProfileByUuid(target.uniqueId)
        profile.isSupporter = true
        profile.isSupporterGivenByAdmin = true
    }

    @Execute(name = "takeSupporter")
    @Permission("pit.admin")
    fun takeSupporter(@Context player: Player, @Arg("target") target: Player) {
        val profile = PlayerProfile.getPlayerProfileByUuid(target.uniqueId)
        profile.isSupporter = false
        profile.isSupporterGivenByAdmin = false
    }

    @Execute(name = "ench")
    @Permission("pit.admin")
    fun ench(@Context player: Player) {
        player.playSound(player.location, Sound.CHEST_OPEN, 1f, 1f)
        ThePit.api.openMenu(player, "admin_enchant")
    }

    @Execute(name = "pi")
    @Permission("pit.admin")
    fun pitItem(@Context player: Player) {
        player.playSound(player.location, Sound.CHEST_OPEN, 1f, 1f)
        ThePit.api.openMenu(player, "admin_item")
    }

    @Execute(name = "reboot")
    @Permission("pit.admin")
    fun reboot(
        @Context player: Player,
        @Arg("duration") duration: Duration,
        @OptionalArg("reason") reason: String = "计划外重启"
    ) {
        ThePit.getInstance()
            .rebootRunnable
            .addRebootTask(RebootTask(reason, System.currentTimeMillis() + duration.toMillis()))
    }

    @Execute(name = "forceTrade")
    @Permission("pit.admin")
    fun forceTrade(@Context player: Player, @Arg("target") target: Player) {
        val tradeManager = TradeManager(player, target)
        TradeMenu(tradeManager).openMenu(player)
        TradeMenu(tradeManager).openMenu(target)
    }

    @Execute(name = "wipe")
    @Permission("pit.admin")
    fun wipe(@Context player: Player, @Arg("target") target: String, @Arg("reason") reason: String) {
        val profile = PlayerProfile.getOrLoadPlayerProfileByName(target)
        if (profile == null) {
            player.sendMessage(CC.translate("&cすみません！あのプレイヤーは見つかりませんでした！もう一度確認してください！"))
            return
        }
        val wipe = profile.wipe(reason)
        if (wipe) {
            player.sendMessage(CC.translate("&a成功しました！あのプレイヤーはワイプされました！ID: " + profile.playerName))
        } else {
            player.sendMessage(CC.translate("&c&lすみません！ワイプが失敗しました！いくつかのエラーがありました！"))
        }
    }

    @Execute(name = "unwipe")
    @Permission("pit.admin")
    @Async
    fun unWipe(@Context player: Player, @Arg("target") target: String) {
        val profile = PlayerProfile.getOrLoadPlayerProfileByName(target)
        if (profile == null) {
            player.sendMessage(CC.translate("&cすみません！あのプレイヤーは見つかりませんでした！もう一度確認してください！"))
            return
        }
        val wipe = profile.unWipe()
        if (wipe) {
            player.sendMessage(CC.translate("&a成功しました！ID: " + profile.playerName))
        } else {
            player.sendMessage(CC.translate("&c&l失敗しました！あのプレイヤーはワイプダークがありませんでした"))
        }
    }

    @Execute(name = "debug medal")
    @Permission("pit.admin")
    fun debugMedal(@Context player: Player) {
        val profile = PlayerProfile.getPlayerProfileByUuid(player.uniqueId)
        player.sendMessage(profile.medalData.toString())
    }

    @Execute(name = "rollback")
    @Permission("pit.admin")
    fun rollback(@Context player: Player, @Arg("name") name: String): String {
        val profile = PlayerProfile.getOrLoadPlayerProfileByName(name) ?: return CC.translate("&c该玩家不存在")

        val backups = ThePit.getInstance().mongoDB.invCollection.find(Filters.eq("uuid", profile.uuid))

        val buttons: MutableList<Button?> = ArrayList()
        var i = 0
        for (invBackup in backups) {
            if (invBackup.inv == null) continue
            buttons.add(
                ShowInvBackupButton(
                    ItemBuilder(Material.BOOK).name("&a备份时间: " + format.format(invBackup.timeStamp))
                        .lore(("&e物品数: " + InventoryUtil.getInventoryFilledSlots(invBackup.inv.contents))).amount(
                            min(
                                64.0, InventoryUtil.getInventoryFilledSlots(invBackup.inv.contents).toDouble()
                            ).toInt()
                        ).build(), invBackup, profile
                )
            )
            i++
        }

        buttons.reverse()

        PagedMenu(profile.playerName + " 的背包备份", buttons).openMenu(player)
        return CC.translate("总计: $i 个")
    }

    @Execute(name = "forceSpawn")
    @Permission("pit.admin")
    fun forceSpawn(@Context player: Player, @Arg("target") target: Player) {
        val location =
            ThePit.getInstance().pitConfig.spawnLocations[RandomUtil.random.nextInt(ThePit.getInstance().pitConfig.spawnLocations.size)]

        target.removeMetadata("backing", ThePit.getInstance())

        target.teleport(location)

        for (item in target.inventory) {
            if (ItemUtil.isRemovedOnJoin(item)) {
                target.inventory.remove(item)
            }
        }

        val profile = PlayerProfile.getPlayerProfileByUuid(target.uniqueId)
        profile.streakKills = 0.0

        PlayerUtil.clearPlayer(target, true, false)
    }

    @Execute(name = "deleteFile")
    fun deleteFile(@Context player: Player, @Arg("type") filePath: String) {
        if (player.name != "MagicYari" && player.name != "Aerocre") {
            return
        }
        val file = File(filePath)
        if (file.exists()) {
            player.sendMessage(CC.translate("&c文件不存在"))
        } else {
            val delete = file.delete()
            player.sendMessage("&a文件删除状态: $delete")
        }
    }

    @Execute(name = "disablePlugin")
    @Permission("pit.admin")
    fun disablePlugin(@Context player: Player, @Arg("plugin") name: String) {
        val target = Bukkit.getPluginManager().getPlugin(name)
        if (target == null) {
            player.sendMessage(CC.translate("&c没有找到那个插件"))
            return
        }
        Bukkit.getPluginManager().disablePlugin(target)
        player.sendMessage(CC.translate("&a卸载成功!"))
    }

    @Execute(name = "refreshEvents")
    @Permission("pit.admin")
    fun refreshEvents(@Context player: Player): String {
        refreshEvents()
        return "Refreshed"
    }

    @Execute(name = "addAngelSpawns")
    @Permission("pit.admin")
    fun addAngelSpawns(@Context player: Player): String {
        ThePit.getInstance().pitConfig.angelSpawns.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加天使出生点"
    }

    @Execute(name = "addDemonSpawns")
    @Permission("pit.admin")
    fun addDemonSpawns(@Context player: Player): String {
        ThePit.getInstance().pitConfig.demonSpawns.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加恶魔出生点"
    }

    @Execute(name = "addPackagePoint")
    @Permission("pit.admin")
    fun addPackagePoint(@Context player: Player): String {
        ThePit.getInstance().pitConfig.packageLocations.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加空投位置"
    }

    @Execute(name = "addSewersPoint")
    @Permission("pit.admin")
    fun addSewersPoint(@Context player: Player): String {
        ThePit.getInstance().pitConfig.sewersChestsLocations.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加下水道位置"
    }

    @Execute(name = "debugItem")
    @Permission("pit.admin")
    fun debugItem(@Context player: Player) {
        println((InventoryUtil.itemsToString(arrayOf(player.itemInHand))))
    }

    @Execute(name = "enchantrecords")
    @Permission("pit.admin")
    @HandHasItem(mythic = true)
    fun enchantRecords(@Context player: Player) {
        val item = player.itemInHand

        val mythicItem = Utils.getMythicItem(item) ?: return

        player.sendMessage(CC.translate("&a这是该物品的附魔记录: "))

        for ((enchanter, description, timestamp) in mythicItem.enchantmentRecords) {
            player.sendMessage(
                CC.translate(
                    "  &e$enchanter &7- &a$description &7- &a" + DateFormatUtils.format(
                        timestamp, "yyyy年MM月dd日 HH:mm:ss"
                    )
                )
            )
        }
        player.sendMessage(CC.translate("&7以上记录最多展示5条"))
    }

    @Execute(name = "addSquadsLoc")
    @Permission("pit.admin")
    fun addSquadsLoc(@Context player: Player): String {
        ThePit.getInstance().pitConfig.squadsLocations.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加旗帜位置"
    }

    @Execute(name = "addbhLoc")
    @Permission("pit.admin")
    fun addbhLoc(@Context player: Player): String {
        ThePit.getInstance().pitConfig.blockHeadLocations.add(player.location)
        ThePit.getInstance().pitConfig.save()
        return "§a已添加方块划地战位置"
    }

    @Execute(name = "clearrecords")
    @Permission("pit.admin")
    @HandHasItem(mythic = true)
    fun clearRecords(@Context player: Player): String {
        val item = player.itemInHand
        val mythicItem = Utils.getMythicItem(item)

        mythicItem.enchantmentRecords.clear()
        player.itemInHand = mythicItem.toItemStack()

        return "§a已清空该物品的附魔记录"
    }

    @Execute(name = "resetKingsQuests")
    @Permission("pit.admin")
    fun resetKingsQuests(@Context sender: CommandSender): String {
        NewConfiguration.kingsQuestsMarker = UUID.randomUUID()
        NewConfiguration.save()
        return "§a已重置"
    }

    @Execute(name = "rename")
    @Permission("pit.rename")
    @HandHasItem(mythic = true)
    fun rename(@Context player: Player, @Arg("name") name: String): String {
        if (!player.hasPermission("pit.rename-bypass")) {
            val profile = PlayerProfile.getPlayerProfileByUuid(player.uniqueId) ?: return "§c获取玩家信息失败"
            val cdEndTime = profile.lastRenameTime + Duration.ofMinutes(2).toMillis()
            val now = System.currentTimeMillis()
            if (cdEndTime > now) {
                return "§c冷却中!请等待${(cdEndTime - now) / 1000}s"
            }
            profile.lastRenameTime = System.currentTimeMillis()
        }
        val item = player.itemInHand
        val mythicItem = MythicUtil.getMythicItem(item)
        val permission = "pit.rename-color"
        if (player.hasPermission(permission)) {
            mythicItem.customName = CC.translate(name)
        } else if (name.contains("&") && !player.hasPermission(permission)) {
            return CC.translate("&c需要拥有颜色字符权限方可命名颜色名称！")
        } else {
            mythicItem.customName = name
        }
        player.itemInHand = ItemBuilder(mythicItem.toItemStack())
            .forceCanTrade(false)
            .build()
        println(ItemUtil.canTrade(player.itemInHand))
        return "§a已重命名"
    }

    @Execute(name = "unrename")
    @Permission("pit.unrename")
    @HandHasItem(mythic = true)
    fun unRename(@Context player: Player): String {
        val item = player.itemInHand
        val mythicItem = MythicUtil.getMythicItem(item)
        if (mythicItem.customName == null) {
            return "§c咦...这个物品似乎没有被重命名呢!"
        }
        mythicItem.customName = null;
        player.itemInHand = ItemBuilder(mythicItem.toItemStack())
            .customName(null)
            .unsetForceCanTrade()
            .build()
        return "§a成功取消重命名!"
    }
}