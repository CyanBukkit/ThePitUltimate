package cn.charlotte.pit.item.type;

import cn.charlotte.pit.util.item.ItemBuilder;
import dev.jnic.annotation.Include;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Include
public class ChunkOfVileItem {
 static ItemBuilder builder = new ItemBuilder(Material.COAL)
                .name("&5暗聚块")
                .lore(
                        "&7死亡后保留",
                                "",
                                "&c邪术收藏品"
    )
                .canSaveToEnderChest(true)
                .canTrade(true)
                .internalName(getInternalName())
            ;
    public static ItemStack toItemStack() {
        return builder.build();
    }

    public static String getInternalName() {
        return "chunk_of_vile_item";
    }
}
