package cn.charlotte.pit.menu.hub.game;


import cn.charlotte.pit.util.ConnectServer;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SakuraBlowSeason extends Button {
    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.REDSTONE).name("&d樱风纪").lore("&7天坑刷累了?", "&7也不想返回大厅?","&7那就来试试樱风纪吧", "&e点击传送至&d樱风纪!").build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
        ConnectServer.connect(player,"rpg");
    }
}
