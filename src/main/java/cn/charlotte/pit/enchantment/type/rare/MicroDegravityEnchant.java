package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
@ArmorOnly
public class MicroDegravityEnchant extends AbstractEnchantment implements IPlayerDamaged, IAttackEntity {
    Map<Entity,Byte> entityByteMap = new ConcurrentHashMap<>();
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
        if ("mythic_leggings".equals(ItemUtil.getInternalName(player.getItemInHand()))) {
            Byte hurtTime;
            if ((hurtTime = entityByteMap.get(player)) == null) {
                entityByteMap.putIfAbsent(player, (byte) 1);
            } else {
                if (++hurtTime > 3) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 2));
                    NBTTagCompound nbtTag = ((CraftPlayer) player).getHandle().getNBTTag();
                    int levelForNBT = nbtTag.getInt("MD_LEVEL_" + level);
                    nbtTag.setInt("MD_LEVEL_" + level,Math.min(++levelForNBT,2));
                    entityByteMap.put(player, (byte) 1);
                }
            }
        }
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        NBTTagCompound nbtTag = ((CraftPlayer) attacker).getHandle().getNBTTag();
        double v = finalDamage.doubleValue();
        for(byte level2 = 0; level2 < nbtTag.getInt("MD_LEVEL_2");level2++){
            v *= 1.1;
        }
        for(byte level3 = 0; level3 < nbtTag.getInt("MD_LEVEL_3");level3++){
            v *= 1.5;
        }
        finalDamage.set(v);
    }
}
