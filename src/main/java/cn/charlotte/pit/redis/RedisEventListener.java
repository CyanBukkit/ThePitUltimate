package cn.charlotte.pit.redis;


import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.CDKData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.*;
import cn.charlotte.pit.network.*;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.pidgin.packet.handler.IncomingPacketHandler;
import cn.charlotte.pit.util.pidgin.packet.listener.PacketListener;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @Author: EmptyIrony
 * @Date: 2021/3/7 19:04
 */
public class RedisEventListener implements PacketListener {

    @IncomingPacketHandler
    public void onPacket(EventsRefresh05Packet packet){
        EventsHandler.INSTANCE.onReceiveRefresh();
        CC.boardCastWithPermission("&a事件刷新完成", "pit.admin");
    }

    @IncomingPacketHandler
    public void onPacket(CDKActive02Packet packet) {
        Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), CDKData::loadAllCDKFromData);
        CC.boardCastWithPermission("&aCDK刷新完成", "pit.admin");
    }

    @IncomingPacketHandler
    public void onPacket(Broadcast04Packet packet){
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(packet.getMessage());
        });
    }

    @IncomingPacketHandler
    @SneakyThrows
    public void onPacket(EpiEvent00Packet packet) {
        final String internal = packet.getInternalName();

        final EventFactory factory = ThePit.getInstance().getEventFactory();

        if (factory.getActiveEpicEvent() != null) {
            return;
        }
        if (factory.getNextEpicEvent() != null) {
            return;
        }

        final Optional<IEvent> first = factory.getEpicEvents()
                .stream()
                .map(event -> (IEvent) event)
                .filter(event -> event.getEventInternalName().equalsIgnoreCase(internal))
                .findFirst();

        if (!first.isPresent()) {
            return;
        }

        if (!packet.isIgnoreOnline()) {
            if (Bukkit.getOnlinePlayers().size() < first.get().requireOnline()) {
                return;
            }
        }

        final IEpicEvent event = (IEpicEvent) first.get().getClass().newInstance();

        //temp disable cuz some bug
        factory.readyEpicEvent(event);
    }

    @IncomingPacketHandler
    @SneakyThrows
    public void onPacket(MailSend01Packet packet) {
        final Player player = Bukkit.getPlayer(packet.getUuid());
        if (player == null || !player.isOnline()) {
            return;
        }
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.isLoaded()) {
            PlayerProfile.loadMail(profile, player.getUniqueId());
            player.sendMessage("§a§l邮件! §7你收到了一封新邮件! 请在邮件NPC处查看.");
        }
    }

    @IncomingPacketHandler
    @SneakyThrows
    public void onPacket(MiniEvent03Packet packet) {
        final String internal = packet.getInternalName();

        final EventFactory factory = ThePit.getInstance().getEventFactory();

        final Optional<IEvent> first = factory.getNormalEvents()
                .stream()
                .map(event -> (IEvent) event)
                .filter(event -> event.getEventInternalName().equalsIgnoreCase(internal))
                .findFirst();

        if (!first.isPresent()) {
            return;
        }

        if (!packet.isIgnoreOnline()) {
            if (Bukkit.getOnlinePlayers().size() < first.get().requireOnline()) {
                return;
            }
        }

        final INormalEvent event = (INormalEvent) first.get().getClass().newInstance();


        if (factory.getActiveEpicEvent() != null) {
            return;
        }
        if (factory.getNextEpicEvent() != null) {
            return;
        }
        if (factory.getActiveNormalEvent() != null) {
            return;
        }
        if (System.currentTimeMillis() - factory.getLastNormalEvent() <= 3 * 60 * 1000) {
            return;
        }

        factory.activeEvent(event);
    }

}
