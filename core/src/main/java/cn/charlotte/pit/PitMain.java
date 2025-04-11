package cn.charlotte.pit;

import cn.charlotte.pit.impl.PitInternalImpl;


public class PitMain {

    private static PitHook hook;

    public static void start() {
        postStart();
    }

    public static void postStart() {

        ThePit.getInstance().loadListener();
        ThePit.setApi(PitInternalImpl.INSTANCE);

        hook = PitHook.INSTANCE;
        hook.init();
        PitInternalImpl.INSTANCE.setLoaded(true);
    }

}
