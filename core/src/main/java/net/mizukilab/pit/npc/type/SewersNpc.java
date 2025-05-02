package net.mizukilab.pit.npc.type;

import cn.charlotte.pit.ThePit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCAnimation;
import net.mizukilab.pit.menu.sewers.SewersMenu;
import net.mizukilab.pit.util.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Araykal
 * @since 2025/5/2
 */
public class SewersNpc extends SkinNPC {
    @Override
    public String getNpcInternalName() {
        return "Sewers_Caviar";
    }

    @Override
    public List<String> getNpcDisplayName(Player player) {
        List<String> lines = new ObjectArrayList<>(3);
        lines.add("&9&l下水道鱼");
        lines.add("&e&l右键查看");
        return lines;
    }

    @Override
    public NPCAnimation getAnimation() {
        return null;
    }

    @Override
    public Location getNpcSpawnLocation() {
        return ThePit.getInstance().getPitWorldConfig().getSewersFishNpcLocation();
    }

    @Override
    public Skin getNpcSkin() {
        return new Skin("ewogICJ0aW1lc3RhbXAiIDogMTczNDExMjQ5MjM3OSwKICAicHJvZmlsZUlkIiA6ICJmODI3NGFhYWU5MzU0Zjk4YTBlNDZmMTgxZDU3Y2IxYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaG9ydXZhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM3NDFlMmRhMDFjN2NmYWRkOGNiZmVkYjBjYjgzOGUyNWQ1ODExZTZjN2ZiNGY1NTExNTdlYTM5NzNkZTg2MGQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==","jrYnhSxFEDLlk6aZ0epLzBFM17vy9JJKAOigUZhiEVjByBVdtgIf7Xk/pQlv3HDLHlwzB6QVFCPlin24DtDWkcu2wbRAFNs3ImvV86RcfcfIvYilaueesgr4A1NkpvAuwQ1UGqXx2LFZh4koCEtiPIRMCDGW2aVb3ORsx+THUVT82uG+rPWqMt4P44Jgzsouh4GZShV4UTZ2N+A2orqVg0IcinJrU4nFWTID2fVJRWe8ZUqW+gZEkQjVXbuuHvA9uCshJKJ5FrHU+h9iuml1TRS2tD1o45V3vK2z4qcmp014rKPB8gGJ5J4SqWnSKZ+uSsaVeFcCx1nqD/qr66+Yf2DywWwKuiYeVhAr0lieJ7kp9j3xMCNdTf9McR+Na+nWe/YtqYowWcmo5mQ/0edr4nPGRBal0VYAvXuk2AZlpPo0j1eeZbS4zRoFTbjmoV8MNnuH7/xvh3/nKVRBFmDYEygY9jaNsJHMe49xZbb68jfJzjD2iPk3E3m0ekuq32YoEkq6fH5QHcUw2XlX1WyRKIO1mP5M0HGT/pP68/Cv4VBCL/P2RnHB0WMzs6oMEgmJtuiCxHXU5z3hywKpsZy/MPsVt6Bf5Ybow28plJ+WoHa4X/hVPkR7LtzXDI4Ag7G6T7abwRBUqESEVR9NHwMX/lDmYtxnSt/FGesArjKsVt4=");
    }

    @Override
    public void handlePlayerInteract(Player player) {
        new SewersMenu().openMenu(player);
        player.playSound(player.getLocation(), Sound.SILVERFISH_HIT, 1, 1);
    }

    @Override
    public ItemStack getNpcHeldItem() {
        return new ItemBuilder(Material.RAW_FISH).build();
    }
}
