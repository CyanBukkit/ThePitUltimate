package cn.charlotte.pit.perk.type.prestige;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PerkData;
import cn.charlotte.pit.perk.AbstractPerk;
import cn.charlotte.pit.perk.PerkType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 2022/10/29<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
public class OfferCoinMaxLimitPerk extends AbstractPerk {
    @Override
    public String getInternalPerkName() {
        return "offer_coin_limit";
    }

    @Override
    public String getDisplayName() {
        return "交易额度提升";
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public double requireCoins() {
        return 100000000;
    }

    @Override
    public int requireRenown(int level) {
        return level * 100;
    }

    @Override
    public int requirePrestige() {
        return 0;
    }

    @Override
    public int requireLevel() {
        return 0;
    }

    private final DecimalFormat df = new DecimalFormat(",###,###,###,###");

    @Override
    public List<String> getDescription(Player player) {
        List<String> list = new ArrayList<>();
        list.add("§7当前交易额度: §a" + df.format(getOfferCoinMaxLimit(player, null)));
        if (getPerkLevel(player) < 5) {
            list.add("§7提升交易额度至: §a" + df.format(getOfferCoinMaxLimit(player, Math.min(getPerkLevel(player) + 1, 5))));
        }
        return list;
    }

    public static int getPerkLevel(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        PerkData perkData = profile.getChosePerk().get(-8);
        if (perkData != null) {
            return perkData.getLevel();
        }
        return 0;
    }

    public static final int[] limit = new int[]{5_0000, 8_0000, 10_0000, 20_0000, 50_0000, 70_0000};

    public static int getOfferCoinMaxLimit(Player player, Integer level) {
        if (level == null) {
            level = getPerkLevel(player);
        }
        return limit[level];
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public PerkType getPerkType() {
        return PerkType.PERK;
    }

    @Override
    public void onPerkActive(Player player) {

    }

    @Override
    public void onPerkInactive(Player player) {

    }
}
