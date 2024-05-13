package cn.charlotte.pit.events.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.events.IEvent;
import cn.charlotte.pit.events.INormalEvent;
import cn.charlotte.pit.events.IScoreBoardInsert;
import cn.charlotte.pit.item.type.mythic.MythicBowItem;
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem;
import cn.charlotte.pit.item.type.mythic.MythicSwordItem;
import cn.charlotte.pit.menu.pack.PackageMenu;
import cn.charlotte.pit.menu.pack.SmallPackageMenu;
import cn.charlotte.pit.util.DirectionUtil;
import cn.charlotte.pit.util.ServerAddress;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.hologram.Hologram;
import cn.charlotte.pit.util.hologram.HologramAPI;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.thread.ThreadHelper;
import cn.charlotte.pit.util.time.TimeUtil;
import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/30 16:41
 */

public class SmallCarePackageEvent implements INormalEvent, IEvent, Listener, IScoreBoardInsert {
    @Getter
    private static Location chest;
    @Getter
    private static ChestData chestData;

    private static BukkitRunnable runnable;

    private static Cooldown endTimer;
    @Getter
    private final int range = 233;


    @Getter
    private final SmallPackageMenu packageMenu;

    public SmallCarePackageEvent() {
        packageMenu = new SmallPackageMenu(this, range);
    }

    @Override
    public String getEventInternalName() {
        return "small_care_package";
    }

    @Override
    public String getEventName() {
        return "小型空投";
    }


    @Override
    public int requireOnline() {
        if (ThePit.serverAddress == ServerAddress.AC_CN) {
            return 10;
        }
        if (DateUtil.isAM(DateUtil.date())) {
            return 20;
        }
        return 25;
    }

