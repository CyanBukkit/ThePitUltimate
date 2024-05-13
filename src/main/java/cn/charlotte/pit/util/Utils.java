package cn.charlotte.pit.util;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.MythicColor;
import cn.charlotte.pit.item.type.AngelChestplate;
import cn.charlotte.pit.item.type.ArmageddonBoots;
import cn.charlotte.pit.item.type.GoldenHelmet;
import cn.charlotte.pit.item.type.LuckyChestplate;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.util.item.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Utils {

    /**
     * 返回-1为没有
     *
     * @param item
     * @param enchantName
     * @return
     */
    public static int getEnchantLevel(ItemStack item, String enchantName) {
        final IMythicItem mythicItem = getMythicItem(item);
        if (mythicItem == null) {
            return -1;
        }

        for (Map.Entry<AbstractEnchantment, Integer> entry : mythicItem.getEnchantments().entrySet()) {
            if (entry.getKey().getNbtName().equals(enchantName)) {
                return entry.getValue();
            }
        }

        return -1;
    }

    public static IMythicItem getMythicItem(ItemStack item) {
        final String internalName = ItemUtil.getInternalName(item);
        IMythicItem mythicItem = null;
        if ("mythic_sword".equals(internalName)) {
            mythicItem = new MythicSwordItem();
        } else if ("mythic_bow".equals(internalName)) {
            mythicItem = new MythicBowItem();
        } else if ("mythic_leggings".equals(internalName)) {
            mythicItem = new MythicLeggingsItem();
        } else if ("angel_chestplate".equals(internalName)) {
            mythicItem = new AngelChestplate();
        } else if ("armageddon_boots".equals(internalName)) {
            mythicItem = new ArmageddonBoots();
        } else if ("kings_helmet".equals(internalName)) {
            mythicItem = new GoldenHelmet();
        } else if ("lucky_chestplate".equals(internalName)) {
            mythicItem = new LuckyChestplate();
        } else {
            return null;
        }

        mythicItem.loadFromItemStack(item);

        return mythicItem;
    }

    public static boolean canUseGen(ItemStack item) {
        if (item == null) {
            return false;
        }

        final IMythicItem mythicItem = FuncsKt.toMythicItem(item);
        if (mythicItem == null || !mythicItem.isEnchanted() || mythicItem.isBoostedByGem()) {
            return false;
        }

        if (mythicItem.getColor() == MythicColor.DARK) {
            return false;
        }

        for (Map.Entry<AbstractEnchantment, Integer> entry : mythicItem.getEnchantments().entrySet()) {
            if (entry.getKey().getRarity().getParentType() != EnchantmentRarity.RarityType.RARE && entry.getValue() < entry.getKey().getMaxEnchantLevel()) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack subtractLive(ItemStack item) {
        if (item == null) {
            return null;
        }

        final IMythicItem mythicLeggings = MythicUtil.getMythicItem(item);
        if (mythicLeggings == null) return null;
        if (mythicLeggings.isEnchanted()) {
            if (mythicLeggings.getLive() <= 1) {
                return new ItemStack(Material.AIR);
            } else {
                mythicLeggings.setLive(mythicLeggings.getLive() - 1);
                return mythicLeggings.toItemStack();
            }
        }
        return item;
    }

}
