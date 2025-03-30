package cn.charlotte.pit.actionbar;

import cn.charlotte.pit.util.chat.ActionBarUtil;
import cn.klee.backports.utils.SWMRHashTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionBarManager{
    Map<UUID,Map<String, Map.Entry<String,Integer>>> multiMap = new SWMRHashTable<>();
    public void addActionBarOnQueue(Player player,String arg,String val,int repeat) {
        UUID uniqueId = player.getUniqueId();
        Map<String, Map.Entry<String,Integer>> stringStringMap = multiMap.get(uniqueId);
        if (stringStringMap == null) {
            stringStringMap = new SWMRHashTable<>();
            multiMap.put(uniqueId, stringStringMap);
        }
         stringStringMap.put(arg,new
                 AbstractMap.SimpleEntry<>(val,repeat));
    }
    public void tick() {

        StringBuilder builder = new StringBuilder();
        ((SWMRHashTable<UUID,Map<String, Map.Entry<String,Integer>>>)multiMap).removeIf((uuid, mappedString) -> { //forEach as multimap
            Player player = Bukkit.getPlayer(uuid); //get Players
            if (mappedString.isEmpty() || player == null || !player.isOnline()) {
                //point to gc
                return true; //save performances
            }
            AtomicBoolean ab = new AtomicBoolean(false);
            AtomicInteger index = new AtomicInteger();
            int size = mappedString.size();
            ((SWMRHashTable<String, Map.Entry<String,Integer>>)mappedString).removeIf((key, value) -> {
                String rawString = value.getKey();
                Integer repeat = value.getValue();
                builder.append(rawString);
                if(index.getAndIncrement() < size) {
                    builder.append(" ");
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
                builder.setLength(0); //clear it
            }
            return false;
        });
    }
}
