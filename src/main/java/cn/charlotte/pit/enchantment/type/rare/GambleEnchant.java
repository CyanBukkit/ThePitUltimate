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

import java.util.Vector;
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
                + "/s&7额外造成 &c" + enchantLevel + "❤ &7的&c必中&7伤害" + "/s&7若使用1.8双倍攻击特性, 下次攻击的倍率将将为1.5-1.7x, 并且对自己造成大量击退并且各方必获得缓慢 3 4秒."
                + "/s&7&c(必中伤害无法被免疫与抵抗)/s" + "&7可搭配 1.8类鱼竿切换技巧 打出双倍伤害!!!";
    }
    PotionEffect WEAK_NESS = PotionEffectType.WEAKNESS.createEffect(80,2);
    PotionEffect SLOW_NESS = PotionEffectType.SLOW.createEffect(80,3);

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (RandomUtil.hasSuccessfullyByChance(0.5)) {
            Player targetPlayer = (Player) target;
            Player gamblePlayer = attacker;
            if (RandomUtil.hasSuccessfullyByChance(0.5)) {
                gamblePlayer = targetPlayer;
            }
            if(gamblePlayer.getNoDamageTicks() < 5 ) {
                PlayerUtil.damage(attacker,gamblePlayer, PlayerUtil.DamageType.TRUE, (double) (enchantLevel * 2),false);

            } else {
                PlayerUtil.damage(attacker, gamblePlayer, PlayerUtil.DamageType.TRUE, (double) (enchantLevel) * (Math.random() * 0.2F + 1.5), true);
                if (gamblePlayer != attacker) {
                    VectorUtil.entityPushBack(gamblePlayer, 15.0);
                }
                VectorUtil.entityPushBack(attacker, 20.0);
                if (gamblePlayer.isDead() && gamblePlayer != attacker) {
                    attacker.addPotionEffect(SLOW_NESS);
                }

                targetPlayer.addPotionEffect(SLOW_NESS);
                attacker.addPotionEffect(WEAK_NESS);

            }
        }
    }
}
