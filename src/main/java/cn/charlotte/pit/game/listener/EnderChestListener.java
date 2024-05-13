package cn.charlotte.pit.game.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.perk.type.prestige.ExtraEnderchestPerk;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/7 12:45
 */
@AutoRegister
public class EnderChestListener implements Listener {
    private final Map<UUID, Location> locationCache = new HashMap<>();
    private final Map<UUID, Cooldown> openCooldown = new HashMap<>();

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals("末影箱")) {
            if (event.getClick() == ClickType.NUMBER_KEY || event.getAction().name().contains("HOTBAR")) {
                event.setCancelled(true);
                return;
            }
            if ("not_unlock_slot".equals(ItemUtil.getInternalName(event.getCurrentItem()))) {
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && !ItemUtil.canSaveEnderChest(event.getCurrentItem())) {
                    event.setCancelled(true);
                }
            } else {
                if (!event.getCursor().getType().equals(Material.AIR) && !ItemUtil.canSaveEnderChest(event.getCursor())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && event.getInventory().getName().equals("末影箱")) {
            final Location location = locationCache.remove(event.getPlayer().getUniqueId());

            if (location != null) {
                ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.CHEST_CLOSE, 1, 0.6F);

                BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.ENDER_CHEST, (byte) 1, (byte) 0);
                ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    @EventHandler
    public void onInternal(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.
                ENDER_CHEST) {
            event.setCancelled(true);

            if (!event.getPlayer().hasPermission("pit.admin")){
                Cooldown cooldown = this.openCooldown.get(event.getPlayer().getUniqueId());
                if (cooldown != null && !cooldown.hasExpired()){
                    event.getPlayer().sendMessage(CC.translate("&c请过一会儿再打开末影箱"));
                    return;
                }
                this.openCooldown.put(event.getPlayer().getUniqueId(),new Cooldown(3, TimeUnit.SECONDS));
            }

            final EventFactory factory = ThePit.getInstance().getEventFactory();
            if ("spire".equals(factory.getActiveEpicEventName())){
                event.getPlayer().sendMessage(CC.translate("&c末影箱当前被禁用了!"));
                return;
            }

            final Location location = event.getClickedBlock().getLocation();
            final Player player = event.getPlayer();
            final PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

            if (profile.isInArena()) {
                return;
            }

            for (PerkData perkData : profile.getUnlockedPerk()) {
                if (perkData.getPerkInternalName().equals(new ExtraEnderchestPerk().getInternalPerkName())) {
                    profile.setEnderChestRow(3 + perkData.getLevel());
                    break;
                }
            }
            profile.getEnderChest().openEnderChest(player);
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 0.6F);

            this.locationCache.put(player.getUniqueId(), location);

            BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.ENDER_CHEST, (byte) 1, (byte) 1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
