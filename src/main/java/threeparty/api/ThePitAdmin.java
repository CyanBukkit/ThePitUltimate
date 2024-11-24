package threeparty.api;

import kotlin.collections.ArrayDeque;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import threeparty.api.loop.FastVerifyEventLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@NativeObfuscation(obfuscated = true)
public class ThePitAdmin {
    List<FastVerifyEventLoop> loops = new CopyOnWriteArrayList<>();
    static ThePitAdmin INSTANCE;

    private ThePitAdmin() {
        INSTANCE = this;
    }
    @NativeObfuscation(obfuscated = false)
    public boolean shouldLoop(){
        return !loops.isEmpty();
    }
    public static FastVerifyEventLoop newLoop() {
        if (INSTANCE == null) {
            throw new RuntimeException("Has not been loaded yet");
        }
        return INSTANCE.newEventLoop();
    }

    public FastVerifyEventLoop newEventLoop() {
        FastVerifyEventLoop e = new FastVerifyEventLoop(this);
        loops.add(e);
        return e;
    }
    public void newEvent(Object object){
        loops.forEach(i -> i.loop(object));
    }
    public void destroyEventLoop(FastVerifyEventLoop e) {
        loops.remove(e);
    }
}
