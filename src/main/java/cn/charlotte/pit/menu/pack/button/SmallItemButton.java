package cn.charlotte.pit.menu.pack.button;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.events.impl.CarePackageEvent;
import cn.charlotte.pit.events.impl.SmallCarePackageEvent;
import cn.charlotte.pit.menu.pack.SmallPackageMenu;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.charlotte.pit.util.menu.buttons.DisplayButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/30 17:20
 */
public class SmallItemButton extends DisplayButton {
    private final SmallPackageMenu packageMenu;

    public SmallItemButton(SmallPackageMenu parent, ItemStack itemStack) {
        super(itemStack, true);
        packageMenu = parent;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
        super.clicked(player, slot, clickType, hotbarButton, currentItem);
        player.closeInventory();
        SmallCarePackageEvent.ChestData chestData = SmallCarePackageEvent.getChestData();
        if (chestData.getRewarded().contains(player.getUniqueId())) {
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
            player.sendMessage(CC.translate("&6&l空投! &c你已经从空投箱中找到过一件物品了"));
            return;
        }
        if (!packageMenu.getItems().containsKey(slot)) {
            player.closeInventory();
            return;
        }
        ItemStack itemStack = packageMenu.getItems().remove(slot);
        if (itemStack != null) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if ("xp_reward".equals(ItemUtil.getInternalName(itemStack))) {
                profile.setExperience(profile.getExperience() + packageMenu.getRange());
                player.sendMessage(CC.translate("&6&l空投! &7你从空投箱中找到了 &b"+packageMenu.getRange()+" &7经验"));
            } else if ("coin_reward".equals(ItemUtil.getInternalName(itemStack))) {
                profile.setCoins(profile.getCoins() + packageMenu.getRange());
                player.sendMessage(CC.translate("&6&l空投! &7你从空投箱中找到了 &6"+packageMenu.getRange()+" &7硬币"));
            } else if ("renown_reward".equals(ItemUtil.getInternalName(itemStack))) {
                profile.setRenown(profile.getRenown() + 1);
                player.sendMessage(CC.translate("&6&l空投! &7你从空投箱中找到了 &e1 &7声望"));
            } else {
                player.getInventory().addItem(itemStack);
                CC.boardCast(MessageType.EVENT, p -> "&6&l空投! " + profile.getFormattedName(p) + "&7 从空投箱中找到了 " + itemStack.getItemMeta().getDisplayName());
            }
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            chestData.getRewarded().add(player.getUniqueId());
        }
        EventFactory eventFactory = ThePit.getInstance()
                .getEventFactory();
        if (packageMenu.getItems().isEmpty() && "small_care_package".equals(eventFactory.getActiveNormalEventName())) {
            eventFactory
                    .inactiveEvent(eventFactory.getActiveNormalEvent());
        }
    }
}
