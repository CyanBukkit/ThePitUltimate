package cn.charlotte.pit.item.type;

import cn.charlotte.pit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * 2022/10/21<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class UberReward {
    public static ItemStack toItemStack() {
        return new ItemBuilder(Material.ENDER_CHEST)
                .internalName("reward_uber").name("&d登峰造极掉落物")
                .lore("&7右键获取!", " ", "§c打开前请确认背包是否有1-3格的空余位置!")
                .canSaveToEnderChest(true)
                .canDrop(false).deathDrop(false)
                .build();
    }
}
