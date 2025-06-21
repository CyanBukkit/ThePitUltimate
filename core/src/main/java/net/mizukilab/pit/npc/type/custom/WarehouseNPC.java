package net.mizukilab.pit.npc.type.custom;

import cn.charlotte.pit.data.PlayerProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.mizukilab.pit.menu.warehouse.WarehouseMainMenu;
import net.mizukilab.pit.npc.AbstractCustomEntityNPC;
import net.mizukilab.pit.util.chat.CC;
import net.mizukilab.pit.util.level.LevelUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @Author: Araykal
 * @Date: 2025/6/21
 */
public class WarehouseNPC extends AbstractCustomEntityNPC {

    @Override
    public String getNpcInternalName() {
        return "warehouse_npc";
    }

    @Override
    public List<String> getNpcDisplayName(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        List<String> lines = new ObjectArrayList<>(3);
        lines.add("&6&l寄存所");
        if (profile.getLevel() >= 110) {
            lines.add("&e&l右键查看");
        } else {
            lines.add("&c在 " + LevelUtil.getLevelTag(profile.getPrestige(), 110) + " &c时解锁");
        }
        return lines;
    }

    @Override
    public Location getNpcSpawnLocation() {
        return new Location(org.bukkit.Bukkit.getWorld("world"), 0, 100, 0);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.VILLAGER;
    }

    @Override
    public void handlePlayerInteract(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.getLevel() >= 120) {
            new WarehouseMainMenu().openMenu(player);
        } else {
            player.sendMessage(CC.translate("&c&l等级不足! &7寄存在 " + LevelUtil.getLevelTag(profile.getPrestige(), 110) + " &7时解锁."));
        }
    }

    @Override
    public void customizeEntity(Entity entity) {
    }
} 