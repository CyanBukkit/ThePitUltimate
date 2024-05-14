package cn.charlotte.pit.item.type.mythic;

import cn.charlotte.pit.item.IMythicItem;
import dev.jnic.annotation.Include;
import org.bukkit.Material;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/17 23:14
 */
@Include
public class MythicLeggingsItem extends IMythicItem {

    @Override
    public String getInternalName() {
        return "mythic_leggings";
    }

    @Override
    public String getItemDisplayName() {
        return "神话之甲";
    }

    @Override
    public Material getItemDisplayMaterial() {
        return Material.LEATHER_LEGGINGS;
    }
}
