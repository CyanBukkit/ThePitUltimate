package cn.charlotte.pit.npc.type;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.menu.hub.HubMenu;
import dev.jnic.annotation.Include;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/6 16:28
 */
@Include
public class KeeperNPC extends SkinNPC {
    @Override
    public String getNpcInternalName() {
        return "keeper";
    }

    @Override
    public List<String> getNpcDisplayName(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("&2&l看门人");
        lines.add("&7返回大厅");
        return lines;
    }

    @Override
    public Location getNpcSpawnLocation() {
        return ThePit.getInstance().getPitConfig().getKeeperNpcLocation();
    }

    @Override
    public Skin getNpcSkin() {
        return new Skin(
                "eyJ0aW1lc3RhbXAiOjE1ODE5MTIzMjQ4MzQsInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83NzI4MWEwZDVkOWY3OGU4Y2FlOTlmMGVhNDExNDhkYmQ2YjJkZTAyNmEzYzc5NTgyMzg4NjMyMGJhNWVkMDI0In19fQ==",
                null
        );
    }

    @Override
    public void handlePlayerInteract(Player player) {
        new HubMenu().openMenu(player);
    }

    @Override
    public ItemStack getNpcHeldItem() {
        return null;
    }
}
