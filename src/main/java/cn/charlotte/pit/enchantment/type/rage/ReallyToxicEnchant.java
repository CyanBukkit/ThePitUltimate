package cn.charlotte.pit.enchantment.type.rage;

import cn.charlotte.pit.buff.impl.HealPoisonDeBuff;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Creator Misoryan
 * @Date 2021/5/8 19:39
 */
@AutoRegister
public class ReallyToxicEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity, Listener {

    public static final HealPoisonDeBuff buff = new HealPoisonDeBuff();


    @Override
    public String getEnchantName() {
        return "不愈";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "really_toxic";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NOSTALGIA;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7攻击为目标玩家施加 &e2 &7层 &a不愈之毒 &f(00:30) &7,最高叠加" + (enchantLevel * 10 + 10) + "层."
                + "/s&7效果 &a不愈之毒&7: 降低恢复生命值时的生命恢复量,每层降低 &a1%";
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player) target;
        buff.stackBuff(targetPlayer, 30 * 1000L, Math.min(2, enchantLevel * 10 + 10 - buff.getPlayerBuffData(targetPlayer).getTier()));
    }

    @Override
    @PlayerOnly
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player) target;
        buff.stackBuff(targetPlayer, 30 * 1000L, Math.min(2, enchantLevel * 10 + 10 - buff.getPlayerBuffData(targetPlayer).getTier()));
    }

}
