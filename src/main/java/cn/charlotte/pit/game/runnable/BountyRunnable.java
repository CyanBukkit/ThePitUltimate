package cn.charlotte.pit.game.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.enchantment.type.limit.GuoQing2022Ench;
import cn.charlotte.pit.events.genesis.team.GenesisTeam;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.hologram.Hologram;
import cn.charlotte.pit.util.hologram.HologramAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/1 17:33
 */
public class BountyRunnable implements Runnable {
    private final Random random = new Random();
    private final Map<UUID, AnimationData> animationDataMap = new HashMap<>();

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() == 0) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtil.isStaff(player)) {
                continue;
            }
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (profile.getBounty() >= 500 || profile.getBounty() < 0) {
                animationDataMap.putIfAbsent(player.getUniqueId(), new AnimationData());
                String color = "&6";
                if (ThePit.getInstance().getPitConfig().isGenesisEnable()) {
                    if (profile.getGenesisData().getTeam() == GenesisTeam.ANGEL) {
                        color = "&b";
                    }
                    if (profile.getGenesisData().getTeam() == GenesisTeam.DEMON) {
                        color = "&c";
                    }
                    ItemStack itemInHand = player.getItemInHand();
                    if (GuoQing2022Ench.instance.isItemHasEnchant(itemInHand)) {
                        color = "&4";
                    }
                }
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

    private void playAnimation(Player player, int bounty, String color) {
        AnimationData animationData = animationDataMap.get(player.getUniqueId());
        List<HologramDisplay> holograms = animationData.holograms;

        if (animationData.spawnCooldown.hasExpired()) {
            Location playerLocation = player.getLocation();
            double x = generatorLocDouble();
            double z = generatorLocDouble();
            Hologram newHologram;
            newHologram = HologramAPI.createHologram(playerLocation.clone().add(x, 0, z), CC.translate(color + "&l" + bounty + "g"));

            List<Player> reviewers = new ArrayList<>(Bukkit.getOnlinePlayers());
            reviewers.remove(player);
            reviewers.removeIf(target -> PlayerProfile.getPlayerProfileByUuid(target.getUniqueId()).getPlayerOption().isBountyHiddenWhenNear() && PlayerUtil.getDistance(target, player) < 8);

            newHologram.spawn(reviewers);
            holograms.add(new HologramDisplay(newHologram, x, z));

            animationData.spawnCooldown = new Cooldown(650);
        }

        List<HologramDisplay> shouldRemove = new ArrayList<>();
        for (HologramDisplay hologram : holograms) {
            Location location = player.getLocation().clone();
            location.setX(location.getX() + hologram.boostX);
            location.setY(hologram.getHologram().getLocation().getY() + 0.25);
            location.setZ(location.getZ() + hologram.boostZ);
            hologram.getHologram().setLocation(location);
            if (hologram.timer.hasExpired()) {
                hologram.hologram.deSpawn();
                shouldRemove.add(hologram);
            }
        }
        holograms.removeAll(shouldRemove);
    }

    private double generatorLocDouble() {
        if (random.nextBoolean()) {
            return random.nextDouble();
        } else {
            return -random.nextDouble();
        }
    }

    public static class AnimationData {
        private final List<HologramDisplay> holograms;
        private Cooldown spawnCooldown;

        public AnimationData() {
            this.holograms = new ArrayList<>();
            this.spawnCooldown = new Cooldown(0);
        }
    }

    @Getter
    public static class HologramDisplay {
        private final Hologram hologram;
        private final double boostX;
        private final double boostZ;
        private final Cooldown timer;

        public HologramDisplay(Hologram hologram, double boostX, double boostZ) {
            this.hologram = hologram;
            this.boostX = boostX;
            this.boostZ = boostZ;
            this.timer = new Cooldown(660, TimeUnit.MILLISECONDS);
        }
    }
}
