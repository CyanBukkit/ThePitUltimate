package net.mizukilab.pit.npc.type;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import net.mizukilab.pit.menu.quest.main.QuestMenu;
import net.mizukilab.pit.util.chat.CC;
import net.mizukilab.pit.util.level.LevelUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/19 18:38
 */

public class QuestNpc extends SkinNPC {

    @Override
    public String getNpcInternalName() {
        return "quest";
    }

    @Override
    public List<String> getNpcDisplayName(Player player) {
        List<String> lines = new ObjectArrayList<>(3);
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        lines.add("&3&l任务");
        if (profile.getLevel() >= 30 || profile.getPrestige() > 0) {
            lines.add("&e&l右键查看");
        } else {
            lines.add("&c在 " + LevelUtil.getLevelTag(profile.getPrestige(), 30) + " &c时解锁");
        }
        return lines;
    }

    @Override
    public Location getNpcSpawnLocation() {
        return ThePit.getInstance().getPitConfig().getQuestNpcLocation();
    }

    @Override
    public Skin getNpcSkin() {
        return new Skin("ewogICJ0aW1lc3RhbXAiIDogMTU5OTIxNzI3NjA5NywKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4Yjc5N2M1NjQ4YzQwNDFkNWE0ZTYwYTY1OGMxMjAzMGJiZGQ3OTM4NWRjMzA4NGRlZmVkYzBjZmQ1MmZjNSIKICAgIH0KICB9Cn0=",
                null
        );
    }

    @Override
    public void handlePlayerInteract(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.getLevel() < 30) {
            player.sendMessage(CC.translate("&c&l等级不足! &7任务在 " + LevelUtil.getLevelTag(profile.getPrestige(), 30) + " &7时解锁."));
            return;
        }
        new QuestMenu().openMenu(player);
    }

    @Override
    public ItemStack getNpcHeldItem() {
        return new ItemStack(Material.BOOK);
    }
}
