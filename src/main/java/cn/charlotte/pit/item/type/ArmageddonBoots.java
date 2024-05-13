package cn.charlotte.pit.item.type;

import cn.charlotte.pit.item.IMythicItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/16 21:57
 */
public class ArmageddonBoots extends IMythicItem {
    @Override
    public String getInternalName() {
        return "armageddon_boots";
    }

    @Override
    public String getItemDisplayName() {
        return "&c末日之靴";
    }

    @Override
    public Material getItemDisplayMaterial() {
        return Material.LEATHER_BOOTS;
    }

    @Override
    public ItemStack toItemStack() {
        return null;
    }

    @Override
    public void loadFromItemStack(ItemStack item) {

    }
}
