package net.mizukilab.pit.enchantment.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.mizukilab.pit.config.PitWorldConfig;
import net.mizukilab.pit.enchantment.menu.MythicWellMenu;
import net.mizukilab.pit.item.MythicColor;
import net.mizukilab.pit.util.ParticleUtil;
import net.mizukilab.pit.util.inventory.InventoryUtil;
import net.mizukilab.pit.util.item.ItemUtil;
import net.mizukilab.pit.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/11 21:10
 */
@Getter
public class AnimationRunnable extends BukkitRunnable {

    private final Map<UUID, AnimationData> animations = new ConcurrentHashMap<>();
    private final List<Location> animationLocations;

    public AnimationRunnable() {
        final PitWorldConfig pitWorldConfig = ThePit.getInstance().getPitConfig();
        final Location loc = pitWorldConfig.getEnchantLocation();
        final Location center;
        if (loc == null) {
            center = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        } else {
            center = loc.clone().add(0.0, -1.0, 0.0);
        }
        this.animationLocations = Arrays.asList(
                center.clone().add(-1, 0, 0),   // 19: 左中
                center.clone().add(-1, 0, -1),  // 10: 左上
                center.clone().add(0, 0, -1),   // 11: 中上
                center.clone().add(1, 0, -1),   // 12: 右上
                center.clone().add(1, 0, 0),    // 21: 右中
                center.clone().add(1, 0, 1),    // 30: 右下
                center.clone().add(0, 0, 1),    // 29: 中下
                center.clone().add(-1, 0, 1)    // 28: 左下
        );


        this.runTaskTimerAsynchronously(ThePit.getInstance(), 20, 1);

    }

    @Override
    public void run() {
        synchronized (animations) {
            final Object2ObjectOpenHashMap<UUID, AnimationData> removeMap = new Object2ObjectOpenHashMap<>(animations);
            removeMap.forEach((uuid, animationData) -> {
                if (!animationData.getPlayer().isOnline() ||
                        !(Menu.currentlyOpenedMenus.get(animationData.getPlayer().getName()) instanceof MythicWellMenu)) {
                    animations.remove(uuid);
                }
            });

            for (AnimationData data : animations.values()) {
                data.animationGlobalTick++;
                Menu menu = Menu.currentlyOpenedMenus.get(data.getPlayer().getName());

                if (data.isFinished()) {
                    Player player = data.getPlayer();
                    PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                    String mythicColor = ItemUtil.getItemStringData(InventoryUtil.deserializeItemStack(profile.getEnchantingItem()), "mythic_color");
                    MythicColor foundColor = null;
                    for (MythicColor color : MythicColor.values()) {
                        if (color.getInternalName().equals(mythicColor)) {
                            foundColor = color;
                            break;
                        }
                    }
                    if (foundColor == null) {
                        continue;
                    }

                    for (Location location : animationLocations) {
                        player.sendBlockChange(location, Material.STAINED_GLASS, foundColor.getColorByte());
                    }
                    continue;
                }

                if (data.isStartEnchanting()) {
                    Player player = data.getPlayer();
                    PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                    String mythic_color = ItemUtil.getItemStringData(InventoryUtil.deserializeItemStack(profile.getEnchantingItem()), "mythic_color");
                    MythicColor foundColor = null;
                    for (MythicColor color : MythicColor.values()) {
                        if (color.getInternalName().equals(mythic_color)) {
                            foundColor = color;
                            break;
                        }
                    }
                    if (foundColor == null) {
                        continue;
                    }

                    // 增强的附魔动画序列（加快速度）
                    handleEnchantingAnimation(data, player, foundColor, menu);

                } else {
                    if (data.animationTick > 28) {  // 8个位置 * 4tick = 32，减1为31，但用28更合适
                        data.animationTick = 0;
                    }

                    if (data.animationTick % 4 != 0) {  // 改为每4tick切换一次
                        data.animationTick++;
                        continue;
                    }

                    int realTick = data.animationTick / 4;  // 对应调整计算


                    if (Menu.currentlyOpenedMenus.get(data.getPlayer().getName()) instanceof MythicWellMenu) {
                        menu.openMenu(data.getPlayer());
                    }

                    Location location = animationLocations.get(realTick);

                    if (data.animationTick == 0) {
                        data.player.sendBlockChange(animationLocations.get(7), Material.STAINED_GLASS, (byte) 0);
                    } else {
                        data.player.sendBlockChange(animationLocations.get(realTick - 1), Material.STAINED_GLASS, (byte) 0);
                    }
                    data.player.sendBlockChange(location, Material.STAINED_GLASS, data.color);
                }
                data.animationTick++;
            }
        }
    }