    private Location generateLocation() {
        return RandomUtil.generateRandomLocation();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if ("small_care_package".equals(ThePit.getInstance().getEventFactory().getActiveNormalEventName())) {
            chestData.getFirstHologram().spawn(Collections.singletonList(event.getPlayer()));
            chestData.getSecondHologram().spawn(Collections.singletonList(event.getPlayer()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ("small_care_package".equals(ThePit.getInstance().getEventFactory().getActiveNormalEventName())) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST && event.getClickedBlock().getLocation().equals(chest)) {
                event.setCancelled(true);
                if (chestData == null) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), () -> {
                    if (chestData.getNum() <= 0) {
                        click(event.getPlayer(), chestData);
                        return;
                    }
                    if (chestData.isLeft()) {
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            click(event.getPlayer(), chestData);
                        }
                    } else {
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            click(event.getPlayer(), chestData);
                        }
                    }
                });
            }
        }
    }

    private void click(Player player, ChestData data) {
        Hologram firstHologram = data.getFirstHologram();
        if (data.getNum() <= 0) {
            sync(() -> {
                packageMenu.openMenu(player);
            });
            if (firstHologram.isSpawned()) {
                firstHologram.deSpawn();
            }
            data.getSecondHologram().setText(CC.translate("&a&l" + (data.isLeft() ? "左键" : "右键") + "开启"));
            return;
        }
        data.setNum(data.getNum() - 1);

        chest.getWorld().playSound(chest, Sound.ZOMBIE_WOODBREAK, 0.5F, 1.5F);

        data.setLeft(!data.isLeft());
        if (data.getNum() > 50) {
            firstHologram.setText(CC.translate("&a&l" + data.getNum()));
            data.getSecondHologram().setText(CC.translate("&a&l" + (data.isLeft() ? "左键" : "右键") + "点击"));
        } else if (data.getNum() > 35) {
            firstHologram.setText(CC.translate("&e&l" + data.getNum()));
            data.getSecondHologram().setText(CC.translate("&e&l" + (data.isLeft() ? "左键" : "右键") + "点击"));
        } else {
            firstHologram.setText(CC.translate("&c&l" + data.getNum()));
            data.getSecondHologram().setText(CC.translate("&c&l" + (data.isLeft() ? "左键" : "右键") + "点击"));
        }
    }

    @Override
    public void onActive() {
        if (!Bukkit.isPrimaryThread()) {
            ThreadHelper.Sync(this::onActive);
            return;
        }
        Bukkit.getPluginManager()
                .registerEvents(this, ThePit.getInstance());
        Location location = this.generateLocation();
        while (location.getY() >= ThePit.getInstance().getPitConfig().getArenaHighestY()) {
            location = this.generateLocation();
        }

        location.getWorld().strikeLightningEffect(location);
        CC.boardCast("&e&l空投! &7一个新的小型空投已在地图降落!打开可以获得神话物品,声望等稀有物资!");
        location.getBlock().setType(Material.CHEST);
        chest = location;


        Map<Integer, ItemStack> items = packageMenu.getItems();

//        for (int i = 0; i < RandomUtil.random.nextInt(3) + 3; i++) {
//            int nextInt = RandomUtil.random.nextInt(27);
//            while (items.get(nextInt) != null && items.get(nextInt).getType() != Material.AIR) {
//                nextInt = RandomUtil.random.nextInt(27);
//            }
//            items.put(nextInt, new ItemBuilder(Material.GOLD_BLOCK).name("&e+1声望").internalName("renown_reward").shiny().build());
//        }

        for (int i = 0; i < 27; i++) {
            if (items.get(i) == null) {
                items.put(i, RandomUtil.helpMeToChooseOne(new ItemBuilder(Material.EXP_BOTTLE).name("&b+" + range + "经验值").internalName("xp_reward").shiny().build(), new ItemBuilder(Material.GOLD_INGOT).name("&6+" + range + "硬币").internalName("coin_reward").shiny().build()));
            }
        }
        if (RandomUtil.random.nextInt(100) <= 2) {
            for (int i = 0; i < RandomUtil.random.nextInt(2) + 1; i++) {
                items.put(RandomUtil.random.nextInt(27), RandomUtil.helpMeToChooseOne(
                        new MythicLeggingsItem().toItemStack(),
                        new MythicSwordItem().toItemStack(),
                        new MythicBowItem().toItemStack(),
                        new MythicSwordItem().toItemStack(),
                        new MythicBowItem().toItemStack()
                ));
            }
        }
        for (int i = 0; i < RandomUtil.random.nextInt(3) + 1; i++) {
            if (RandomUtil.random.nextInt(100) <= 20) {
                ItemStack itemStack = new ItemBuilder(Material.GOLD_BLOCK).name("&e+1声望").internalName("renown_reward").shiny().build();
                items.put(RandomUtil.random.nextInt(27), itemStack);
            }
        }

        chestData = new ChestData();
        Location holo = location;
        chestData.setFirstHologram(HologramAPI.createHologram(holo.clone().add(1, 2.4, 0.5), CC.translate("&a&l100")));
        chestData.setSecondHologram(HologramAPI.createHologram(holo.clone().add(1, 1.2, 0.5), CC.translate("&a&l左键点击")));

        chestData.getFirstHologram().spawn();
        chestData.getSecondHologram().spawn();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                ThePit.getInstance()
                        .getEventFactory()
                        .inactiveEvent(ThePit.getInstance().getEventFactory().getActiveNormalEvent());
            }
        };
        runnable.runTaskLaterAsynchronously(ThePit.getInstance(), 20 * 60 * 5);
        endTimer = new Cooldown(5, TimeUnit.MINUTES);
    }

    @Override
    public void onInactive() {
        try {
            HandlerList.unregisterAll(this);
            chestData.getSecondHologram().deSpawn();
            chestData.getFirstHologram().deSpawn();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chestData = null;
            packageMenu.getItems().clear();
            sync(() -> {
                chest.getBlock().setType(Material.AIR);
                chest = null;
            });
            runnable.cancel();
            runnable = null;
        }
    }

    @Override
    public List<String> insert(Player player) {
        List<String> lines = new ArrayList<>(2);
        if (chest == null) {
            return lines;
        }
        int distance = (int) player.getLocation().distance(chest);

        if (endTimer.getRemaining() > 2 * 60 * 1000L) {
            lines.add("&f剩余时间: &a" + TimeUtil.millisToTimer(endTimer.getRemaining()));
        } else if (endTimer.getRemaining() >= 60 * 1000L) {
            lines.add("&f剩余时间: &e" + TimeUtil.millisToTimer(endTimer.getRemaining()));
        } else {
            lines.add("&f剩余时间: &c" + TimeUtil.millisToTimer(endTimer.getRemaining()));
        }

        if (!chestData.getRewarded().contains(player.getUniqueId())) {
            if (chestData.getNum() == 100) {
                lines.add("&f追踪: &c&l? &7(&f" + distance + "m&7)");
                return lines;
            }
            String targetDirection = DirectionUtil.getTargetDirection(player, chest);
            if (chestData.getNum() > 0) {
                lines.add("&f追踪: &b&l" + targetDirection + " &7(&f" + distance + "m&7)");
            } else {
                lines.add("&f追踪: &d&l" + targetDirection + " &7(&f" + distance + "m&7)");
            }
        }

        return lines;
    }

    @Data
    public static class ChestData {
        private boolean left = true;
        private int num = 100;
        private Hologram firstHologram;
        private Hologram secondHologram;
        private List<UUID> rewarded = new ArrayList<>();
    }
}
