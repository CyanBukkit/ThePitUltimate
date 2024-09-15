package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import com.google.common.util.concurrent.AtomicDouble;
import dev.jnic.annotation.Include;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/26 13:36
 */
@Include
@ArmorOnly
@AutoRegister
public class CounterOffensiveEnchant extends AbstractEnchantment implements IPlayerDamaged {
    @Override
    public String getEnchantName() {
        return "反恐";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "counter_offensive_enchant";
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
        return "&7每被同一名玩家攻击 &e" + (5 - enchantLevel) + " &7次,自身获得 &b速度 II &f(" + TimeUtil.millisToTimer((2L * enchantLevel + 1) * 1000) + ")";
    }

    @Override
    @PlayerOnly
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player) attacker;
        String uniqueId = targetPlayer.getUniqueId().toString();
        if (!myself.hasMetadata("counter_enchant_" + uniqueId)) {
            myself.setMetadata("counter_enchant_" + uniqueId, new FixedMetadataValue(ThePit.getInstance(), 1));
        } else {
            int count = myself.getMetadata("counter_enchant_" + uniqueId).get(0).asInt();
            if (count + 1 >= 5 - enchantLevel) {
                count = -1;
                myself.removePotionEffect(PotionEffectType.SPEED);
                myself.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (2 * enchantLevel + 1), 1, true));
            }
            if(count == -1){
                myself.removeMetadata("counter_enchant_" + uniqueId,ThePit.getInstance());
                return;
            }
            myself.setMetadata("counter_enchant_" + uniqueId, new FixedMetadataValue(ThePit.getInstance(), count + 1));
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.getPlayer().removeMetadata("counter_enchant_" + e.getPlayer().getUniqueId(),ThePit.getInstance());
    }
}
