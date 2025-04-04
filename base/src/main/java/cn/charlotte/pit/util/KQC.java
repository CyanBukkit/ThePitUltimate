package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import lombok.SneakyThrows;
import pku.yim.license.MagicLicense;

public class KQC {
    private static MagicLicense LICENSE;
    private static Exception e;
    //Python Ptyhon Java Jvav
    public final static void hook(){
        new Thread(() -> {
            try {
                MagicLicense magicLicense = new MagicLicense(ThePit.getInstance());
                magicLicense.authenticate("Ptyhon", PublicUtil.signVer,"AQEBAQEBAX8=", false);
                LICENSE = magicLicense;
            } catch (Exception e){
                KQC.e = e;
            }
            }).start();
    }
    @SneakyThrows
    public synchronized strictfp final static void ensureIsLoaded(){
        while(true){
            if(e != null){
                e.printStackTrace();
                System.exit(-114514);
                Thread.currentThread().getThreadGroup().enumerate(new Thread[0]);
            }
            if(LICENSE == null){
                Thread.onSpinWait();
                Thread.sleep(1);
            } else {
                break;
            }
        }
    }
}
