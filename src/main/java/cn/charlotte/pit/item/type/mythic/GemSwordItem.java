package cn.charlotte.pit.item.type.mythic;

import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.IMythicSword;
import cn.charlotte.pit.util.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 2022/10/18<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class GemSwordItem extends IMythicItem implements IMythicSword {
    private int kills;

    @Override
    public String getInternalName() {
        return "gem_sword";
    }

    @Override
    public String getItemDisplayName() {
        return "&b宝石剑";
    }

    @Override
    public Material getItemDisplayMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public boolean deathDrop() {
        return true;
    }

    @Override
    public double getItemDamage() {
        return 7;
    }

    @Override
    public void appendLore(List<String> lore) {
        lore.add("&7击杀: &9" + kills + "&7/&9277");
        lore.add(" ");
    }

    @Override
    public void onSaveItem(ItemBuilder builder) {
        builder.kills(kills);
    }

    @Override
    protected void onLoadItem(ItemStack itemStack, net.minecraft.server.v1_8_R3.ItemStack nmsItem, NBTTagCompound extra) {
        kills = extra.getInt("kills");
    }

    public int kills() {
        return kills;
    }

    public GemSwordItem kills(int kills) {
        this.kills = kills;
        return this;
    }
}
