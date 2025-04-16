package net.mizukilab.pit.license;

import org.bukkit.plugin.java.JavaPlugin;

public class CommonLoader {
    public static void bootstrap(JavaPlugin plugin){
        try {
            plugin.getLogger().warning("Loading from local storage");
            Class.forName("net.mizukilab.pit.Loader").getMethod("start").invoke(null);
        } catch (Exception e) {
            plugin.getLogger().warning("Fetching");
            MagicLoader.load();
            MagicLoader.ensureIsLoaded();
        }
    }
}
