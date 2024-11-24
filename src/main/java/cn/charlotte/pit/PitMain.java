package cn.charlotte.pit;

import cn.charlotte.pit.impl.PitInternalImpl;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.io.IOException;
import java.net.InetAddress;

public class PitMain {
    private static PitHook hook;

    @NativeObfuscation(obfuscated = true)
    public static void start() {
        try {
            InetAddress address = InetAddress.getByName("thepit.nyacho.cn");
            boolean a = address.isReachable(3000);

            if (a) {
                ThePit.getInstance().loadListener();

                ThePit.setApi(PitInternalImpl.INSTANCE);

                hook = PitHook.INSTANCE;
                hook.init();
                PitInternalImpl.INSTANCE.setLoaded(true);
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

}
