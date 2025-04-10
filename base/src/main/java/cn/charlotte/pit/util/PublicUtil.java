package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PublicUtil {

    public static String signVer = "Loader";
    public static String itemVersion = "Loader";

    public static net.minecraft.server.v1_8_R3.ItemStack toNMStackQuick(ItemStack item) {
        if (item instanceof CraftItemStack) {
            try {
                java.lang.reflect.Field handleField = CraftItemStack.class.getDeclaredField("handle");
                handleField.setAccessible(true);
                return (net.minecraft.server.v1_8_R3.ItemStack) handleField.get(item);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to access the handle field", e);
            }
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
    public static String[] splitByCharAt(final String line, final char delimiter) {
        CharSequence[] temp = new CharSequence[(line.length() / 2) + 1];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(delimiter); // first substring

        while (j >= 0) {
            temp[wordCount++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }

        temp[wordCount++] = line.substring(i); // last substring

        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);

        return result;
    }

    public static void register(Class<?> clazz, Object instance, List<IPlayerDamaged> playerDamageds, List<IAttackEntity> attackEntities, List<IItemDamage> iItemDamages, List<IPlayerBeKilledByEntity> playerBeKilledByEntities, List<IPlayerKilledEntity> playerKilledEntities, List<IPlayerRespawn> playerRespawns, List<IPlayerShootEntity> playerShootEntities) {
        if (instance instanceof Listener && instance.getClass().isAnnotationPresent(AutoRegister.class)) {
            Bukkit.getPluginManager().registerEvents((Listener) instance, ThePit.getInstance());
        }

        if (IPlayerDamaged.class.isAssignableFrom(clazz)) {
            playerDamageds.add((IPlayerDamaged) instance);
        }
        if (IAttackEntity.class.isAssignableFrom(clazz)) {
            attackEntities.add((IAttackEntity) instance);
        }
        if (IItemDamage.class.isAssignableFrom(clazz)) {
            iItemDamages.add((IItemDamage) instance);
        }
        if (IPlayerBeKilledByEntity.class.isAssignableFrom(clazz)) {
            playerBeKilledByEntities.add((IPlayerBeKilledByEntity) instance);
        }
        if (IPlayerKilledEntity.class.isAssignableFrom(clazz)) {
            playerKilledEntities.add((IPlayerKilledEntity) instance);
        }
        if (IPlayerRespawn.class.isAssignableFrom(clazz)) {
            playerRespawns.add((IPlayerRespawn) instance);
        }
        if (IPlayerShootEntity.class.isAssignableFrom(clazz)) {
            playerShootEntities.add((IPlayerShootEntity) instance);
        }
    }

    public static void unregister(Class<?> clazz, Object instance, List<IPlayerDamaged> playerDamageds, List<IAttackEntity> attackEntities, List<IItemDamage> iItemDamages, List<IPlayerBeKilledByEntity> playerBeKilledByEntities, List<IPlayerKilledEntity> playerKilledEntities, List<IPlayerRespawn> playerRespawns, List<IPlayerShootEntity> playerShootEntities) {
        if (instance instanceof Listener && instance.getClass().isAnnotationPresent(AutoRegister.class)) {
            HandlerList.unregisterAll((Listener) instance);
        }

        if (IPlayerDamaged.class.isAssignableFrom(clazz)) {
            playerDamageds.remove((IPlayerDamaged) instance);
        }
        if (IAttackEntity.class.isAssignableFrom(clazz)) {
            attackEntities.remove((IAttackEntity) instance);
        }
        if (IItemDamage.class.isAssignableFrom(clazz)) {
            iItemDamages.remove((IItemDamage) instance);
        }
        if (IPlayerBeKilledByEntity.class.isAssignableFrom(clazz)) {
            playerBeKilledByEntities.remove((IPlayerBeKilledByEntity) instance);
        }
        if (IPlayerKilledEntity.class.isAssignableFrom(clazz)) {
            playerKilledEntities.remove((IPlayerKilledEntity) instance);
        }
        if (IPlayerRespawn.class.isAssignableFrom(clazz)) {
            playerRespawns.remove((IPlayerRespawn) instance);
        }
        if (IPlayerShootEntity.class.isAssignableFrom(clazz)) {
            playerShootEntities.remove((IPlayerShootEntity) instance);
        }
    }
}
