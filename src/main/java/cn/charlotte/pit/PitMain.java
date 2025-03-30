package cn.charlotte.pit;

import cn.charlotte.pit.impl.PitInternalImpl;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.io.IOException;
import java.net.InetAddress;

public class PitMain {
    private static PitHook hook;

    @NativeObfuscation(obfuscated = true)
    public static void start() {
        ThePit.getInstance().loadListener();

        ThePit.setApi(PitInternalImpl.INSTANCE);

        hook = PitHook.INSTANCE;
        hook.init();
        PitInternalImpl.INSTANCE.setLoaded(true);
    }

}
