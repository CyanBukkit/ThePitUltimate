package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.entity.EntityUtil;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.VectorUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import com.google.common.util.concurrent.AtomicDouble;
import dev.jnic.annotation.Include;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/2 18:35
 */
@Include
@WeaponOnly
public class GambleEnchant extends AbstractEnchantment implements IAttackEntity {
    @Override
    public String getEnchantName() {
        return "赌徒";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "gamble_enchant";
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
        return "&7攻击时有 &e50% &7的几率对自身或敌人"
                + "/s&7额外造成 &c" + enchantLevel + "❤ &7的&c必中&7伤害" + "/s&7若使用1.8双倍攻击特性, 下次攻击的倍率将将为1.5-1.7x";
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (RandomUtil.hasSuccessfullyByChance(0.5)) {
            Player targetPlayer = (Player) target;
            Player gamblePlayer = attacker;
            if (RandomUtil.hasSuccessfullyByChance(0.4)) {
                gamblePlayer = targetPlayer;
            }
            if (gamblePlayer.getNoDamageTicks() < 5) {
                PlayerUtil.damage(attacker, gamblePlayer, PlayerUtil.DamageType.TRUE, (double) (enchantLevel * 2), false);

            } else {
                PlayerUtil.damage(attacker, gamblePlayer, PlayerUtil.DamageType.TRUE, (double) (enchantLevel) * (Math.random() * 0.2F + 1.3), true);
            }
        }
    }
}
