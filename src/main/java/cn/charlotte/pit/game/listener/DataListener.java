package cn.charlotte.pit.game.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.event.PitProfileLoadedEvent;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.runnable.ClearRunnable;
import cn.charlotte.pit.runnable.RebootRunnable;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.TaskUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.level.LevelUtil;
import cn.charlotte.pit.util.rank.RankUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 22:27
 */
@AutoRegister
public class DataListener implements Listener {
    private static final Random random = new Random();
    private final ExecutorService executor;

    public DataListener() {
        this.executor = new ScheduledThreadPoolExecutor(12);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        executor.execute(() -> {
            System.out.println("get lock...");
            final RReadWriteLock lock = ThePit.getInstance()
                    .getRedisClient()
                    .getLock("pit_lock-" + event.getPlayer().getUniqueId().toString());
            System.out.println("read lock...");
            final RLock readLock = lock.readLock();

            System.out.println("lock...");
            readLock.lock(5, TimeUnit.SECONDS);

            System.out.println("create Player Profile");
            String playerName = event.getPlayer().getName();
            /*if (playerName.equalsIgnoreCase("China_BlueSky")) {
                return;
            }*/
            PlayerProfile profile = new PlayerProfile(event.getPlayer().getUniqueId(), playerName);

            System.out.println("load profile...");
            PlayerProfile load = profile.load();
            System.out.println("cache profile...");
            PlayerProfile.getCacheProfile().put(event.getPlayer().getUniqueId(), load);

            System.out.println("log time...");
            if (load.getRegisterTime() <= 1) {
                load.setRegisterTime(System.currentTimeMillis());
            }
            load.setLastLoginTime(System.currentTimeMillis());

            System.out.println("unlock...");
            if (readLock.isLocked()) {
                readLock.unlock();
            }

            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                new PitProfileLoadedEvent(PlayerProfile.getPlayerProfileByUuid(event.getPlayer().getUniqueId()), event.getPlayer()).callEvent();
                TaskUtil.taskLater(profile::updateShow, 2);
            });

            if (event.getPlayer() != null && event.getPlayer().isOnline()) {
                FixedRewardData.Companion.sendMail(load, event.getPlayer());
            }
        });

        event.setJoinMessage(null);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        if (!PlayerUtil.isStaffSpectating(event.getPlayer()) && PlayerProfile.getCacheProfile().containsKey(event.getPlayer().getUniqueId())) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(event.getPlayer().getUniqueId());

            if (profile.isScreenShare()) {
                CC.boardCastWithPermission("&4&l查端时退出! &7玩家 " + LevelUtil.getLevelTagWithRoman(profile.getPrestige(), profile.getLevel()) + " " + RankUtil.getPlayerRealColoredName(event.getPlayer().getUniqueId()) + " &7在查端时退出了游戏!", PlayerUtil.getStaffPermission());
            }

            if (!profile.isTempInvUsing()) {
                profile.setInventory(PlayerInv.fromPlayerInventory(event.getPlayer().getInventory()));
            }
            profile.setLastLogoutTime(System.currentTimeMillis());

            profile.setTotalPlayedTime(profile.getTotalPlayedTime() + profile.getLastLogoutTime() - profile.getLastLoginTime());
            //reset if data have an error (old bug)
            if (profile.getTotalPlayedTime() > System.currentTimeMillis() - profile.getRegisterTime()) {
                profile.setTotalPlayedTime(0);
            }

            profile.setLogin(false);
            this.executor.execute(() -> {
                profile.save();
                PlayerProfile.getCacheProfile().remove(event.getPlayer().getUniqueId());
            });
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        RebootRunnable.safeShutdown = true;
        if (e.getPlugin() instanceof ThePit) {
            synchronized (Bukkit.getOnlinePlayers()) {
                CC.boardCast("&6&l公告! &7正在执行关闭服务器...");

                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                        if (profile.isLoaded()) {
                            profile.setInventory(InventoryUtil.playerInventoryFromPlayer(player));
                            profile.save();
                            if (!player.hasPermission("thepit.admin")) {
                                CC.boardCast("&6&l公告! &7正在保存 " + player.getDisplayName() + " 玩家的数据...");
                            } else {
                                player.sendMessage(CC.translate("&6&l公告! &c正在保存你的玩家数据..."));
                            }
                        }
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                }
                ClearRunnable.getClearRunnable().removeBlocks(true);
                CC.boardCast("&6&l公告! &7正在关闭服务器...");
            }
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(((Player) arrow.getShooter()).getUniqueId());
                profile.setShootAttack(profile.getShootAttack() + 1);
            }
        } else if (event.getEntity() instanceof FishHook) {
            FishHook hook = (FishHook) event.getEntity();
            if (hook.getShooter() instanceof Player) {
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(((Player) hook.getShooter()).getUniqueId());
                profile.setRodUsed(profile.getRodUsed() + 1);
            }
        }
    }

}
