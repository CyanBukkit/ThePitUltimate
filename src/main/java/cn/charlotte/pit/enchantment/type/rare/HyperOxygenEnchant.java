package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class HyperOxygenEnchant extends AbstractEnchantment implements IPlayerDamaged {
    private static PotionEffect potionEffectLevel1 = new PotionEffect(PotionEffectType.REGENERATION, 4, 1);

    private static PotionEffect potionEffectLevel2 = new PotionEffect(PotionEffectType.REGENERATION, 8, 1);


    private static PotionEffect potionEffectLevel3 = new PotionEffect(PotionEffectType.REGENERATION, 8, 2);

    @Override
    public String getEnchantName() {
        return "超氧化物";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "hyper_oxygen";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int i) {
        switch (i) {
            case 1:
                return "受到攻击时如自身没有“生命恢复”效果，则为自身添加“生命恢复 I (00：04)”效果";
            case 2:
                return "受到攻击时如自身没有“生命恢复”效果，则为自身添加“生命恢复 I (00：08)”效果";
            case 3:
                return "受到攻击时如自身没有“生命恢复”效果，则为自身添加“生命恢复 II (00：08)”效果";
            default:
                return "&c请提交至管理员寻求帮助 (INVALID LEVEL | + " + i + ")";
        }
    }
    @Override
    public void handlePlayerDamaged(int level, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        System.out.println("Damaged");
        boolean canExecuteAsMythic = "mythic_leggings".equals(ItemUtil.getInternalName(player.getItemInHand()));
        if(canExecuteAsMythic) {
            if (! player.hasPotionEffect( PotionEffectType.REGENERATION)) {
                System.out.println("ADDED");
                switch (level) {
                    case 1:
                        player.addPotionEffect(potionEffectLevel1);
                        break;
                    case 2:
                        player.addPotionEffect(potionEffectLevel2);
                        break;
                    case 3:
                        player.addPotionEffect(potionEffectLevel3);
                        break;
                    default:
                        player.sendMessage("INVALID POTION LEVEL");
                }
            }
        }
    }
}
