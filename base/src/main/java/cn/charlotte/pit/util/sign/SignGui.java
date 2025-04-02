package cn.charlotte.pit.util.sign;

import cn.charlotte.pit.util.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.refinedev.spigot.api.handlers.impl.PacketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/19 21:33
 */
public class SignGui {
    protected Map<String, Vector> signLocations = new ConcurrentHashMap<>();
    protected Map<String, SignGUIListener> listeners = new ConcurrentHashMap<>();
    public SignGui(JavaPlugin plugin) {
        PacketHandler packetHandler = new PacketHandler() {
            @Override
            public void handleReceivedPacket(PlayerConnection playerConnection, Packet<?> packet) {
                if (packet instanceof PacketPlayInUpdateSign sign) {
                    final Player player = playerConnection.player.getBukkitEntity();
                    Vector v = signLocations.remove(player.getName());
                    BlockPosition bp = sign.a();
                    final IChatBaseComponent[] chatarray = sign.b();
                    final String[] lines = new String[4];
                    for (int i = 0; i < chatarray.length; i++) {
                        lines[i] = EnumChatFormat.a(chatarray[i].c());
                    }
                    final SignGUIListener response = listeners.remove(playerConnection.player.getBukkitEntity().getName());

                    if (v == null) {
                        return;
                    }
                    if (bp.getX() != v.getBlockX()) {
                        return;
                    }
                    if (bp.getY() != v.getBlockY()) {
                        return;
                    }
                    if (bp.getZ() != v.getBlockZ()) {
                        return;
                    }

                    if (response != null) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> response.onSignDone(player, lines));
                        throw CancelledPacketHandleException.INSTANCE;
                    }
                }
            }

            @Override
            public void handleSentPacket(PlayerConnection playerConnection, Packet<?> packet) {
            }
        };
        Utils.addCommonHandler(packetHandler);
    }



    public void open(Player player, String[] messages, SignGUIListener response) {
        Location loc = new Location(player.getWorld(), 0, 1, 0);
        player.sendBlockChange(loc, Material.SIGN_POST, (byte) 0);
        player.sendSignChange(loc, messages);

        try {
            EntityPlayer handle = ((CraftPlayer) player).getHandle();
            PlayerConnection connection = handle.playerConnection;

            PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(
                    new BlockPosition(
                            loc.getX(),
                            loc.getY(),
                            loc.getZ()
                    )
            );

            connection.sendPacket(packet);
            signLocations.put(player.getName(), loc.toVector());
            listeners.put(player.getName(), response);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public interface SignGUIListener {
        void onSignDone(Player player, String[] lines);
    }

}
