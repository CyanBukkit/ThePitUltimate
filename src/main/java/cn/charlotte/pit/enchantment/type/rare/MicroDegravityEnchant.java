package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.klee.backports.utils.SWMRHashTable;
import com.comphenix.protocol.PacketType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
@ArmorOnly
@AutoRegister
public class MicroDegravityEnchant extends AbstractEnchantment implements Listener,IPlayerDamaged, IAttackEntity {
    Cache<Entity,Byte> entityByteMap = Caffeine.newBuilder().expireAfterWrite(30,TimeUnit.SECONDS).build();
    @Override
    public String getEnchantName() {
        return "微观反重力";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "micro_degravity";
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
    public String getUsefulnessLore(int i) {
        switch (i){
            case 1:
                return  "&7若你在空中连续受到 &f3 &7次攻击 (2秒内)/s" +
                        "&7则恢复 &c2.0❤ &7生命值";
            case 2:
                return "&7若你在空中连续受到 &f3 &7次攻击 (2秒内)/s" +
                        "&7则恢复 &c2.0❤ &7生命值, 并且在接下来的30秒内/s" +
                        "&7增加 &c10% &7的伤害。(可叠加，最高2层)";
            case 3:
                return "&7若你在空中连续受到 &f3 &7次攻击 (2秒内)/s" +
                        "&7则恢复 &c2.0❤ &7生命值, 并且在接下来的30秒内/s" +
                        "&7增加 &c15% &7的伤害。(可叠加，最高2层)";
        }
        return "&c请提交至管理员寻求帮助 (INVALID LEVEL | + "+ i +")";
    }
    @ArmorOnly
    @Override
    public void handlePlayerDamaged(int level, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if(!player.isOnGround()){
            return;
        }
        Byte hurtTime;
        if ((hurtTime = entityByteMap.getIfPresent(player)) == null) {
            entityByteMap.put(player, (byte) 0);
        } else {
            if (++hurtTime > 2) {
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 2));
            }
            int min = Math.min(2, hurtTime);
            entityByteMap.put(player, (byte) min);

        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        entityByteMap.invalidate(e.getPlayer());
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Byte b = entityByteMap.getIfPresent(attacker);
        if (b != null) {
            if (enchantLevel <= 1) {
                return;
            } else if (enchantLevel >= 3) {
                boostDamage.addAndGet(0.15 * b);
            } else {
                boostDamage.addAndGet(0.11 * b);
            }
        }


    }
}
