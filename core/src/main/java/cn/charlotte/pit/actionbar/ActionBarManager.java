package cn.charlotte.pit.actionbar;

import cn.charlotte.pit.util.chat.ActionBarUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.mutable.MutablePair;
import io.irina.backports.utils.SWMRHashTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionBarManager implements IActionBarManager{
    Map<UUID,Map<String, MutablePair<String,Integer>>> multiMap = new SWMRHashTable<>();
    public void addActionBarOnQueue(Player player,String arg,String val,int repeat) {
        UUID uniqueId = player.getUniqueId();
        Map<String, MutablePair<String,Integer>> stringStringMap = multiMap.get(uniqueId);
        if (stringStringMap == null) {
            stringStringMap = new SWMRHashTable<>();
            multiMap.put(uniqueId, stringStringMap);
        }
         stringStringMap.put(arg,new MutablePair<>(val,repeat));
    }
    public void tick() {

        StringBuilder builder = new StringBuilder();
        ((SWMRHashTable<UUID,Map<String, MutablePair<String,Integer>>>)multiMap).removeIf((uuid, mappedString) -> { //forEach as multimap
            Player player = Bukkit.getPlayer(uuid); //get Players
            if (mappedString.isEmpty() || player == null || !player.isOnline()) {
                //point to gc
                return true; //save performances
            }
            AtomicBoolean ab = new AtomicBoolean(false);
            AtomicInteger index = new AtomicInteger();
            int size = mappedString.size();
            ((SWMRHashTable<String, MutablePair<String,Integer>>)mappedString).removeIf((key, value) -> {
                String rawString = value.getKey();
                Integer repeat = value.getValue();
              //  System.out.println("Append " + rawString + " for " + player);
                builder.append(rawString);
                if(index.incrementAndGet() < size) {
                    builder.append("&7| ");
                }
                int i1 = --repeat;
                if (i1 <= 0) {
                    return true;
                } else if(!rawString.isEmpty()){
                    ab.set(true);
                }
                value.setValue(i1); //设置指针
                return false;
            });
            if (ab.get()) {
                ActionBarUtil.sendActionBar0(player, builder.toString());
                ; //clear it
            }
            if(!builder.isEmpty()) {
                builder.setLength(0);
            }

            return false;
        });
    }
}
