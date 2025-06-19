package net.mizukilab.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import net.mizukilab.pit.enchantment.AbstractEnchantment;
import net.mizukilab.pit.enchantment.IActionDisplayEnchant;
import net.mizukilab.pit.enchantment.param.event.PlayerOnly;
import net.mizukilab.pit.enchantment.param.item.WeaponOnly;
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity;
import net.mizukilab.pit.parm.listener.IAttackEntity;
import net.mizukilab.pit.util.PlayerUtil;
import net.mizukilab.pit.util.cooldown.Cooldown;
import net.mizukilab.pit.util.random.RandomUtil;
import net.mizukilab.pit.util.time.TimeUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@WeaponOnly
public class ElementalFuryEnchant extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant {

    private static final HashMap<UUID, Cooldown> cooldown = new HashMap<>();

    @Override
    public String getEnchantName() {
        return "元素";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "elemental_fury_enchant";
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
        int chance = enchantLevel * 8 + 12; // 20%, 28%, 36%
        return "&7攻击时有 &e" + chance + "% &7的概率触发元素效果:" +
               "/s&c火焰: &7点燃敌人 " + (enchantLevel + 2) + " 秒" +
               "/s&b冰霜: &7缓慢敌人 " + (enchantLevel + 1) + " 秒" +
               "/s&e雷电: &7获得 &b速度 &7效果 " + (enchantLevel + 2) + " 秒 (3秒冷却)";
    }

    @Override
    public String getText(int level, Player player) {
        return cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired() ? 
               "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).getRemaining()).replace(" ", "");
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player) target;
        
        if (cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0)).hasExpired()) {
            int chance = enchantLevel * 8 + 12;
            if (RandomUtil.hasSuccessfullyByChance(chance / 100.0)) {

                int elementType = (int) (Math.random() * 3);
                
                switch (elementType) {
                    case 0:
                        targetPlayer.setFireTicks((enchantLevel + 2) * 20);
                        attacker.playSound(attacker.getLocation(), Sound.GHAST_FIREBALL, 0.8f, 1.2f);
                        break;
                        
                    case 1:
                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (enchantLevel + 1) * 20, 1), true);
                        attacker.playSound(attacker.getLocation(), Sound.GLASS, 1.0f, 0.8f);
                        break;
                        
                    case 2:
                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (enchantLevel + 2) * 20, 1), true);

                        PlayerUtil.heal(attacker, enchantLevel * 0.5);
                        attacker.playSound(attacker.getLocation(), Sound.AMBIENCE_THUNDER, 0.6f, 1.5f);
                        break;
                }
                
                cooldown.put(attacker.getUniqueId(), new Cooldown(3, TimeUnit.SECONDS));
            }
        }
    }
} 