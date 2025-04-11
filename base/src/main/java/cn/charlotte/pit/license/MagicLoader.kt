package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import lombok.SneakyThrows;
import pku.yim.license.MagicLicense;
import pku.yim.license.Response;

public class KQC {

    private static final Object lock = new Object();
    private static Exception exception;
    private static volatile boolean isLoaded;

    public static void hook() {
        new Thread(() -> {
            try {
                MagicLicense magicLicense = new MagicLicense(ThePit.getInstance());
                Response response = magicLicense.authenticate(
                        ThePit.getInstance().getDescription().getName(),
                        ThePit.getInstance().getDescription().getVersion(),
                        "AQEBAQEBAX8=",
                        false
                );
                ThePit.getInstance().info(response.toString());
                synchronized (lock) {
                    isLoaded = true;
                    lock.notifyAll();
                }
            } catch (Exception ex) {
                synchronized (lock) {
                    exception = ex;
                    lock.notifyAll();
                }
            }
        }).start();
    }

    @SneakyThrows
    public static synchronized void ensureIsLoaded() {
        if (!isLoaded) {
            synchronized (lock) {
                lock.wait();
            }
            if (exception != null) {
                exception.printStackTrace();
                System.exit(-114514);
            }
        }
    }
}
