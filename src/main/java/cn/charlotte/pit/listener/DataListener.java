package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.operator.PackedOperator;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.event.PitProfileLoadedEvent;
import cn.charlotte.pit.runnable.BountyRunnable;
import cn.charlotte.pit.util.PitProfileUpdater;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.rank.RankUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 22:27
 */
//@AutoRegister
public class DataListener implements Listener {
    private final ScheduledExecutorService executor; //所以说为什么要移动到这里???????


    public final Set<UUID> busyMap = new ObjectOpenHashSet<>(); // do it static

    public DataListener() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        JedisPool jedisPool = ThePit.getInstance().getJedis(); //coming soon
//        if (jedisPool != null) {
//            executor.scheduleAtFixedRate(() -> {
//                String databaseName = ThePit.getInstance().getPitConfig().getDatabaseName();
//
//                try(Jedis jedis = jedisPool.getResource()) {
//                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        if (PlayerProfile.LOADING_MAP.containsKey(player.getUniqueId())) {
//                            continue;
//                        }
//
//                        jedis.expire(
//                                "THEPIT_" + databaseName + "_" + player.getUniqueId().toString(),
//                                15
//                        );
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }, 10L, 10L, TimeUnit.SECONDS);
//        }
    }



    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
//        if(PlayerProfile.LOADING_MAP.containsKey(event.getPlayer().getUniqueId())){
//            event.getPlayer().kickPlayer("您的档案正在加载中呢, 数据库访问很慢, 请耐心等待 ;w;");
//        }
//        if(PlayerProfile.SAVING_MAP.containsKey(event.getPlayer().getUniqueId())){
//            event.getPlayer().kickPlayer("您的档案正在保存中呢, 数据库访问很慢, 请耐心等待 ;w;");
//        }
//        if(this.busyMap.contains(event.getPlayer().getUniqueId())){
//            event.getPlayer().kickPlayer("服务器很忙哦, 正在处理您的两个保存任务!!!");
//        }
//        PlayerUtil.clearPlayer(player, true, true);
//
//        BukkitRunnable runnable = new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (!player.isOnline()) {
//                    PlayerProfile.LOADING_MAP.remove(player.getUniqueId()); //GC
//                    cancel();
//                    return;
//                }
//
//                if (canLoad(player)) {
//                    cancel();
//                    load(player);
//                }
//
//                //continue waiting
//            }
//        };

//        runnable.runTaskTimerAsynchronously(ThePit.getInstance(), 1L, 1L);
//
//        PlayerProfile.LOADING_MAP.put(player.getUniqueId(), runnable);
        ThePit.getInstance().getProfileOperator().getOrLoadOperator(player).pendingUntilLoaded(this::whenLoaded);
        event.setJoinMessage(null);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ThePit.getInstance().getProfileOperator()
                .operatorStrict(event.getPlayer()).ifPresent(profileOper -> {
                    PlayerProfile profile = profileOper.profile();
                if (!PlayerUtil.isStaffSpectating(event.getPlayer())) {
                        if (profile.isScreenShare()) {
                            CC.boardCastWithPermission("&4&l查端时退出! &7玩家 " + LevelUtil.getLevelTagWithRoman(profile.getPrestige(), profile.getLevel()) + " " + RankUtil.getPlayerRealColoredName(event.getPlayer().getUniqueId() + " &7在查端时退出了游戏!"), PlayerUtil.getStaffPermission());
                        }

                      profile.setLastLogoutTime(System.currentTimeMillis());

                        profile.setTotalPlayedTime(profile.getTotalPlayedTime() + profile.getLastLogoutTime() - profile.getLastLoginTime());
                        //reset if data have an error (old bug)
                        if (profile.getTotalPlayedTime() > System.currentTimeMillis() - profile.getRegisterTime()) {
                            profile.setTotalPlayedTime(0);
                        }

                        profile.setLogin(false); //我草泥马
                        profileOper.pending(i -> {
                                    profile.setInventoryUnsafe(PlayerInv.fromPlayerInventory(event.getPlayer().getInventory()));
                                    //unsafe exit
                                });
                        CombatListener instance = CombatListener.INSTANCE;
                        if (instance != null && !profile.getCombatTimer().hasExpired()) {
                            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                                instance.handlePlayerDeath(event.getPlayer(), null, false);
                            });
                        }
                        //handle Death
                        profile.setBounty(0);
                    }
                });
        //useless, for automatic only
//        final BountyRunnable.AnimationData animationData = BountyRunnable.getAnimationDataMap().get(event.getPlayer().getUniqueId());
//        if (animationData != null) {
//            for (BountyRunnable.HologramDisplay hologram : animationData.getHolograms()) {
//                hologram.getHologram().deSpawn();
//            }
//        }
//        BountyRunnable.getAnimationDataMap().remove(event.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(((Player) arrow.getShooter()).getUniqueId());
                profile.setShootAttack(profile.getShootAttack() + 1);
            }
        } else if (event.getEntity() instanceof FishHook hook) {
            if (hook.getShooter() instanceof Player) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(((Player) hook.getShooter()).getUniqueId());
                profile.setRodUsed(profile.getRodUsed() + 1);
            }
        }
    }

    public boolean canLoad(Player player) {
        String databaseName = ThePit.getInstance().getPitConfig().getDatabaseName();
        JedisPool jedisPool = ThePit.getInstance().getJedis();
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                return "OK".equals(jedis.set(
                        "THEPIT_" + databaseName + "_" + player.getUniqueId().toString(),
                        "locked", "NX", "EX", 30L
                ));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public void whenLoaded(PlayerProfile load) {
            Player player = Bukkit.getPlayer(load.getPlayerUuid());
            if (load.getRegisterTime() <= 1) {
                load.setRegisterTime(System.currentTimeMillis());
            }
            load.setLastLoginTime(System.currentTimeMillis());

            if (load.getProfileFormatVersion() == 0) {
                PitProfileUpdater.updateVersion0(load);
            }
            if (player != null && player.isOnline()) {
                load.setLogin(true);
                Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                    new PitProfileLoadedEvent(load).callEvent();
                });
                FixedRewardData.Companion.sendMail(load, player);
            }

    }
}
