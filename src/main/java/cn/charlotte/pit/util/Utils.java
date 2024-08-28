package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.item.ItemFactory;
import cn.charlotte.pit.item.MythicColor;
import cn.charlotte.pit.item.type.*;
import cn.charlotte.pit.item.type.mythic.MagicFishingRod;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.util.item.ItemUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
     * @param line string
     * @return a array of strings
     */
    public static String[] splitByCharAt(final String line, final char delimiter)
    {
        CharSequence[] temp = new CharSequence[(line.length() / 2) + 1];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring

        while (j >= 0)
        {
            temp[wordCount++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }

        temp[wordCount++] = line.substring(i); // last substring

        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);

        return result;
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

        return getEnchantLevel(mythicItem,enchantName);
    }
    public static int getEnchantLevel(ItemStack item, AbstractEnchantment enchObj) {
        final IMythicItem mythicItem = getMythicItem(item);
        if (mythicItem == null) {
            return -1;
        }

        return mythicItem.getEnchantments().getInt(enchObj);
    }
    public static int getEnchantLevel(IMythicItem mythicItem, String enchantName){
        for (Map.Entry<AbstractEnchantment, Integer> entry : mythicItem.getEnchantments().entrySet()) {
            if (entry.getKey().getNbtName().equals(enchantName)) {
                return entry.getValue();
            }
        }
        return -1;
    }
    public static String dumpNBTOnString(ItemStack stack){
        NBTTagCompound tag = Utils.toNMStackQuick(stack).getTag();
        return tag.toString();
    }
    public static IMythicItem getMythicItem(ItemStack item){
        ThePit instance = ThePit.getInstance();
        if(instance != null){
            ItemFactory itemFactory = instance.getItemFactory();
            if(itemFactory != null){
                return itemFactory.getIMythicItem(item);
            }
        }
        return getMythicItem0(item);
    }
    public static IMythicItem getMythicItem0(ItemStack item, String internalName){
        IMythicItem mythicItem = null;
        if(internalName == null){ //提前skip, 不需要name。
            return null;
        }
        switch (internalName) {
            case "mythic_sword" -> mythicItem = new MythicSwordItem();
            case "mythic_bow" -> mythicItem = new MythicBowItem();
            case "mythic_leggings" -> mythicItem = new MythicLeggingsItem();
            case "angel_chestplate" -> mythicItem = new AngelChestplate();
            case "armageddon_boots" -> mythicItem = new ArmageddonBoots();
            case "kings_helmet" -> mythicItem = new GoldenHelmet();
            case "lucky_chestplate" -> mythicItem = new LuckyChestplate();
            case "jewel_sword" -> mythicItem = new JewelSword();
            case "magic_fishing_rod" -> mythicItem = new MagicFishingRod();
            default -> {
                return null;
            }
        }

        mythicItem.loadFromItemStack(item);

        return mythicItem;
    }
    public static IMythicItem getMythicItem0(ItemStack item) {
        final String internalName = ItemUtil.getInternalName(item);
        return getMythicItem0(item,internalName);
    }

    public static boolean canUseGen(ItemStack item) {
        if (item == null) {
            return false;
        }

        final IMythicItem mythicItem = FuncsKt.toMythicItem(item);
        if (mythicItem == null || !mythicItem.isEnchanted() || mythicItem.isBoostedByGem() || mythicItem.isBoostedByGlobalGem()) {
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
    public static boolean canUseGlobalAttGem(ItemStack item) {
        if (item == null) {
            return false;
        }

        final IMythicItem mythicItem = FuncsKt.toMythicItem(item);
        if (mythicItem == null || !mythicItem.isEnchanted() || mythicItem.isBoostedByGem() || mythicItem.isBoostedByGlobalGem()) {
            return false;
        }
        if (mythicItem.getColor() == MythicColor.DARK) {
            return false;
        }

        for (Map.Entry<AbstractEnchantment, Integer> entry : mythicItem.getEnchantments().entrySet()) {
            if (entry.getKey().getRarity().getParentType() == EnchantmentRarity.RarityType.RARE && entry.getValue() < entry.getKey().getMaxEnchantLevel()) {
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
