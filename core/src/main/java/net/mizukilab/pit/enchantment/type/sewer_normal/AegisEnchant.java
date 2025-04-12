package net.mizukilab.pit.enchantment.type.sewer_normal;

import net.mizukilab.pit.enchantment.AbstractEnchantment;
import net.mizukilab.pit.enchantment.rarity.EnchantmentRarity;
import net.mizukilab.pit.parm.listener.ITickTask;
import net.mizukilab.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Player;

/**
 * @Author: Misoryan
 * @Created_In: 2021/3/16 21:43
 */

public class AegisEnchant extends AbstractEnchantment implements ITickTask {

    @Override
    public String getEnchantName() {
        return "宙斯之盾";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "aegis_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.SEWER_NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7每 &e9 &7秒获得一层护盾 (可以抵消1次玩家伤害) (最高1层)";
    }

    @Override
    public void handle(int enchantLevel, Player player) {

    }

    @Override
    public int loopTick(int enchantLevel) {
        return 0;
    }
}
