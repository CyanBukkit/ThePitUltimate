package cn.charlotte.pit.util;

import cn.charlotte.pit.item.IMythicItem;
import cn.klee.backports.utils.SWMRHashTable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class ItemGlobalReference extends Object2ObjectLinkedOpenHashMap<String, IMythicItem> {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    AtomicBoolean removeLast = new AtomicBoolean(false);
    LongSupplier limit;
    public ItemGlobalReference(LongSupplier limit){
        super();
        this.limit = limit;
    }
    public IMythicItem getValue(String key) {
        return get(key);
    }
    public IMythicItem getValue(UUID key){
        return getValue(key.toString());
    }

    public void putValue(String key, IMythicItem value) {
        boolean opaque = removeLast.get();
        if(!opaque) {
            long asLong = limit.getAsLong();
            if (size > asLong) {

                removeLast.setOpaque(true);
                for (int i = size; i > asLong; i--) {
                    this.removeLast();
                }

                removeLast.setOpaque(false);
            }
        }
        put(key, value);
    }
    @Override
    public IMythicItem put(String uuid, IMythicItem mythicItem) {
        try {
            lock.writeLock().lock();
            return super.put(uuid, mythicItem);
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void putValue(UUID uuid, IMythicItem item){
        putValue(uuid.toString(),item);
    }

    @Override
    public IMythicItem removeLast() {
        try {
            lock.writeLock().lock();
            return super.removeLast();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public IMythicItem remove(UUID key) {
        try {
            lock.writeLock().lock();
            return super.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
