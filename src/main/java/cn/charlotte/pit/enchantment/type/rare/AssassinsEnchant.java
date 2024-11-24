package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
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
@ArmorOnly
public class AssassinsEnchant extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant {
    private static final HashMap<UUID, Cooldown> Cooldown = new HashMap<>();
    @Override
    public String getEnchantName() {
        return "暗影刺客";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "assassins_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return new Cooldown(5L, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return (new StringBuilder()).insert(0, "&7若当此攻击使目标生命值低于 &c").append(enchantLevel).append("❤ &7,/s则立即将你传送至目标身后并对其施加 &8失明 &f(00:02) &7效果. &7(5秒冷却)").toString();
    }

    @Override
    public String getText(int level, Player player) {
        return Cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L)).hasExpired() ? "&a&l✔" : (new StringBuilder()).insert(0, "&c&l").append(TimeUtil.millisToRoundedTime(Cooldown.get(player.getUniqueId()).getRemaining()).replace(" ", "")).toString();
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (((Player)target).getHealth() <= (2 * enchantLevel) && (Cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            attacker.teleport(target.getLocation());
            ((Player)target).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 4), true);
            Cooldown.put(attacker.getUniqueId(), getCooldown());
        }
    }
}
