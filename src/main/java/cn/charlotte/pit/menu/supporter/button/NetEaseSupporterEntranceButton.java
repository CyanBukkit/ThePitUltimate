package cn.charlotte.pit.menu.supporter.button;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.medal.impl.challenge.hidden.SupporterMedal;
import cn.charlotte.pit.menu.supporter.SupporterMenu;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.menus.ConfirmMenu;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NetEaseSupporterEntranceButton extends Button {
    public static final int POINTS = 485;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add("&7展示您对天坑乱斗游戏的支持,");
        lines.add("&7并解锁专属的会员权益!");
        lines.add("");
        lines.add("&7会员权益:");
        lines.add("&e◼ &7可开关的 &e✬ &7会员标志显示");
        lines.add("&e◼ &7/events 查看范围两天内的&d事件&7排布表");
        lines.add("&e◼ &7/show 展示手中物品给房间内全部玩家");
        lines.add("&e◼ &7将 &c神&6话&e之&a甲 &7以专属染料进行染色");
        lines.add("&e◼ &7可自选的隐藏自身档案 (无法被他人/view查看)");
        lines.add("");
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

        try {
            boolean price = PlayerPoints.getPlugin(PlayerPoints.class).getAPI().look(player.getUniqueId()) >= POINTS; //get amount of player's point here
            if (profile.isSupporter()) {
                lines.add("&a&l感谢您的购买!");
            } else {
                lines.add("&7点券: &d" + POINTS); //remember to edit the price
                lines.add(" ");
                lines.add("&8当会员权益增加新内容时,");
                lines.add("&8已购买的用户会自动获得新的会员权益.");
            }
            lines.add(" ");
            if (profile.isSupporter()) {
                lines.add("&e点击调整相关设定!");
            } else {
                lines.add((price ? "&a点击购买!" : "&c按F4来打开点券商店!")); //check if player have enough points
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ItemBuilder(Material.BEACON)
                .name("&e天坑乱斗会员")
                .lore(lines)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.isSupporter()) {
            new SupporterMenu().openMenu(player);
        } else {
            try {
                boolean haveEnoughPoints = PlayerPoints.getPlugin(PlayerPoints.class).getAPI().look(player.getUniqueId()) >= POINTS;
                if (!haveEnoughPoints) { //do nothing if player have not enough points
                    return;
                }
                new ConfirmMenu("确认购买: 天坑乱斗会员", confirm -> {
                    if (confirm) {
                        //remove player's points
                        boolean successfully = PlayerPoints.getPlugin(PlayerPoints.class).getAPI().look(player.getUniqueId()) >= POINTS; //if the purchase is successful
                        if (successfully) {
                            PlayerPoints.getPlugin(PlayerPoints.class).getAPI().take(player.getUniqueId(), POINTS);
                            profile.setSupporter(true);
                            CC.boardCast("&6&l惊了! " + profile.getFormattedNameWithRoman(null) + " &7购买了 &e天坑会员 &7!");
                            new SupporterMedal().setProgress(profile, 1);
                        } else {
                            player.sendMessage(CC.translate("&c购买失败,如果发现扣费异常,请联系管理员."));
                        }
                    }
                }, true, 5).openMenu(player);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(CC.translate("&c获取信息失败,请重试!"));
            }
        }
    }
}
