package net.mizukilab.pit.enchantment.type.dark_rare;

import cn.charlotte.pit.buff.impl.CoagulationBuff;
import cn.charlotte.pit.data.PlayerProfile;
import net.mizukilab.pit.enchantment.AbstractEnchantment;
import net.mizukilab.pit.enchantment.IActionDisplayEnchant;
import net.mizukilab.pit.enchantment.param.item.ArmorOnly;
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity;
import net.mizukilab.pit.parm.listener.IAttackEntity;
import net.mizukilab.pit.parm.listener.IPlayerShootEntity;
import net.mizukilab.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class ComboRottenHeartEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity, IActionDisplayEnchant {

    private final HashMap<UUID, Cooldown> Cooldown = new HashMap<>();
    private final static CoagulationBuff coagulationBuff = new CoagulationBuff();

    @Override
    public String getEnchantName() {
        return "强力击: &4腐烂&6之&c心&d";
    }


    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "rottenheart_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.DARK_RARE;
    }


    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7每 &e4 &7次攻击命中, 扣除目标 &c1.5❤ &7生命值上限, /s并附带施加 &8凋零 I &f(00:03) &7与 &4凝血 &f(00:02)/s&7效果 &4凝血 &7: &7丧失血量恢复能力";
    }

    @Override
    public String getText(int level, Player player) {
        level = (player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW) ? PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit();
        byte b = 4;
        if (Cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L)).hasExpired())
            return (level % b == 0) ? "&4&l✔" : (new StringBuilder()).insert(0, "&c&l").append(b - level % b).toString();
        return getCooldownActionText(Cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L)));
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player player1 = (Player) target;
        if (PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getMeleeHit() % 4 == 0) {
            if (player1.getMaxHealth() > 3.0D) {
                player1.setMaxHealth(Math.max(0.1D, player1.getMaxHealth() - 3.0D));
            } else {
                player1.damage(player1.getMaxHealth() * 100.0D);
            }
            player1.removePotionEffect(PotionEffectType.WITHER);
            player1.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0), true);
        }
    }

    @Override
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player player1 = (Player) target;
        if (PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getMeleeHit() % 4 == 0) {
            if (player1.getMaxHealth() > 3.0D) {
                player1.setMaxHealth(Math.max(0.1D, player1.getMaxHealth() - 3.0D));
            } else {
                player1.damage(player1.getMaxHealth() * 100.0D);
            }
            player1.removePotionEffect(PotionEffectType.WITHER);
            player1.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0), true);
            coagulationBuff.stackBuff(player1, 2000L, 1);
        }
    }
}