    /**
     * 处理附魔过程中的炫酷动画（调慢速度）
     */
    private void handleEnchantingAnimation(AnimationData data, Player player, MythicColor foundColor, Menu menu) {
        int tick = data.animationTick;

        // 第一阶段：准备阶段 (0-11 tick) - 延长时间
        if (tick <= 11) {
            if (tick == 0 || tick == 3 || tick == 6 || tick == 9) {
                // 所有方块同时亮起
                for (Location location : animationLocations) {
                    player.sendBlockChange(location, Material.STAINED_GLASS, foundColor.getColorByte());
                }
                // 播放准备音效
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1.5F + (tick / 3) * 0.1F);

                // 添加粒子效果
                spawnParticles(player, "SPELL_MOB", foundColor);

            } else if (tick == 1 || tick == 2 || tick == 4 || tick == 5 || tick == 7 || tick == 8 || tick == 10 || tick == 11) {
                // 方块熄灭
                for (Location location : animationLocations) {
                    player.sendBlockChange(location, Material.STAINED_GLASS, (byte) 0);
                }
                if (tick % 3 == 1) {  // 只在特定tick播放音效
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.8F);
                }
            }
        }
        // 第二阶段：旋转能量聚集 (12-35 tick) - 大幅延长时间
        else if (tick <= 35) {
            int rotationIndex = ((tick - 12) / 3) % 8;  // 每3tick切换一个位置，更慢

            // 清除所有方块
            for (Location location : animationLocations) {
                player.sendBlockChange(location, Material.STAINED_GLASS, (byte) 0);
            }

            // 在当前位置显示方块
            player.sendBlockChange(animationLocations.get(rotationIndex), Material.STAINED_GLASS, foundColor.getColorByte());

            // 渐强音效
            if ((tick - 12) % 3 == 0) {  // 每3tick播放一次音效
                float pitch = 0.5F + ((tick - 12) / 3) * 0.15F;
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, pitch);

                // 螺旋粒子效果
                spawnSpiralParticles(player, foundColor);
            }
        }

        else if (tick <= 59) {
            int burstIndex = ((tick - 36) / 3) % 8;

            for (int i = 0; i <= burstIndex; i++) {
                player.sendBlockChange(animationLocations.get(i), Material.STAINED_GLASS, foundColor.getColorByte());
            }

            if ((tick - 36) % 3 == 0) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1.0F + burstIndex * 0.1F);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 2.0F);

                // 魔法粒子效果（替代爆炸）
                spawnMagicParticles(player, foundColor);
            }

            // 在最后一个tick播放完成效果
            if (tick == 59) {
                // 播放成功音效
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1.2F);
                player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 1.5F);

                // 最终魔法粒子效果
                spawnFinalMagicParticles(player, foundColor);
            }
        }
        // 第四阶段：完成 (60+ tick)
        else {
            data.finished = true;

            // 最终完成效果
            for (Location location : animationLocations) {
                player.sendBlockChange(location, Material.STAINED_GLASS, foundColor.getColorByte());
            }

            // 成功音效
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1.0F);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2.0F);

            // 最终粒子效果
            spawnFinalBurstParticles(player, foundColor);

            // 确保菜单正确刷新，避免被关闭
            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                if (player.isOnline() && Menu.currentlyOpenedMenus.get(player.getName()) instanceof MythicWellMenu) {
                    menu.openMenu(player);
                }
            });
            return;
        }

        Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
            if (player.isOnline() && Menu.currentlyOpenedMenus.get(player.getName()) instanceof MythicWellMenu) {
                menu.openMenu(player);
            }
        });
    }

    /**
     * 生成基础粒子效果
     */
    private void spawnParticles(Player player, String particleType, MythicColor color) {
        Location center = player.getLocation().add(0, 1, 0);
        int[] rgb = ParticleUtil.getColorFromMythicColor(color.getInternalName());

        // 创建魔法阵效果
        ParticleUtil.createMagicCircleParticles(player, center, 1.5, rgb[0], rgb[1], rgb[2]);

        // 播放额外的视觉音效
        player.playSound(center, Sound.FIZZ, 0.5F, 1.5F);
    }

    /**
     * 生成螺旋粒子效果
     */
    private void spawnSpiralParticles(Player player, MythicColor color) {
        Location center = player.getLocation().add(0, 1, 0);
        int[] rgb = ParticleUtil.getColorFromMythicColor(color.getInternalName());

        // 螺旋上升的粒子效果
        ParticleUtil.createSpiralParticles(player, center, rgb[0], rgb[1], rgb[2]);
        player.playSound(player.getLocation(), Sound.PORTAL, 0.3F, 2.0F);
    }

    /**
     * 生成魔法粒子效果（替代爆炸）
     */
    private void spawnMagicParticles(Player player, MythicColor color) {
        Location center = player.getLocation().add(0, 1, 0);
        int[] rgb = ParticleUtil.getColorFromMythicColor(color.getInternalName());

        // 魔法能量聚集效果
        ParticleUtil.createMagicParticles(player, center, rgb[0], rgb[1], rgb[2]);
        player.playSound(player.getLocation(), Sound.PORTAL, 0.3F, 1.5F);
    }

    /**
     * 生成最终魔法粒子效果
     */
    private void spawnFinalMagicParticles(Player player, MythicColor color) {
        Location center = player.getLocation().add(0, 1, 0);
        int[] rgb = ParticleUtil.getColorFromMythicColor(color.getInternalName());

        // 最终魔法完成效果
        ParticleUtil.createSpiralParticles(player, center, rgb[0], rgb[1], rgb[2]);
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0F, 1.5F);
    }


    private void spawnFinalBurstParticles(Player player, MythicColor color) {
        Location center = player.getLocation().add(0, 1, 0);
        int[] rgb = ParticleUtil.getColorFromMythicColor(color.getInternalName());
        ParticleUtil.createFinalBurstParticles(player, center, rgb[0], rgb[1], rgb[2]);
        player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1.0F, 1.0F);
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0F, 1.5F);
    }

    public void sendReset(Player player) {
        for (Location location : animationLocations) {
            Block block = location.getBlock();
            player.sendBlockChange(location, block.getType(), block.getData());
        }
    }

    public void sendStart(Player player) {
        for (Location location : animationLocations) {
            player.sendBlockChange(location, Material.STAINED_GLASS, (byte) 0);
        }
    }

    @Getter
    @Setter
    public static class AnimationData {

        private final Player player;
        private int animationGlobalTick = 0;
        private int animationTick = 0;
        private byte color = (byte) 6;
        private boolean startEnchanting = false;
        private boolean finished = false;

        public void reset() {
            animationTick = 0;
            startEnchanting = false;
            finished = false;
        }

        public AnimationData(Player player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnimationData data = (AnimationData) o;
            return Objects.equals(player.getUniqueId(), data.player.getUniqueId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(player.getUniqueId());
        }
    }
}
