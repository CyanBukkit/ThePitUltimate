package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.enchantment.type.rage.ThinkOfThePeopleEnchant;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/1/17 18:35
 */
@WeaponOnly
public class ComboStrikeEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity, IActionDisplayEnchant {

    private final DecimalFormat numFormat = new DecimalFormat("0.0");
    private static final ThinkOfThePeopleEnchant thinkOfThePeople = new ThinkOfThePeopleEnchant();

    @Override
    public String getEnchantName() {
        return "强力击: 闪电";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "lightning_strike";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(3, TimeUnit.SECONDS);
    }

    public static double getDamage(int enchantLevel) {
        switch (enchantLevel) {
            default:
                return 0;
            case 1:
                return 1.5;
            case 2:
                return 2;
            case 3:
                return 1;
        }
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7每 &e" + (enchantLevel > 1 ? 4 : 5) + " &7次击中目标额外召唤一道闪电攻击你的敌人,/s&7造成 &c"
                + numFormat.format(getDamage(enchantLevel))
                + "❤ &b雷电&f(真实)&7伤害"
                + (enchantLevel >= 3 ? "&7,且闪电攻击目标/s每穿着一件&b钻石装备&7,闪电额外造成 &c1❤ &b雷电&f(真实)&7伤害" : "");
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player victim = (Player) target;
        if (attacker.getItemInHand().getType() != Material.GOLD_SWORD) {
            return;
        }
        for (Player nearbyPlayers : PlayerUtil.getNearbyPlayers(victim.getLocation(), 10)) {
            if (nearbyPlayers.getInventory().getLeggings() != null && thinkOfThePeople.isItemHasEnchant(nearbyPlayers.getInventory().getLeggings())) {
                int level = thinkOfThePeople.getItemEnchantLevel(nearbyPlayers.getInventory().getLeggings());
                if (level > 1) {
                    boostDamage.getAndAdd(level * -0.1 + 0.1);
                }
                return;
            }
        }
        final PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId());
        if (profile.getMeleeHit() % (enchantLevel > 1 ? 4 : 5) == 0) {
            finalDamage.set(finalDamage.get() + 2 * getDamage(enchantLevel));
            PlayerUtil.playThunderEffect(target.getLocation());
            PlayerProfile victimProfile = PlayerProfile.getPlayerProfileByUuid(victim.getUniqueId());
            CC.send(MessageType.MISC, attacker, "&b&l闪电! &7你的闪电击中了 " + victimProfile.getFormattedName(attacker) + " &7!");
            if (enchantLevel >= 3) {
                int extra = 0;
                for (ItemStack armorContent : victim.getInventory().getArmorContents()) {
                    if (armorContent != null && armorContent.getType().name().contains("DIAMOND")) {
                        extra++;
                    }
                }
                finalDamage.set(finalDamage.get() + extra * 2);
            }
        }
    }

    @Override
    @PlayerOnly
    @cn.charlotte.pit.parm.type.BowOnly
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player victim = (Player) target;
        if (attacker.getItemInHand().getType() != new MythicBowItem().getItemDisplayMaterial()) {
            return;
        }

        for (Player nearbyPlayers : PlayerUtil.getNearbyPlayers(victim.getLocation(), 10)) {
            if (nearbyPlayers.getInventory().getLeggings() != null && thinkOfThePeople.isItemHasEnchant(nearbyPlayers.getInventory().getLeggings())) {
                int level = thinkOfThePeople.getItemEnchantLevel(nearbyPlayers.getInventory().getLeggings());
                if (level > 1) {
                    boostDamage.getAndAdd(level * -0.1 + 0.1);
                }
                return;
            }
        }
        if (PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getMeleeHit() % (enchantLevel > 1 ? 4 : 5) == 0) {
            finalDamage.set(finalDamage.get() + 2 * getDamage(enchantLevel));
            PlayerUtil.playThunderEffect(target.getLocation());
            PlayerProfile victimProfile = PlayerProfile.getPlayerProfileByUuid(victim.getUniqueId());
            CC.send(MessageType.MISC, attacker, "&b&l闪电! &7你的闪电击中了 " + victimProfile.getFormattedName(attacker) + " &7!");
            if (enchantLevel >= 3) {
                double extra = 0;
                for (ItemStack armorContent : victim.getInventory().getArmorContents()) {
                    if (armorContent != null && armorContent.getType().name().contains("DIAMOND")) {
                        extra++;
                    }
                }
                finalDamage.set(finalDamage.get() + extra * 0.5F);
            }
        }
    }

    @Override
    public String getText(int level, Player player) {
        int hit = (player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW ? PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit());
        return (hit % (level > 1 ? 4 : 5) == 0 ? "&a&l✔" : "&e&l" + ((level > 1 ? 4 : 5) - hit % (level > 1 ? 4 : 5)));
    }

}
