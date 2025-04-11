package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.chat.RomanUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class EscapeEnchant extends AbstractEnchantment implements IPlayerDamaged, IActionDisplayEnchant {

    private final Map<UUID, Cooldown> Cooldown = new HashMap<>();

    @Override
    public String getEnchantName() {
        return "紧急逃生";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "escape_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return new Cooldown(30L, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return (new StringBuilder()).insert(0, "&7当你的生命值低于 &c4❤ &7时受到伤害, 则为你施加以下效果: &7(30秒冷却)/s  &f▶ &3抗性提升 ").append(RomanUtil.convert(enchantLevel)).append(" &f(00:03)/s  &f▶ &b速度 IV &f(00:03)/s  &f▶ &c生命恢复 ").append(RomanUtil.convert(enchantLevel)).append(" &f(00:07)").toString();
    }

    @Override
    public String getText(int enchantLevel, org.bukkit.entity.Player player) {
        if (Cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L)).hasExpired()) {
            return "&a&l✔";
        }
        return new StringBuilder().insert(0, "&c&l").append(TimeUtil.millisToRoundedTime((Cooldown.get(player.getUniqueId())).getRemaining()).replace(" ", "")).toString();
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        this.Cooldown.putIfAbsent(myself.getUniqueId(), new Cooldown(0L));
        if (myself.getHealth() <= 8.0D && Cooldown.get((myself.getUniqueId())).hasExpired()) {
            this.Cooldown.put(myself.getUniqueId(), getCooldown());
            myself.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            myself.removePotionEffect(PotionEffectType.REGENERATION);
            myself.removePotionEffect(PotionEffectType.SPEED);
            myself.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, enchantLevel - 1), true);
        }
    }
}
