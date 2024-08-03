package cn.charlotte.pit.runnable;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.hologram.Hologram;
import cn.charlotte.pit.util.hologram.HologramAPI;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/1 17:33
 */
public class BountyRunnable extends BukkitRunnable {
    private final Random random = new Random();
    private static final Map<UUID, AnimationData> animationDataMap = new HashMap<>();

    public static Map<UUID, AnimationData> getAnimationDataMap() {
        return animationDataMap;
    }

    @Override
    public void run() {
        animationDataMap.forEach((i,a) -> {
            Player player = Bukkit.getPlayer(i);
            if(player == null || !player.isOnline()){
                a.holograms.forEach(s -> {
                    s.hologram.deSpawn();
                });
            }
        });
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (profile.getBounty() >= 500 || profile.getBounty() < 0) {
                animationDataMap.putIfAbsent(player.getUniqueId(), new AnimationData());
                String color = profile.bountyColor();
                playAnimation(player, profile.getBounty(), color);
            } else {
                AnimationData animationData = animationDataMap.get(player.getUniqueId());
                if (animationData != null) {
                    for (HologramDisplay hologram : animationData.holograms) {
                        hologram.hologram.deSpawn();
                    }
                    animationData.holograms.clear();
                }
            }
        }
    }


    @SneakyThrows
    private void playAnimation(Player player, int bounty, String color) {
        AnimationData animationData = animationDataMap.get(player.getUniqueId());
        Set<HologramDisplay> holograms = animationData.holograms;

        if (holograms.size() < 3) {
            Location playerLocation = player.getLocation();
            double x = generatorLocDouble();
            double z = generatorLocDouble();
            Hologram newHologram = HologramAPI.createHologram(playerLocation.clone().add(x, 0, z), CC.translate(color + "&l" + bounty + "g"));

            List<Player> reviewers = new ArrayList<>(Bukkit.getOnlinePlayers());
            reviewers.remove(player);
            reviewers.removeIf(target -> PlayerProfile.getPlayerProfileByUuid(target.getUniqueId()).getPlayerOption().isBountyHiddenWhenNear() && PlayerUtil.getDistance(target, player) < 8);

            newHologram.spawn(reviewers);
            holograms.add(new HologramDisplay(newHologram, x, z));

            animationData.spawnCooldown = new Cooldown(650);
        }

        List<HologramDisplay> shouldRemove = new ArrayList<>();
        for (HologramDisplay hologram : holograms) {
            if (System.currentTimeMillis() > hologram.endTime) {
                hologram.hologram.deSpawn();
                shouldRemove.add(hologram);
            } else {
                Location location = player.getLocation().clone();
                location.setX(location.getX() + hologram.boostX);
                location.setY(hologram.getHologram().getLocation().getY() + 0.1);
                location.setZ(location.getZ() + hologram.boostZ);
                hologram.getHologram().setLocation(location);
            }
        }
        shouldRemove.forEach(holograms::remove);
    }

    private double generatorLocDouble() {
        if (random.nextBoolean()) {
            return random.nextDouble();
        } else {
            return -random.nextDouble();
        }
    }

    public static class AnimationData {
        private final Set<HologramDisplay> holograms;
        private Cooldown spawnCooldown;

        public AnimationData() {
            this.holograms = new ConcurrentHashSet<>();
            this.spawnCooldown = new Cooldown(0);
        }

        public Set<HologramDisplay> getHolograms() {
            return holograms;
        }

        public Cooldown getSpawnCooldown() {
            return spawnCooldown;
        }
    }

    @Getter
    public static class HologramDisplay {
        private final Hologram hologram;
        private final double boostX;
        private final double boostZ;
        private final long endTime;

        public HologramDisplay(Hologram hologram, double boostX, double boostZ) {
            this.hologram = hologram;
            this.boostX = boostX;
            this.boostZ = boostZ;
            this.endTime = System.currentTimeMillis() + 2000;
        }
    }
}
