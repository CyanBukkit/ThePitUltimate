package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.spigot.CarbonSpigot;
import xyz.refinedev.spigot.api.handlers.impl.PacketHandler;

import java.lang.reflect.Method;

public class PublicUtil {
    public static String signVer;
    public static String itemVersion;
    public static final net.minecraft.server.v1_8_R3.ItemStack toNMStackQuick(ItemStack item) {
        if (item instanceof CraftItemStack) {
            return ((CraftItemStack) item).handle;
        } else {
            return CraftItemStack.asNMSCopy(item);
        }
    }
    public static void addCommonHandler(PacketHandler packetHandler) {
        try {
            CarbonSpigot.getPacketAPI().registerPacketHandler(ThePit.getInstance(), packetHandler);
        } catch (Throwable a) {
            try {
                Bukkit.getLogger().warning("Error in adding the packet handler from " + packetHandler + ", using the public way...");
                Class<?> aClass = Class.forName("xyz.refinedev.spigot.api.handlers.impl.KQC");
                Method addHandler = aClass.getMethod("addHandler", PacketHandler.class);
                addHandler.invoke(null, packetHandler);
            } catch (Throwable e){
                e.printStackTrace();
                System.out.println("Error, this plugin is buggy");
            }
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
        int j = line.indexOf(delimiter); // first substring

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
}
