package cn.charlotte.pit.runnable

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object AnnouncementRunnable : BukkitRunnable() {
    private var index = 0;
    override fun run() {
        val announcement = listOf("&e&l公告! &fMOC天坑乱斗官方交流群: 697126758 &f欢迎您的加入~", "&e&l公告! &f发现BUG找管理员举报可以领取奖励哦~").map { it.replace("&", "§") }
        Bukkit.broadcastMessage(announcement[index])
        index = (index + 1) % announcement.size
    }
}