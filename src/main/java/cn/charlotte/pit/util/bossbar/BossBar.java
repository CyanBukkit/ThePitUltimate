package cn.charlotte.pit.util.bossbar;

import cn.charlotte.pit.ThePit;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BossBar {
    private net.kyori.adventure.bossbar.BossBar bar;
    private Set<UUID> viewers = new HashSet<>();
    private String title;

    public BossBar(String title) {
        this.title = title;
        bar = net.kyori.adventure.bossbar.BossBar.bossBar(Component.text(title), 1f, net.kyori.adventure.bossbar.BossBar.Color.PURPLE, net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS);
    }

    public void addPlayer(Player player) {
        if (viewers.add(player.getUniqueId())) {
            ThePit.getInstance().getAudiences().player(player).showBossBar(bar);
        }
    }

    public void removePlayer(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            ThePit.getInstance().getAudiences().player(player).hideBossBar(bar);
        }
    }

    public void setProgress(float progress) {
        bar.progress(progress);
    }

    public void update() {
    }

    public void update(Player player) {
    }

    public Location getWitherLocation(Location location) {
        return location.clone().add(location.getDirection().multiply(60));
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        if (this.title.equals(title)) return;
        this.title = title;
        bar.name(Component.text(title));
    }

    public Set<UUID> getViewers() {
        return viewers;
    }
}
