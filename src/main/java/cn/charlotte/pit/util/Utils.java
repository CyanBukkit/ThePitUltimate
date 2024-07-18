package cn.charlotte.pit.util;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.MythicColor;
import cn.charlotte.pit.item.type.*;
import cn.charlotte.pit.item.type.mythic.MagicFishingRod;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.util.item.ItemUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Utils {
    /**
     * 需要Paper支持。
     * @return
     */
    public static final net.minecraft.server.v1_8_R3.ItemStack toNMStackQuick(ItemStack item) {
        if (item instanceof CraftItemStack) {
            return ((CraftItemStack) item).handle;
        } else {
            return CraftItemStack.asNMSCopy(item);
        }
    }
    /**
     * 超级高效的split方法。
     *
     * @param str regx
     * @return a array of strings
     */
    public static String[] splitByCharAt(String str, char regx) {
        //字符串截取的开始位置
        int begin = 0;
        //截取分割得到的字符串
        String splitStr = "";
        List<String> strL = new ObjectArrayList<>();
        int length = str.length();
        //计数器
        int i = 0;
        int splitted;
        for (i = 0; i < length;i++ ) {
            if (str.charAt(i) == regx) {
                splitStr = str.substring(begin, i);
                strL.add(splitStr);
                str = str.substring(i + 1, length);
                length = str.length();
                i = 0;

            }
        }
        if (!str.isBlank()) {
            strL.add(str);
        }
        return strL.toArray(new String[0]);
    }
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
        } else if ("jewel_sword".equals(internalName)) {
            mythicItem = new JewelSword();
        } else if ("magic_fishing_rod".equals(internalName)) {
            mythicItem = new MagicFishingRod();
        }
        else {
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
