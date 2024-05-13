package cn.charlotte.pit.enchantment.type.rage;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.util.cooldown.Cooldown;

/**
 * @Creator Misoryan
 * @Date 2021/5/8 14:12
 */
@ArmorOnly
public class ThinkOfThePeopleEnchant extends AbstractEnchantment {
    @Override
    public String getEnchantName() {
        return "\"为人着想\"";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "think_of_the_people";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NOSTALGIA_RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7为周围 &e10 &7格内的所有玩家(包括自身)添加以下效果:"
                + "/s&7免疫附魔 &c处决 &7与 &b强力击:闪电 &7的效果"
                + (enchantLevel > 1 ? ";/s&7受到来自以上附魔使用者的伤害 &9-" + (enchantLevel * 10 - 10) + "%" : "");
    }
}
