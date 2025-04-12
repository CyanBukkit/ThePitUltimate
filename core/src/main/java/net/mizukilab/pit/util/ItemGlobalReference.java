package net.mizukilab.pit.util;

import com.google.common.annotations.Beta;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.mizukilab.pit.item.IMythicItem;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.LongSupplier;

public class ItemGlobalReference extends Object2ObjectLinkedOpenHashMap<String, IMythicItem> {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    AtomicBoolean shouldLRU = new AtomicBoolean(false);
    AtomicBoolean removeLast = new AtomicBoolean(false);
    LongSupplier limit;

    public ItemGlobalReference(LongSupplier limit) {
        super();
        this.limit = limit;
    }

    public IMythicItem getValue(String key) {
        removeLast.setOpaque(true);
        IMythicItem andMoveToFirst = get(key);
        removeLast.setOpaque(false);
        return andMoveToFirst;
    }

    public IMythicItem getValue(UUID key) {
        return getValue(key.toString());
    }

    @Beta
    public void putValue(String key, IMythicItem value) {
        putAndMoveToFirst(key, value);
    }

    public void executeLRU() {//CAS
        if (!shouldLRU.getAcquire()) {
            return;
        }
        boolean opaque = removeLast.getAcquire();
        if (!opaque) {
            long asLong = limit.getAsLong();
            if (size > asLong) {

                removeLast.setOpaque(true);
                for (int i = size; i > asLong; i--) {
                    if (size == limit.getAsLong() || size == 0) {
                        break; //effectively
                    }
                    this.removeLast();
                }
                removeLast.setOpaque(false);
            }
            shouldLRU.setOpaque(false);
        }
    }

    @Override
    public IMythicItem putAndMoveToFirst(String string, IMythicItem mythicItem) {
        removeLast.setOpaque(true);
        lock.writeLock().lock();
        IMythicItem iMythicItem = super.putAndMoveToFirst(string, mythicItem);
        lock.writeLock().unlock();
        removeLast.setOpaque(false);
        shouldLRU.setOpaque(true);
        return iMythicItem;

    }

    @Override
    public IMythicItem put(String uuid, IMythicItem mythicItem) {
        try {
            lock.writeLock().lock();
            shouldLRU.setOpaque(true);
            return super.put(uuid, mythicItem);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void putValue(UUID uuid, IMythicItem item) {

        putValue(uuid.toString(), item);
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
