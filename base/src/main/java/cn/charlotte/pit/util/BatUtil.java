package cn.charlotte.pit.util;

import cn.charlotte.pit.ThePit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BatUtil {
    public static void attachPlayerToBatsAndMove(Player player, int seconds, int health) {
        final Bat mainBat = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
        mainBat.setMetadata("CUSTOM_BAT", new FixedMetadataValue(ThePit.getInstance(), ""));
        mainBat.setPassenger(player);
        PlayerUtil.heal(player, health);
        VisibleApi.hidePlayerFromAll(player);

        List<LivingEntity> bats = spawnBats(player, 1);
        new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (ticks >= seconds * 20) {
                    Location finalLocation = mainBat.getLocation();
                    resetPlayerState(player, bats, mainBat);
                    player.teleport(finalLocation);
                    this.cancel();
                    return;
                }

                Vector direction = player.getLocation().getDirection().normalize().multiply(1);
                moveBats(mainBat, bats, direction);
                if (ticks % 10 == 0) {
                    damageNearbyEntities(mainBat, player, 5, 8);
                }
                ticks++;
            }
        }.runTaskTimer(ThePit.getInstance(), 0L, 1L);
    }

    private static void damageNearbyEntities(Entity source, Player me, double radius, double damage) {
        source.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !(entity instanceof Bat))
                .filter(entity -> !entity.equals(source) && !entity.equals(me))
                .forEach(target -> ((LivingEntity) target).damage(damage, me));
    }

    private static void resetPlayerState(Player player, List<LivingEntity> bats, Bat mainBat) {

        VisibleApi.showPlayerToAll(player);

        bats.forEach(bat -> {
            if (!bat.isDead()) {
                bat.remove();
            }
        });
        bats.clear();

        if (!mainBat.isDead()) {
            mainBat.remove();
        }
    }


    private static List<LivingEntity> spawnBats(Player player, double distance) {
        List<LivingEntity> bats = new ArrayList<>();
        Location playerLoc = player.getLocation();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location batLoc = playerLoc.clone().add(
                            x * distance + randomOffset(),
                            y * distance + 0.5 + randomOffset(),
                            z * distance + randomOffset()
                    );
                    Bat bat = player.getWorld().spawn(batLoc, Bat.class);
                    bat.setNoDamageTicks(Integer.MAX_VALUE);
                    bat.setMaximumNoDamageTicks(Integer.MAX_VALUE);
                    bat.setMetadata("NoDamage", new FixedMetadataValue(ThePit.getInstance(), true));
                    bats.add(bat);
                }
            }
        }
        return bats;
    }


    private static void moveBats(Bat mainBat, List<LivingEntity> bats, Vector direction) {
        mainBat.setVelocity(direction);

        bats.forEach(bat -> bat.setVelocity(direction));
    }

    private static double randomOffset() {
        return Math.random() * 1.4 - 0.8;
    }
}
