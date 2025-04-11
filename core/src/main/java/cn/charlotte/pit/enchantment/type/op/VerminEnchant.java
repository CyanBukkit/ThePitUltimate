package cn.charlotte.pit.enchantment.type.op;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.BatUtil;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.entity.Player;
import real.nanoneko.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Araykal
 * @since 2025/4/11
 */
@WeaponOnly
public class VerminEnchant extends AbstractEnchantment implements ITickTask, IActionDisplayEnchant, IMagicLicense {

    private static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap<>();

    @Override
    public String getEnchantName() {
        return "吸血鬼";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "vermin_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(15, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return String.format(
                "&7右键化身蝙蝠 /s&7向视角方向移动&e%d秒&7 无法受到攻击 并恢复&c%d❤&7生命值/s同时对5格范围内的目标每0.5秒造成&d%d❤&7伤害/s（15秒冷却）",
                enchantLevel, enchantLevel, 4
        );
    }

    @Override
    public void handle(int enchantLevel, Player player) {
        if (player.isBlocking() && COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired() && !PlayerUtil.isVenom(player) && !PlayerUtil.isEquippingSomber(player)) {
            BatUtil.attachPlayerToBatsAndMove(player, enchantLevel, enchantLevel * 2);
            COOLDOWN.put(player.getUniqueId(), getCooldown());
        }
    }


    @Override
    public int loopTick(int enchantLevel) {
        return 3;
    }

    @Override
    public String getText(int level, Player player) {
        return COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(COOLDOWN.get(player.getUniqueId()).getRemaining()).replace(" ", "") + " ";
    }
}