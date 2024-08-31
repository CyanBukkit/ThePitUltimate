package cn.charlotte.pit.item;

import cn.charlotte.pit.PitHook;
import cn.charlotte.pit.util.ItemGlobalReference;
import cn.charlotte.pit.util.ItemReference;
import cn.charlotte.pit.util.Utils;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.klee.backports.utils.SWMRHashTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
public class ItemFactory {

    public boolean clientSide = false;
    //这里是存玩家的
    ItemGlobalReference theReference = new ItemGlobalReference(() -> Bukkit.getOnlinePlayers().size() * 60L);
    //简易LRU
    public IMythicItem getIMythicItem(ItemStack stack) {

        Object[] objects = ItemUtil.getInternalNameAndUUID(stack); //提前判断节约0.6%
        if (objects[0] == null || objects[1] == null) {
            return null;
        }
        String internalName = (String) objects[0];
        String uuidString = (String) objects[1];
        IMythicItem iMythicItem = theReference.get(uuidString);
        if (iMythicItem == null || clientSide) {
            if(ItemUtil.shouldUpdateItem(stack)){
                if(ItemUtil.shouldUpdateUUID()){
                    ItemUtil.randomUUIDItem(stack);
                }
                ItemUtil.signVer(stack);
            }
            IMythicItem mythicItem = Utils.getMythicItem0(stack, internalName);
            if(mythicItem.uuid != null) {
                if (mythicItem.uuid.equals(IMythicItem.getDefUUID())) {
                    UUID uuid = UUID.randomUUID();
                    ItemUtil.setUUID(stack, uuid.toString());
                    mythicItem.uuid = uuid;
                }
                theReference.putValue(mythicItem.uuid, mythicItem);
            }
            return mythicItem;
        } else {
            return iMythicItem;
        }
    }

}
