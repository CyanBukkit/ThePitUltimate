package cn.charlotte.pit.command

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.runnable.dupe.CleanupDupeEnch0525Runnable
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import dev.jnic.annotation.Include
import dev.rollczi.litecommands.annotations.async.Async
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import org.bukkit.command.CommandSender

/**
 * 2024/5/26<br>
 * ThePitPlus<br>
 * @author huanmeng_qwq
 */
@Include
@Command(name = "dupesFix")
@Permission("pit.admin")
class PitAdminDupeFixCommands {
    @Execute(name = "0526")
    @Async
    fun fix0526(@Context sender: CommandSender) {
        val profileCollection = ThePit.getInstance().mongoDB.profileCollection
        profileCollection.find().forEach { profile ->
            var count = 0
            profile.inventory.apply {
                contents = contents.map { itemStack ->
                    CleanupDupeEnch0525Runnable.auto(itemStack).also {
                        if (it.first) {
                            ++count;
                        }
                    }.second
                }.toTypedArray()
                armorContents = armorContents.map { itemStack ->
                    CleanupDupeEnch0525Runnable.auto(itemStack).also {
                        if (it.first) {
                            ++count;
                        }
                    }.second
                }.toTypedArray()
            }

            for ((index, itemStack) in profile.enderChest.inventory.withIndex()) {
                profile.enderChest.inventory.setItem(index, CleanupDupeEnch0525Runnable.auto(itemStack).also {
                    if (it.first) {
                        ++count;
                    }
                }.second)
            }
            profileCollection.replaceOne(
                Filters.eq("uuid", profile.uuid),
                profile,
                ReplaceOptions().upsert(true)
            )
            if (count > 0) {
                sender.sendMessage("已处理玩家${profile.playerName} 的 $count 个物品")
            }
        }
    }
}