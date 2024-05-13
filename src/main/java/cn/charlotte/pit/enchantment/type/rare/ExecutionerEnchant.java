package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.enchantment.type.rage.ThinkOfThePeopleEnchant;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPriority;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/1/29 21:23
 */
@WeaponOnly
public class ExecutionerEnchant extends AbstractEnchantment implements IAttackEntity, IPriority {

    private static final ThinkOfThePeopleEnchant thinkOfThePeople = new ThinkOfThePeopleEnchant();

    @Override
    public String getEnchantName() {
        return "处决";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "executioner";
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
        return "&7攻击玩家时若当次攻击使玩家的生命值低于 &c" + (0.5 * enchantLevel + 0.5) + "❤ &7,"
                + "/s&7则该次攻击直接致死";
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {

        Player targetPlayer = (Player) target;
        for (Player nearbyPlayers : PlayerUtil.getNearbyPlayers(targetPlayer.getLocation(), 10)) {
            if (nearbyPlayers.getInventory().getLeggings() != null && thinkOfThePeople.isItemHasEnchant(nearbyPlayers.getInventory().getLeggings())) {
                int level = thinkOfThePeople.getItemEnchantLevel(nearbyPlayers.getInventory().getLeggings());
                if (level > 1) {
                    boostDamage.getAndAdd(level * -0.1 + 0.1);
                }
                return;
            }
        }

        if (targetPlayer.getHealth() - damage * boostDamage.get() + finalDamage.get() < (enchantLevel + 1)) {
//            cancel.set(true);
            finalDamage.getAndAdd(99999);
            attacker.playSound(attacker.getLocation(), Sound.VILLAGER_DEATH, 1, 0.5F);

            Location deathLoc = target.getLocation();
            PacketPlayOutWorldEvent packetA = new PacketPlayOutWorldEvent(2001, new BlockPosition(deathLoc.getBlockX(), deathLoc.getBlockY(), deathLoc.getBlockZ()), 152, false);
            PacketPlayOutWorldEvent packetB = new PacketPlayOutWorldEvent(2001, new BlockPosition(deathLoc.getBlockX(), deathLoc.getBlockY() - 1, deathLoc.getBlockZ()), 152, false);

            PlayerConnection connection = ((CraftPlayer) attacker).getHandle().playerConnection;
            connection.sendPacket(packetA);
            connection.sendPacket(packetB);
            sync(() -> {
                if (targetPlayer.isOnline()) {
                    targetPlayer.spigot().respawn();
                }
            });
        }
    }

    @Override
    public int priority() {
        return 1000;
    }
}
