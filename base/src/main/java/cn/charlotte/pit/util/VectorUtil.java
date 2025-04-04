package cn.charlotte.pit.util;


import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * 2 * @Author: EmptyIrony
 * 3 * @Date: 2020/2/22 11:56
 * 4
 */
public class VectorUtil {
    private static final Random random = new Random();

    public static Item itemDrop(Player player, ItemStack itemStack) {
        return itemDrop(player, itemStack, 0.0, 0.4);
    }

    public static Item itemDrop(Player player, ItemStack itemStack, double bulletSpread, double radius) {
        Location location = player.getLocation().add(0.0D, 1.5D, 0.0D);
        Item item = player.getWorld().dropItem(location, itemStack);
        float yaw = (float) Math.toRadians(-player.getLocation().getYaw() - 90.0F);
        float pitch = (float) Math.toRadians(-player.getLocation().getPitch());
        double x;
        double y;
        double z;
        if (bulletSpread > 0.0D) {
            double[] spread = new double[]{1.0D, 1.0D, 1.0D};

            IntStream.range(0, 3).forEach((t) -> spread[t] = (random.nextDouble() - random.nextDouble()) * bulletSpread * 0.1D);
            x = MathHelper.cos(pitch) * MathHelper.cos(yaw) + spread[0];
            y = MathHelper.sin(pitch) + spread[1];
            z = -MathHelper.sin(yaw) * MathHelper.cos(pitch) + spread[2];
        } else {
            x = MathHelper.cos(pitch) * MathHelper.cos(yaw);
            y = MathHelper.sin(pitch);
            z = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        }

        Vector dirVel = new Vector(x, y, z);
        item.setVelocity(dirVel.normalize().multiply(radius));
        return item;
    }

    public static void entityPush(Entity entity, Location to, double velocity) {
        Location from = entity.getLocation();
        Vector vector = getPushVector(from, to, velocity);
        if (vector.getX() != 0 && vector.getY() != 0 && vector.getZ() != 0) {
            entity.setVelocity(vector);
        }
    }
    public static void entityPushBack(Entity entity,double power){
        Location location = entity.getLocation();
        Location location1 = calculateBackwardVector(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entityPush(entity, location1,power);
    }

    public static Location calculateBackwardVector(World world, double x, double y, double z, float yaw, float pitch) {
        // 计算方向向量的分量
        float dx = MathHelper.cos((float) Math.toRadians(pitch)) * MathHelper.sin((float) Math.toRadians(MathHelper.g(yaw + 90)));
        float dy = MathHelper.sin((float) Math.toRadians(30));
        float dz = MathHelper.cos((float) Math.toRadians(pitch)) * MathHelper.sin((float) Math.toRadians(MathHelper.g(yaw + 90)));

        // 归一化方向向量
        float magnitude = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= magnitude;
        dy /= magnitude;
        dz /= magnitude;

        // 计算向后退的向量
        double backwardX = (-dx) * 6.2;
        double backwardY = -dy * 1.24;
        double backwardZ = (-dz) * 6.28;

        return new Location(world,x + backwardX,y - backwardY,z + backwardZ,MathHelper.g(yaw+180F),-30);
    }
    public static Vector getPushVector(Location from, Location to, double velocity) {
        Vector test = to.clone().subtract(from).toVector();
        double elevation = test.getY();
        Double launchAngle = calculateLaunchAngle(from, to, velocity, elevation);
        double z = test.getZ();
        double x = test.getX();
        double distance = MathHelper.sqrt(x * x + z * z);
        if (distance != 0.0D) {
            if (launchAngle == null) {
                launchAngle = Math.atan((40.0D * elevation + Math.pow(velocity, 2.0D)) / (40.0D * elevation + 2.0D * Math.pow(velocity, 2.0D)));
            }
            double hangTime = calculateHangTime(launchAngle, velocity, elevation);
            test.setY(Math.tan(launchAngle) * distance);
            test = normalizeVector(test);
            Vector noise = Vector.getRandom();
            noise = noise.multiply(0.1D);
            test.add(noise);
            velocity = velocity + 1.188D * Math.pow(hangTime, 2.0D) + (random.nextDouble() - 0.8D) / 2.0D;
            test = test.multiply(velocity / 20.0D);
            return test;
        }
        return new Vector(0, 0, 0);
    }

    private static double calculateHangTime(double launchAngle, double v, double elev) {
        double a = v * Math.sin(launchAngle);
        double b = -2.0D * 20.0 * elev;
        return Math.pow(a, 2.0D) + b < 0.0D ? 0.0D : (a + Math.sqrt(Math.pow(a, 2.0D) + b)) / 20.0;
    }

    private static Vector normalizeVector(Vector victor) {
        double mag = MathHelper.sqrt(MathHelper.pow(victor.getX(), 2.0D) + MathHelper.pow(victor.getY(), 2.0D) + MathHelper.pow(victor.getZ(), 2.0D));
        return mag != 0.0D ? victor.multiply(1.0D / mag) : victor.multiply(0);
    }

    private static Double calculateLaunchAngle(Location from, Location to, double v, double elevation) {
        Vector vector = from.clone().subtract(to).toVector();
        double distance = MathHelper.sqrt(MathHelper.pow(vector.getX(), 2.0D) + MathHelper.pow(vector.getZ(), 2.0D));
        double v2 = MathHelper.pow(v, 2.0D);
        double v4 = MathHelper.pow(v, 4.0D);
        double check = 20.0 * (20.0 * MathHelper.pow(distance, 2.0D) + 2.0D * elevation * v2);
        return v4 < check ? null : Math.atan((v2 - MathHelper.sqrt(v4 - check)) / (20.0 * distance));
    }
}

