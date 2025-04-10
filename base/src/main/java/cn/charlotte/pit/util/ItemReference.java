package cn.charlotte.pit.util;

import io.irina.backports.utils.SWMRHashTable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ItemReference extends SWMRHashTable<UUID, Set<UUID>> {

    public Set<UUID> getRef(Player player) {
        UUID uniqueId = player.getUniqueId();
        Set<UUID> uuids = get(uniqueId);
        if (uuids == null) {
            ObjectOpenHashSet<UUID> value = new ObjectOpenHashSet<>();
            put(uniqueId, value);
            return value;
        }
        return uuids;
    }

    public void removeRef(Player player) {
        remove(player.getUniqueId());
    }

    public void addRefCount(Player player, UUID uuid) {
        getRef(player).add(uuid);
    }

    public void removeRef(Player player, UUID uuid) {
        this.getRef(player).remove(uuid);
    }
}
