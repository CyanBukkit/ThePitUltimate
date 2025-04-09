package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.operator.PackedOperator;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.item.*;
import cn.charlotte.pit.item.type.*;
import cn.charlotte.pit.item.type.mythic.MagicFishingRod;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.listener.PacketListener;
import cn.charlotte.pit.util.item.ItemUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    /**
     * 需要Paper支持。
     * @return
     */
    public static final net.minecraft.server.v1_8_R3.ItemStack toNMStackQuick(ItemStack item) {
        return PublicUtil.toNMStackQuick(item);
    }
    /**
     * 随机color
     */
    private static ChatColor[] CHAT_COLORS = ChatColor.values();
    public static ChatColor randomColor(){
        return CHAT_COLORS[ThreadLocalRandom.current().nextInt(Math.max(0,CHAT_COLORS.length - 1))];
    }
    /**
     * 标记GC and Exit
     * @param projectile current Entity
     */
    public static void pointMetadataAndRemove(Entity projectile, int later, String... metadata){
    Bukkit.getScheduler().runTaskLater(ThePit.getInstance(),() -> {
            for (String metadatum : metadata) {
                projectile.removeMetadata(metadatum,ThePit.getInstance());
            }
        },later);
    }
    /**
     * 超级快，nano respond o(":".length())
     * 0 - len
     */
    public static void readEnchantments(Object2IntMap<AbstractEnchantment> ment,NBTTagList nbtTagList) {
        nbtTagList.list.forEach(nbtBase -> {
            if (nbtBase instanceof NBTTagString nbtTagString) {
                String s = nbtTagString.a_(); //read NBT
                final int offset = 2;
                final int strLen = s.length();
                for (int length = strLen - offset; length > 0; length--) {
                    char splitArg = s.charAt(length);
                    boolean fastEqual = splitArg == ':'; //check equal
                    if (fastEqual) { //nano respond 10x faster
                        int lengthUnsigned = length + 1;
                        int level;
                        if (lengthUnsigned + 1 == strLen) {
                            level = Character.getNumericValue(s.charAt(length + 1)); //read level as char
                        } else {
                            String substring = s.substring(length + 1, strLen);
                            level = Integer.parseInt(substring); //read Level as string
                        }
                        String substring = s.substring(0, length);
                        AbstractEnchantment enchantment = ThePit.getInstance()
                                .getEnchantmentFactor()
                                .getEnchantmentMap()
                                .get(substring);
                        if (enchantment == null) {
                            return;
                        }
                        ment.put(enchantment, level);
                    }
                }
            }
        });
    }
    /**
     * 超级高效的split方法。
     *
     * @param line string
     * @return a array of strings
     */
    public static String[] splitByCharAt(final String line, final char delimiter)
    {
        return PublicUtil.splitByCharAt(line, delimiter);
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
    public static int getEnchantLevel(AbstractPitItem item, AbstractEnchantment enchObj) {
        if (item == null) {
            return -1;
        }

        return item.getEnchantmentLevel(enchObj);
    }
    public static int getEnchantLevel(AbstractPitItem item,String enchantName){
        if (item == null) {
            return -1;
        }
        return item.getEnchantmentLevel(enchantName);
    }
    public static String dumpNBTOnString(ItemStack stack){
        NBTTagCompound tag = Utils.toNMStackQuick(stack).getTag();
        return tag.toString();
    }
    public static IMythicItem getMythicItem(ItemStack item){
        ThePit instance = ThePit.getInstance();
        if(instance != null){
            ItemFactory itemFactory = (ItemFactory) instance.getItemFactory();
            if(itemFactory != null){
                return itemFactory.getIMythicItem(item);
            }
        }
        return getMythicItem0(item);
    }
    public static PackedOperator constructUnsafeOperator(String searchName){
        PlayerProfile playerProfile = PlayerProfile.loadPlayerProfileByName(searchName);
        PackedOperator packedOperator = new PackedOperator(ThePit.getInstance());
        if(playerProfile == null){
            return packedOperator;
        }
        packedOperator.loadAs(playerProfile);
        return packedOperator;
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

        final IMythicItem mythicItem = (IMythicItem) FuncsKt.toMythicItem(item);
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

        final IMythicItem mythicItem = (IMythicItem) FuncsKt.toMythicItem(item);
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
    public static boolean isNPC(org.bukkit.entity.Entity entity){
        return entity.getName().equals("bot");
    }
    public static ItemStack subtractLive(ItemStack item) {
        if (item == null) {
            return null;
        }
        return subtractLive(((ItemFactory)ThePit.getInstance().getItemFactory()).getIMythicItemSync(item));
    }

    public static ItemStack subtractLive(IMythicItem item) {


        if (item == null) return null;
        if (item.isEnchanted()) {
            if (item.getLive() <= 1) {
                return new ItemStack(Material.AIR);
            } else {
                item.setLive(item.getLive() - 1);
                return item.toItemStack();
            }
        }
        return item.toItemStack();
    }

    public static void addCommonHandler(@NotNull PacketListener packetListener) {
        PublicUtil.addCommonHandler(packetListener);
    }
}
