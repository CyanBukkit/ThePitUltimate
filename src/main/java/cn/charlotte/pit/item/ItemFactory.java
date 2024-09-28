package cn.charlotte.pit.item;

import cn.charlotte.pit.PitHook;
import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.util.ItemGlobalReference;
import cn.charlotte.pit.util.ItemReference;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.klee.backports.utils.SWMRHashTable;
import com.google.common.annotations.Beta;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.AsyncCatcher;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemFactory {

    public boolean clientSide = false;
   //这里是存玩家的
    ItemGlobalReference theReference = new ItemGlobalReference(() -> Bukkit.getOnlinePlayers().size() * 60L);

    //简易LRU
    public boolean hasItem(UUID uuid) {
        return theReference.containsKey(uuid.toString());
    }

    public IMythicItem getItem(UUID uuid) {
        return theReference.get(uuid.toString());
    }

    Runnable EMPTY_RUNNABLE = () -> {
    };
    public void lru(){
        theReference.executeLRU();
    }
    @Beta
    public IMythicItem getIMythicItem(ItemStack stack) {
        return getIMythicItem(stack, EMPTY_RUNNABLE);
    }
    @Beta
    public IMythicItem getIMythicItem(ItemStack stack, Runnable runnable) {

        Object[] objects = readIMythicItemUUIDAndInternalName(stack);
        if (objects == null) return null;
        String internalName = (String) objects[0];
        String uuidString = (String) objects[1];
        IMythicItem iMythicItem = getIMythicItemFromUUIDString(uuidString);

        if (iMythicItem == null || clientSide) { //会导致不掉命bug, 有点厉害
               runnable.run();

            return getIMythicItem0(stack, internalName);
        } else {
            runnable.run();
            return iMythicItem;
        }

    }

    public Object[] readIMythicItemUUIDAndInternalName(ItemStack stack) {
        Object[] objects = ItemUtil.getInternalNameAndUUID(stack); //提前判断节约0.6%
        if (objects[0] == null || objects[1] == null) {
            return null;
        }
        return objects;
    }

    public IMythicItem getIMythicItemFromUUIDString(String uuidString) {
        IMythicItem iMythicItem;
        if (AsyncCatcher.isAsync()) { //async get sucks
            iMythicItem = theReference.get(uuidString);
        } else {
            iMythicItem = theReference.getValue(uuidString);
        }
        return iMythicItem;
    }

    public IMythicItem getIMythicItemSync(ItemStack stack) {
        return getIMythicItem(stack);
    }

    public IMythicItem getIMythicItem0(ItemStack stack, String internalName) {
        if (ItemUtil.shouldUpdateItem(stack)) {
            if (ItemUtil.shouldUpdateUUID()) {
                ItemUtil.randomUUIDItem(stack);
            }
            ItemUtil.signVer(stack);
        }

        IMythicItem mythicItem = Utils.getMythicItem0(stack, internalName);

        if (mythicItem != null) {
            if (mythicItem.uuid != null) {
                if (mythicItem.uuid.equals(IMythicItem.getDefUUID())) {
                    mythicItem.uuid = ItemUtil.randomUUIDItem(stack);
                }
                theReference.putValue(mythicItem.uuid, mythicItem);
            }
        }
        return mythicItem;
    }
}
