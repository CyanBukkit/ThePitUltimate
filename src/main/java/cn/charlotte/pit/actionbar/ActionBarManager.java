package cn.charlotte.pit.actionbar;

import cn.charlotte.pit.util.chat.ActionBarUtil;
import cn.charlotte.pit.util.item.Attributes;
import cn.klee.backports.utils.SWMRHashTable;
import com.comphenix.protocol.PacketType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionBarManager{
    Map<UUID,Map<String, Map.Entry<String,Integer>>> multiMap = new SWMRHashTable<>();
    public void addActionBarOnQueue(Player player,String arg,String val,int repeat) {
        UUID uniqueId = player.getUniqueId();
        Map<String, Map.Entry<String,Integer>> stringStringMap = multiMap.get(uniqueId);
        if (stringStringMap == null) {
            stringStringMap = new SWMRHashTable<>();
            multiMap.put(uniqueId, stringStringMap);
        }
         stringStringMap.put(arg,Map.entry(val,repeat));
    }
    public void tick() {
        Set<UUID> uuids = new ObjectOpenHashSet<>();
        multiMap.forEach((uuid, mappedString) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (mappedString.size() <= 0 || player == null || !player.isOnline()) {
                uuids.add(uuid);
                return;
            }
            StringBuilder builder = new StringBuilder();
            Set<String> removal = new ObjectOpenHashSet<>();
            AtomicBoolean ab = new AtomicBoolean(false);
            mappedString.forEach((key, value) -> {
                ab.set(true);
                String rawString = value.getKey();
                Integer repeat = value.getValue();
                builder.append(rawString);
                builder.append(" &7| ");
                int i1 = --repeat;
                if (i1 <= 0) {
                    removal.add(key);
                    return;
                }
                mappedString.put(key, Map.entry(rawString, i1));
            });
            if (ab.get()) {
                builder.delete(builder.length() - 5, builder.length());
                ActionBarUtil.sendActionBar0(player, builder.toString());
                removal.forEach(mappedString::remove);
            }
        });
        uuids.forEach(multiMap::remove);
    }
}
