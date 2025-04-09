package cn.charlotte.pit.item.type;

import cn.charlotte.pit.item.AbstractPitItem;
import cn.charlotte.pit.util.item.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/22 17:14
 */

public class SpireArmor extends AbstractPitItem {
    private final Material material;

    public SpireArmor(Material material) {
        this.material = material;
    }

    @Override
    public String getInternalName() {
        return "spire_armor";
    }

    @Override
    public String getItemDisplayName() {
        return material.name();
    }

    @Override
    public Material getItemDisplayMaterial() {
        return material;
    }

    @Override
    public ItemStack toItemStack() {
        final List<String> lore = this.getEnchantLore();
        lore.add(0, "");
        lore.add(0, "&7事件物品");

        return new ItemBuilder(this.getItemDisplayMaterial())
                .name(this.getItemDisplayName())
                .internalName(this.getInternalName())
                .removeOnJoin(true)
                .deathDrop(true)
                .lore(lore)
                .buildWithUnbreakable();
    }

    @Override
    public void loadFromItemStack(ItemStack item) {

    }

}
