package cn.charlotte.pit.enchantment.type.limit;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.RodOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 2022/9/30<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@WeaponOnly
@RodOnly
public class GuoQing2022Ench extends AbstractEnchantment implements ITickTask, IAttackEntity, IPlayerKilledEntity, IPlayerDamaged {
    public static final GuoQing2022Ench instance = new GuoQing2022Ench();

    @Override
    public String getEnchantName() {
        return "&d举世瞩目 &9| &c2022";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "guoqing2022";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7战斗状态时每 " + loopTick(enchantLevel) / 20 + " 秒获得生命回复I(0:10)/s" +
                "&7持有时你的赏金将会变成&4深红色/s" +
                "&7当受到攻击时且生命值低于 10.1&cHP&7,10.1%几率 &6+2❤/s" +
                "&7每持有&4500&7赏金+0.5%伤害/s" +
                "&7每击杀10名获得 &6101硬币 &7/ &b73经验值/s" +
                "&8攻击玩家时 0.0001% &8几率 &e+1声望/s"
                ;
    }

    @Override
    public void handle(int enchantLevel, Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile.getCombatTimer().hasExpired()) {
            return;
        }
        sync(() -> {
            PlayerUtil.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));
        });
    }

    @Override
    public int loopTick(int enchantLevel) {
        switch (enchantLevel) {
            default: {
                return 10 * 20;
            }
            case 2: {
                return 20 * 20;
            }
            case 1: {
                return 30 * 20;
            }
        }
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId());
        if (!profile.isLoaded()) {
            return;
        }
        if (profile.getBounty() >= 500) {
            int i = profile.getBounty() / 500;
            boostDamage.getAndAdd(0.05 * i);
        }
        if (target instanceof Player) {
            if (RandomUtil.hasSuccessfullyByChance(0.000001)) {
                profile.setRenown(profile.getRenown() + 1);
            }
        }
    }

    @Override
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
        if (!profile.isLoaded()) {
            return;
        }
        if (profile.getKills() % 10 == 0) {
            coins.getAndAdd(101);
            experience.getAndAdd(73);
        }
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
        if (!profile.isLoaded()) {
            return;
        }
        if (myself.getHealth() > 10.1) {
            return;
        }
        EntityPlayer player = ((CraftPlayer) myself).getHandle();
        if (player.getAbsorptionHearts() > 2) {
            return;
        }
        sync(() -> {
            if (RandomUtil.hasSuccessfullyByChance(0.101)) {
                player.setAbsorptionHearts(2);
            }
        });
    }
}
