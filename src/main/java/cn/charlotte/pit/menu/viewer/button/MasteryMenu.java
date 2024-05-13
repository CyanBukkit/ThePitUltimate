package cn.charlotte.pit.menu.viewer.button;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2022/12/12<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class MasteryMenu extends Menu {
    private PlayerProfile target;
    private int page = 0;


    public MasteryMenu(PlayerProfile target) {
        this.target = target;
    }

    @Override
    public String getTitle(Player player) {
        return target.getPlayerName() + " 的天赋精通";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        int num = 45 * page;

        List<PerkData> list = target.getUnlockedPerk();
        int slot = 0;
        for (int i = 0; i < 45; i++) {
            if (list.size() <= i + num) {
                break;
            }
            buttonMap.put(slot, new ShowUnlockPerkButton(list.get(i + num)));
            slot++;
        }
        buttonMap.put(53, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ARROW)
                        .name("下一页")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
                page++;
            }

            @Override
            public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
                return true;
            }
        });
        return buttonMap;
    }
}
