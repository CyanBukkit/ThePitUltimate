package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerAssist;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import dev.jnic.annotation.Include;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;

/**
 * @Author: Misoryan
 * @Created_In: 2021/1/30 15:33
 */
@Include
@BowOnly
@AutoRegister
public class TrueDamageArrowEnchant extends AbstractEnchantment implements Listener {
    final DecimalFormat format = new DecimalFormat("0");

    public static double getTrueDamageRate(int enchantLevel) {
        return (0.15 + enchantLevel * 0.1) / (1 - (0.15 + enchantLevel * 0.1));
    }

    @Override
    public String getEnchantName() {
        return "真实一击: 箭矢";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "true_damage_arrow";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {

        return "&7造成的弓箭伤害降低至原来的 &9" + (100 - 15 - enchantLevel * 10) + "% &7,"
                + "/s&7弓箭命中额外造成相当于伤害量 &c" + format.format(100 * getTrueDamageRate(enchantLevel)) + "% &7的&f真实&7伤害";
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            if (event.getDamager().hasMetadata("true_shot")) {
                final Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return;
                final org.bukkit.inventory.ItemStack itemInHand = player.getItemInHand();
                if (itemInHand == null) return;
                final int level = this.getItemEnchantLevel(itemInHand);
                if (level == -1) {
                    return;
                }
                event.setDamage((1 - 0.15 - level * 0.1) * event.getDamage());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTrueShotTrigger(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof Player victim) {
            if (event.getDamager().hasMetadata("true_shot")) {
                final Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player) || PlayerUtil.isEquippingSomber((Player) event.getEntity())) {
                    return;
                }
                final int level = event.getDamager().getMetadata("true_shot_level").get(0).asInt();
                if (level == -1) {
                    return;
                }
                Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                    final int noDamageTicks = player.getNoDamageTicks();
                    victim.setNoDamageTicks(0);
                    EntityPlayer handle = ((CraftPlayer) victim).getHandle();
                    float absorptionHearts = handle.getAbsorptionHearts();
                    double v = getTrueDamageRate(level) * event.getFinalDamage();
                    handle.setAbsorptionHearts((float)Math.max(0,absorptionHearts - v));
                    if(handle.getAbsorptionHearts() <= 0) {
                       victim.setHealth (Math.max(0, Math.min(player.getMaxHealth(),player.getHealth() - (v - absorptionHearts))));
                    }
                    victim.setNoDamageTicks(noDamageTicks);
                });
            }
        }
    }

    @EventHandler
    public void onBowShot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PlayerUtil.isVenom(player) || PlayerUtil.isEquippingSomber(player)) return;
        final org.bukkit.inventory.ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) return;
        final int level = this.getItemEnchantLevel(itemInHand);
        if (level <= 0) {
            return;
        }
        if (itemInHand.getType() == Material.BOW) {
            event.getProjectile().setMetadata("true_shot", new FixedMetadataValue(ThePit.getInstance(), true));
            event.getProjectile().setMetadata("true_shot_level", new FixedMetadataValue(ThePit.getInstance(),level));
        }
    }

}
