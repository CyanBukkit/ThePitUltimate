package cn.charlotte.pit.menu.gem;

import cn.charlotte.pit.menu.gem.button.ItemGlobalGemButton;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class GlobalAttentionGemMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "宝石点缀";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        var index = 0;
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                if (Utils.canUseGlobalAttGem(item)) {
                    buttonMap.put(index, new ItemGlobalGemButton(item, i));
                    index++;
                }
            }
        }

        return buttonMap;
    }

    public void onClickEvent(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
