package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
@BowOnly
public class CriticalHitEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity, IActionDisplayEnchant {
    private static final HashMap<UUID, Cooldown> Cooldown = new HashMap<>();

    @Override
    public String getEnchantName() {
        return "裁决之击";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "critical_hit_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return (new StringBuilder()).insert(0, "&7你造成的伤害增加 &c").append(50 * enchantLevel).append("% &7(").append(35 - 5 * enchantLevel).append("秒冷却)").append((enchantLevel >= 2) ? "/s&7且攻击后对自身施加 &b速度 III &f(00:05)" : "").toString();
    }

    @Override
    public String getText(int level, Player player) {
        return (Cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : (new StringBuilder()).insert(0, "&c&l").append(TimeUtil.millisToRoundedTime((Cooldown.get(player.getUniqueId())).getRemaining()).replace(" ", "")).toString();
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if ((Cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            Cooldown.put(attacker.getUniqueId(), new Cooldown(35L - 5L * enchantLevel, TimeUnit.SECONDS));
            boostDamage.getAndAdd(enchantLevel * 0.5D);
            if (enchantLevel >= 2) {
                attacker.removePotionEffect(PotionEffectType.SPEED);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2), true);
            }
        }
    }

    @Override
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if ((Cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            Cooldown.put(attacker.getUniqueId(), new Cooldown(35L - 5L * enchantLevel, TimeUnit.SECONDS));
            boostDamage.getAndAdd(enchantLevel * 0.5D);
        }
    }
}
