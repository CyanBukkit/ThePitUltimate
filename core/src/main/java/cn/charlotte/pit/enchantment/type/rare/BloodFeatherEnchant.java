package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.type.FunkyFeather;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.random.RandomUtil;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@BowOnly
@WeaponOnly
@ArmorOnly
public class BloodFeatherEnchant extends AbstractEnchantment implements IPlayerKilledEntity {

    @Override
    public String getEnchantName() {
        return "&c血&3羽&6";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "bloodfeather_enchant";
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
        return (new StringBuilder()).insert(0, "&7击杀玩家时有 ").append(iIiIii(enchantLevel)).append(IIiIIi(enchantLevel)).append("% &7的概率强制目标额外掉落 &f1x &3时髦的羽毛 ").append(enchantLevel >= 3 ? "&7并掠夺" : "").toString();

    }

    @Override
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        PlayerProfile playerProfile = PlayerProfile.getPlayerProfileByUuid(myself.getUniqueId());
        if (RandomUtil.hasSuccessfullyByChance(0.01D * IIiIIi(enchantLevel)) && playerProfile.isLoaded()) {
            InventoryUtil.removeItem((Player) target, "funky_feather", 1);
            if (enchantLevel > 3) {
                myself.getInventory().addItem(FunkyFeather.toItemStack());
                myself.sendMessage(CC.translate("&c&l血&3&l羽&f! &7你的附魔掠夺对方一根 &f1x &3时髦的羽毛"));
                target.sendMessage(CC.translate("&c&l血&3&l羽&f! &7对方的附魔掠夺了你一根 &f1x &3时髦的羽毛"));
            } else {
                myself.sendMessage(CC.translate("&c&l血&3&l羽&f! &7你的附魔使对方强制掉落了一根 &f1x &3时髦的羽毛"));
                target.sendMessage(CC.translate("&c&l血&3&l羽&f! &7对方的附魔使你强制掉落了一根 &f1x &3时髦的羽毛"));
            }
        }
    }

    private int IIiIIi(int a) {
        return switch (a) {
            case 2 -> 50;
            case 3 -> 35;
            default -> 25;
        };
    }

    private String iIiIii(int a) {
        return switch (a) {
            case 2 -> "&6";
            case 3 -> "&c";
            default -> "&e";
        };
    }
}
