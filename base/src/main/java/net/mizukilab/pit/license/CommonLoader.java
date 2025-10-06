package net.mizukilab.pit.license;

import net.mizukilab.pit.Loader;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonLoader {
    protected static Loader loader = null;
    public static void bootstrap(JavaPlugin plugin) {
//        try {
//            loader.getMethod("start").invoke(null);
//        } catch (Throwable e) {
//            plugin.getLogger().warning("Fetching");
//            MagicLoader.ensureIsLoaded();
//        }
        loader.start();
    }

    public static void preBootstrap(JavaPlugin plugin) {
//        try {
//            loader = Class.forName("net.mizukilab.pit.Loader");
//        } catch (Throwable able){
//            plugin.getLogger().warning("Fetching");
//            MagicLoader.load();
//        }
        loader = Loader.INSTANCE;
    }
}
