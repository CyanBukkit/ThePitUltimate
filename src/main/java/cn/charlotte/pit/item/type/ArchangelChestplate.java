package cn.charlotte.pit.item.type;

import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/16 21:53
 */
public class ArchangelChestplate extends IMythicItem {
    @Override
    public String getInternalName() {
        return "guardian_chest_plate";
    }

    @Override
    public String getItemDisplayName() {
        return "&b大天使之甲";
    }

    @Override
    public Material getItemDisplayMaterial() {
        return Material.DIAMOND_CHESTPLATE;
    }

    @Override
    public ItemStack toItemStack() {
        final List<String> lore = new ArrayList<>();
        lore.add("&7事件物品");
        lore.add("");
        lore.addAll(this.getEnchantLore());
        lore.remove(lore.size() - 1);

        return new ItemBuilder(this.getItemDisplayMaterial())
                .name(this.getItemDisplayName())
                .internalName(this.getInternalName())
                .enchant(this.enchantments)
                .lore(lore)
                .buildWithUnbreakable();
    }

    @Override
    public void loadFromItemStack(ItemStack item) {

    }
}
