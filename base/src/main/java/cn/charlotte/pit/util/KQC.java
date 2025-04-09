package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import lombok.SneakyThrows;
import pku.yim.license.MagicLicense;

public class KQC {
    private static final Object lock = new Object();
    private static Exception e;
    private static volatile boolean loaded;
    public static void hook(){
        new Thread(() -> {
            try {
                MagicLicense magicLicense = new MagicLicense(ThePit.getInstance());

                magicLicense.authenticate("Ptyhon", PublicUtil.signVer,"AQEBAQEBAX8=", false);
                synchronized (lock) {
                    lock.notify();
                }
                loaded = true;
            } catch (Exception e){
                KQC.e = e;
                lock.notify();
            }
            }).start();
    }
    @SneakyThrows
    public synchronized strictfp static void ensureIsLoaded(){
        if(!loaded){
            synchronized (lock){
                lock.wait();
            }
            if(e != null){
                e.printStackTrace();
                System.exit(-114514);
                Thread.currentThread().getThreadGroup().enumerate(new Thread[0]);
            }
        }
    }

}
