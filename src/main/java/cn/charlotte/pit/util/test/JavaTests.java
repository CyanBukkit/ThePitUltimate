package cn.charlotte.pit.util.test;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;

public class JavaTests {
    @SneakyThrows
    public static void main(String[] args) {
        WeakHashMap<UUID,Object> uuid = new WeakHashMap<>();
        HashMap<Object,UUID> uuid2 = new HashMap<>();
        UUID uuid1 = UUID.randomUUID();
        uuid.put(uuid1,new Object());
        Object key = new Object();
        uuid2.put(key,uuid1);
        Thread.sleep(10000);
        System.gc();
        System.out.println(uuid.size());
        //uuid2.remove(key);
        key = null;
        uuid1 = null;
        System.gc();
        Thread.sleep(10000);
        System.out.println(uuid.size());
    }
}
