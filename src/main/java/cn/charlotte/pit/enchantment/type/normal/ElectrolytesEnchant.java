package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/25 15:11
 */
@ArmorOnly
public class ElectrolytesEnchant extends AbstractEnchantment implements IPlayerKilledEntity {
    @Override
    public String getEnchantName() {
        return "电解质";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "electrolytes_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7击杀时如自身存在 &b速度 &7效果,延长效果时间 &e" + (enchantLevel * 2) + " 秒"
                + "/s&7(如效果等级大于II则延长时间减半,上限" + ((enchantLevel + 2) * 6) + "秒)";
    }

    @Override
    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        for (PotionEffect potionEffect : myself.getActivePotionEffects()) {
            if (potionEffect.getType().getName().equalsIgnoreCase("speed")) {
                if (!(potionEffect.getDuration() < (enchantLevel + 2) * 6 * 20)) {
                    return;
                }
                sync(() -> {
                    if (potionEffect.getAmplifier() > 1) {
                        PlayerUtil.addPotionEffect(myself, new PotionEffect(PotionEffectType.SPEED, enchantLevel * 20, potionEffect.getAmplifier()), true);
                    } else {
                        PlayerUtil.addPotionEffect(myself, new PotionEffect(PotionEffectType.SPEED, enchantLevel * 2 * 20, potionEffect.getAmplifier()), true);
                    }
                });
            }
        }
    }
}
