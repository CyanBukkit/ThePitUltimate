package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/1/29 20:20
 */
@ArmorOnly
public class CoinGloriousEnchant extends AbstractEnchantment implements IAttackEntity {
    @Override
    public String getEnchantName() {
        return "金碧辉煌";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "coin_glorious";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7每拥有一万硬币增加 &c1% &7的伤害 (可叠加,最高&6" + (enchantLevel * 6) + "%&7)";
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId());
        long number = (long) profile.getCoins();

        int count = 0;
        while (number > 0) {
            if (number % 10000 == 0) {
                count++;
            }
            number = number / 10;
        }

        if (count > 0) {
            if (enchantLevel == 1 && count >= enchantLevel * 6) {
                boostDamage.getAndAdd(enchantLevel * 6);
            } else if (enchantLevel == 2 && count >= enchantLevel * 6) {
                boostDamage.getAndAdd(enchantLevel * 6);
            } else if (enchantLevel == 3 && count >= enchantLevel * 6) {
                boostDamage.getAndAdd(enchantLevel * 6);
            }
        }

        attacker.sendMessage("Attack Update " + count);
    }
}
