package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.event.PotionAddEvent;
import cn.charlotte.pit.events.impl.major.RedVSBlueEvent;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.item.ItemBuilder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.jnic.annotation.Include;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.spigot.api.handlers.impl.PacketHandler;

import java.lang.reflect.Field;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/4 14:56
 */
public class PacketListener implements PacketHandler {


    @Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet<?> packet) {

    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet<?> packet) {
        try {
            EntityPlayer player = playerConnection.player;
            if (packet instanceof PacketPlayOutEntityEquipment) {
                if ("red_vs_blue".equals(ThePit.getInstance().getEventFactory().getActiveEpicEventName())) {
                    PacketPlayOutEntityEquipment equipment = (PacketPlayOutEntityEquipment) packet;

                    if (equipment.b == 4) {
                        RedVSBlueEvent activeEpicEvent = (RedVSBlueEvent) ThePit.getInstance().getEventFactory().getActiveEpicEvent();
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (target.getEntityId() == (int) equipment.a) {
                                if (activeEpicEvent.getRedTeam().contains(target.getUniqueId())) {
                                    equipment.c = Utils.toNMStackQuick(new ItemBuilder(Material.WOOL).durability(14).build());
                                } else if (activeEpicEvent.getBlueTeam().contains(target.getUniqueId())) {
                                    equipment.c = Utils.toNMStackQuick(new ItemBuilder(Material.WOOL).durability(11).build());
                                }
                                break;
                            }
                        }
                    }
                }
            } else if (packet instanceof PacketPlayOutEntityEffect packt) {

                if (player.getId() != packt.a) return;

                final PotionEffectType potion = PotionEffectType.getById(packt.b);
                final byte level = packt.c;
                final int duration = packt.d;
                final boolean hide = packt.e == 1;

                Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
                    PotionAddEvent potionEvent = new PotionAddEvent(player.getBukkitEntity(), new PotionEffect(potion, level, duration, hide));
                    potionEvent.callEvent();

                    if (potionEvent.isCancelled()) {
                        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
                            player.getBukkitEntity().removePotionEffect(potion);
                        }, 1L);
                    }
                }, 1L);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
