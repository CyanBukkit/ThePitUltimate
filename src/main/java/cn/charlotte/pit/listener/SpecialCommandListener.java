package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.network.Broadcast04Packet;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author: EmptyIrony
 * @Date: 2021/4/25 11:19
 */
@AutoRegister
public class SpecialCommandListener implements Listener {


    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event){
        if (event.getMessage().toLowerCase().startsWith("/bc")){
            if (!event.getPlayer().hasPermission("pit.admin")) return;

            this.handleBc(event.getPlayer(),event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (event.getMessage().toLowerCase().startsWith("/run")) {
            if (!event.getPlayer().hasPermission("pit.admin")) return;
            this.handleRun(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (event.getMessage().toLowerCase().startsWith("/sudo")) {
            if (!event.getPlayer().hasPermission("pit.admin")) return;
            this.handleSudo(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
            return;
        }
    }

    private void handleBc(Player player,String msg){
        if (msg.length() < 4){
            player.sendMessage(CC.translate("&c/bc [msg]"));
            return;
        }

        String content = msg.substring(4);
        final Broadcast04Packet packet = new Broadcast04Packet(CC.translate("&c[天坑乱斗公告] &a" + content));
        ThePit.getInstance()
                .getPidgin()
                .sendPacket(packet);

        player.sendMessage(CC.translate("&a发送成功!"));
    }

    private void handleSudo(Player player, String msg) {
        msg = msg.substring(1);
        final String[] split = msg.split(" ");
        if (split.length < 3) {
            player.sendMessage(CC.translate("&cCorrect Usage: /sudo <player> <command>"));
            return;
        }
        Player target = Bukkit.getPlayer(split[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(CC.translate("Target player is offline!"));
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < split.length; i++) {
            builder.append(split[i])
                    .append(" ");
        }

        final String substring = builder.substring(0, builder.length() - 1);

        target.chat("/" + substring);
        player.sendMessage(CC.translate("&aExecuting &e" + target.getName() + " &ato run command: &e/" + substring));
    }

    private void handleRun(Player player, String msg) {
        msg = msg.substring(1);
        final String[] split = msg.split(" ");
        if (split.length == 0 || split.length == 1 || split.length == 2) {
            player.sendMessage(CC.translate("&c/run [numb] [delay] [command]"));
            return;
        }

        int num;
        try{
           num = Integer.parseInt(split[1]);
        }catch (Exception ignore){
            player.sendMessage(CC.translate("&c/run [numb] [delay] [command]"));
            return;
        }

        int delay;
        try{
            delay = Integer.parseInt(split[2]);
        }catch (Exception ignore){
            player.sendMessage(CC.translate("&c/run [numb] [delay] [command]"));
            return;
        }

        if (num <= 0){
            player.sendMessage(CC.translate("&c/run [numb] [delay] [command]"));
            return;
        }
        if (delay < 0){
            player.sendMessage(CC.translate("&c/run [numb] [delay] [command]"));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 3; i < split.length; i++) {
            builder.append(split[i])
                    .append(" ");
        }

        final String substring = builder.substring(0, builder.length() - 1);


        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (i >= num){
                    cancel();
                    return;
                }
                player.chat("/" + substring);
                player.sendMessage(CC.translate("&a成功执行: &e/" + substring));
                i++;
            }
        }.runTaskTimer(ThePit.getInstance(),20,delay);
    }

}
