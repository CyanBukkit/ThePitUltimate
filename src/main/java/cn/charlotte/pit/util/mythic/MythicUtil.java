package cn.charlotte.pit.util.mythic;

import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.type.mythic.GemSwordItem;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.util.item.ItemUtil;
import org.bukkit.inventory.ItemStack;

public class MythicUtil {

    /**
     * @param itemStack
     * @return 返回物品读取为IMythicItem后的形式, 如此物品不是MythicItem则返回null
     * (此物品需要能被附魔)
     */
    public static IMythicItem getMythicItem(ItemStack itemStack) {

        String internalName = ItemUtil.getInternalName(itemStack);
        if (internalName == null) {
            return null;
        }
        IMythicItem mythicItem;
        switch (internalName) {
            case "mythic_sword": {
                mythicItem = new MythicSwordItem();
                break;
            }
            case "mythic_bow": {
                mythicItem = new MythicBowItem();
                break;
            }
            case "mythic_leggings": {
                mythicItem = new MythicLeggingsItem();
                break;
            }
            case "gem_sword": {
                mythicItem = new GemSwordItem();
                break;
            }
            default:
                return null;
        }
        mythicItem.loadFromItemStack(itemStack);
        return mythicItem;

    }


}
