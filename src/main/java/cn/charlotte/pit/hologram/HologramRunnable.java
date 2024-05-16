package cn.charlotte.pit.hologram;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.hologram.Hologram;
import cn.charlotte.pit.util.hologram.HologramAPI;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/4 13:25
 */
public class HologramRunnable implements Runnable {
    private long tick = 0;

    public HologramRunnable() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this, 50L, 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    @SneakyThrows
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            HologramListener.PlayerHologram hologram = HologramListener.hologramMap.get(player.getUniqueId());
            if (hologram == null) {
                continue;
            }

            Map<String, HologramListener.HologramData> data = hologram.getHologramData();
            for (AbstractHologram abstractHologram : ThePit.getInstance().getHologramFactory().loopHologram) {
                HologramListener.HologramData hologramData = data.get(abstractHologram.getInternalName());
                if (hologramData == null) {
                    continue;
                }

                if (tick % abstractHologram.loopTicks() != 0) {
                    continue;
                }

                List<String> text = abstractHologram.getText(player);
                if (text.size() != hologramData.getHolograms().size()) {
                    hologramData.getHolograms().forEach(Hologram::deSpawn);
                    hologramData.getHolograms().clear();
                    List<Hologram> holograms = new ArrayList<>();
                    for (int i = 0; i < text.size(); i++) {
                        String line = text.get(i);
                        Hologram holo = HologramAPI.createHologram(abstractHologram.getLocation().clone().add(0, -i * abstractHologram.getHologramHighInterval(), 0), CC.translate(line));
                        holo.spawn(Collections.singletonList(player));
                        holograms.add(holo);
                    }
                    hologramData.getHolograms().addAll(holograms);
                }
                for (int i = 0; i < text.size(); i++) {
                    hologramData.getHolograms().get(i).setText(CC.translate(text.get(i)));
                }
            }
        }

        tick++;
    }
}
