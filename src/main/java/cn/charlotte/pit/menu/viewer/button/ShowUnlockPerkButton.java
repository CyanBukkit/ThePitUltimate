package cn.charlotte.pit.menu.viewer.button;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * 2022/12/12<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class ShowUnlockPerkButton extends Button {
    private static final List<AbstractPerk> perks = ThePit.getInstance().getPerkFactory().getPerks();
    private PerkData perkData;

    public ShowUnlockPerkButton(PerkData perkData) {
        this.perkData = perkData;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        AbstractPerk abstractPerk = perks.stream().filter(e -> Objects.equals(e.getInternalPerkName(), perkData.getPerkInternalName())).findFirst().orElse(null);
        if (abstractPerk != null) {
            return abstractPerk.getIconWithNameAndLore(abstractPerk.getDisplayName(), abstractPerk.getDescription(player), 0, perkData.getLevel());
        }
        return new ItemBuilder(Material.BARRIER)
                .name("§c未知的天赋")
                .lore("§7这个天赋已经不存在了",
                        " ",
                        "name: " + perkData.getPerkInternalName(),
                        "level: " + perkData.getLevel()
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {

    }
}
