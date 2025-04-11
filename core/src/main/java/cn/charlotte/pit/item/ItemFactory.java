package cn.charlotte.pit.item;

import cn.charlotte.pit.util.ItemGlobalReference;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.item.ItemUtil;
import com.google.common.annotations.Beta;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.AsyncCatcher;

import java.util.UUID;

public class ItemFactory implements IItemFactory {

    public boolean clientSide = false;
    //这里是存玩家的
    ItemGlobalReference theReference = new ItemGlobalReference(() -> Bukkit.getOnlinePlayers().size() * 60L);

    //简易LRU
    public boolean hasItem(UUID uuid) {
        return theReference.containsKey(uuid.toString());
    }

    @Override
    public void setClientSide(boolean clientSide) {
        this.clientSide = clientSide;
    }

    @Override
    public boolean getClientSide() {
        return clientSide;
    }

    @Override
    public AbstractPitItem getAbstractPitItem(UUID uuid) {
        return getItem(uuid);
    }

    @Override
    public AbstractPitItem getItemFromStack(ItemStack stack) {
        return getIMythicItem(stack);
    }

    public IMythicItem getItem(UUID uuid) {
        return theReference.get(uuid.toString());
    }

    Runnable EMPTY_RUNNABLE = () -> {
    };

    public void lru() {
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
        if (!Bukkit.isPrimaryThread()) { //async get sucks
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
