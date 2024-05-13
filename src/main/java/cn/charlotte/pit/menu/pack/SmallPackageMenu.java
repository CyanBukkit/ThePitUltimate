package cn.charlotte.pit.menu.pack;

import cn.charlotte.pit.events.impl.SmallCarePackageEvent;
import cn.charlotte.pit.menu.pack.button.ItemButton;
import cn.charlotte.pit.menu.pack.button.SmallItemButton;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/30 17:19
 */
public class SmallPackageMenu extends Menu {
    private SmallCarePackageEvent event;
    private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    @Getter
    private final int range;

    public SmallPackageMenu(SmallCarePackageEvent event, int range) {
        this.event = event;
        this.range = range;
    }

    @Override
    public String getTitle(Player player) {
        return "空投";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            map.put(entry.getKey(), new SmallItemButton(this, entry.getValue()));
        }
        return map;
    }

    @Override
    public void openMenu(Player player) {
        player.closeInventory();
        super.openMenu(player);
    }

    public SmallCarePackageEvent getEvent() {
        return event;
    }
}
