package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@BowOnly
public class UndeadArrowEnchant extends AbstractEnchantment implements IPlayerShootEntity {
    @Override
    public String getEnchantName() {
        return "亡灵之箭";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "judgment_shot_bow";
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
        return (new StringBuilder()).insert(0, "&7攻击玩家时若当次攻击使玩家的生命值低于 &c").append(1.5D + enchantLevel * 0.5D).append("❤ &7,/s&7则该次攻击直接致死.").toString();
    }

    @Override
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (((Player)target).getHealth() - damage * boostDamage.get() < 1.5D + enchantLevel * 0.5D) {
            cancel.set(true);
            finalDamage.getAndAdd(9999.0D);
            attacker.playSound(attacker.getLocation(), Sound.VILLAGER_DEATH, 1.0F, 1.0F);
            Location location = target.getLocation();
            PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(2001, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), 152, false);
        }
    }
}
