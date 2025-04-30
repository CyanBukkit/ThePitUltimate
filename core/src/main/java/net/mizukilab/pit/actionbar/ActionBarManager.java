package net.mizukilab.pit.actionbar;

import cn.hutool.core.lang.mutable.MutablePair;
import io.irina.backports.utils.SWMRHashTable;
import net.mizukilab.pit.util.chat.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 不要使用SimpleEntry, 会污染HashMap
 */
public class ActionBarManager implements IActionBarManager {

    Map<UUID, Map<String, MutablePair<String, Integer>>> multiMap = new SWMRHashTable<>();

    StringBuilder builder = new StringBuilder();
    ReentrantLock lock = new ReentrantLock();
    public void addActionBarOnQueue(Player player, String arg, String val, int repeat,boolean flush) {
        UUID uniqueId = player.getUniqueId();
        Map<String, MutablePair<String, Integer>> stringStringMap = multiMap.get(uniqueId);
        if (stringStringMap == null) {
            stringStringMap = new SWMRHashTable<>();
            multiMap.put(uniqueId, stringStringMap);
        }
        stringStringMap.put(arg, new MutablePair<>(val, repeat));
        if(flush) {
            //All are synchronous
            if (lock.tryLock()) {
                tickPiece(player.getUniqueId(), stringStringMap, false);
                lock.unlock();
            }
        }
    }

    public void addActionBarOnQueue(Player player, String arg, String val, int repeat) {
        addActionBarOnQueue(player, arg, val, repeat, false);
    }

    public void tick() {
        lock.lock();
        ((SWMRHashTable<UUID, Map<String, MutablePair<String, Integer>>>) multiMap).removeIf((uuid, mappedString) -> { //forEach as multimap
            return tickPiece(uuid, mappedString,true);
        });
        lock.unlock();
    }

    private boolean tickPiece(UUID uuid, Map<String, MutablePair<String, Integer>> mappedString,boolean reduce) {
        Player player = Bukkit.getPlayer(uuid); //get Players
        if (mappedString.isEmpty() || player == null || !player.isOnline()) {
            //remove immediately
            return true;
        }
        AtomicBoolean ab = new AtomicBoolean(false);
        AtomicInteger index = new AtomicInteger();
        int size = mappedString.size();
        ((SWMRHashTable<String, MutablePair<String, Integer>>) mappedString).removeIf((key, value) -> {
            String rawString = value.getKey();
            Integer repeat = value.getValue();
            builder.append(rawString);
            if (index.incrementAndGet() < size) {
                builder.append("&7| ");
            }
            int i1 = reduce ? --repeat : repeat;
            if (i1 <= 0) {
                return true;
            } else if (!rawString.isEmpty()) {
                ab.set(true);
            }
            value.setValue(i1); //setting the value instead create new one, do not use SimpleEntry, because of its rehashing operation
            return false;
        });
        if (ab.get()) {
            ActionBarUtil.sendActionBar0(player, builder.toString());
            ; //clear it
        }
        if (!builder.isEmpty()) {
            builder.setLength(0);
        }

        return false;
    }
}
