package cn.charlotte.pit.enchantment.type.dark_normal;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import dev.jnic.annotation.Include;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@Include
@ArmorOnly
public class GrimReaperEnchant extends AbstractEnchantment implements IPlayerKilledEntity {
    @Override
    public String getEnchantName() {
        return "死神";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "grim_reaper_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.DARK_NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7击杀玩家时释放冲击波,对周围10格内所有玩家造成 &c2❤ &7普通伤害"
                + "/s&c击杀获得的奖励 -80%";
    }

    @Override
    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        coins.getAndAdd(-0.8 * coins.get());
        experience.getAndAdd(-0.8 * experience.get());
        Player targetPlayer = (Player) target;
        Collection<Player> nearbyPlayers = PlayerUtil.getNearbyPlayers(myself.getLocation(), 10);

        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
            for (Player player : nearbyPlayers) {
                if (player != myself && player != targetPlayer) {
                    player.damage(4, myself);
                }
            }
        }, 1L);
    }
}
