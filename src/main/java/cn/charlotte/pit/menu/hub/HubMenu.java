package cn.charlotte.pit.menu.hub;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Misoryan
 * @since 2021/2/4 10:03
 */
public class HubMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "加入武林争霸";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> button = new HashMap<>();
        button.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BIRCH_DOOR_ITEM).name("&a武林争霸").lore("&7左键点累了吗?", " ", "&7不妨加入武林争霸"," ","&7休闲娱乐又解压"," ","&7进入即可体验极致的舒适"," ","&e点击加入武林争霸!").build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
/*                Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> player.chat("/" + NewConfiguration.INSTANCE.getLobbyCommand()));*/
                Bukkit.getScheduler().runTask(ThePit.getInstance(),() -> ThePit.getInstance().connect(player,"G_RPG#3"));
            }
        });
        return button;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }
}
