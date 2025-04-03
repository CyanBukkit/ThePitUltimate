package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.operator.PackedOperator;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.event.PitProfileLoadedEvent;
import cn.charlotte.pit.util.PitProfileUpdater;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
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
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 22:27
 */
//@AutoRegister
public class DataListener implements Listener {

    public DataListener() {
//        JedisPool jedisPool = ThePit.getInstance().getJedis(); //coming soon
//        if (jedisPool != null) {
//            Bukkit.getScheduler().runTaskTimerAsynchronously(ThePit.getInstance(), () -> {
//                String databaseName = ThePit.getInstance().getPitConfig().getDatabaseName();
//
//                try(Jedis jedis = jedisPool.getResource()) {
//                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        PackedOperator operator = ThePit.getInstance().getProfileOperator().getOperator(player);
//                        if(operator == null) {
//
//                            jedis.expire(
//                                    "THEPIT_" + databaseName + "_" + player.getUniqueId().toString(),
//                                    15
//                            );
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }, 10L, 10L);
//        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PackedOperator orLoadOperator = ThePit.getInstance().getProfileOperator().getOrLoadOperator(player);
        orLoadOperator.ifLoaded(() -> {
            byte code = orLoadOperator.profile().code;

            if (code == -2) {
                event.getPlayer().kickPlayer("Saving your latest profile, please wait :)");
            }
        });
        orLoadOperator.pendingUntilLoaded(prof -> {
            orLoadOperator.heartBeat();
            this.whenLoaded(prof);
        });
        event.setJoinMessage(null);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInv playerInv = PlayerInv.fromPlayerInventory(event.getPlayer().getInventory());
        event.setQuitMessage(null);
        ThePit.getInstance().getProfileOperator()
                .operatorStrict(event.getPlayer()).ifPresent(profileOper -> {
                    PlayerProfile profile = profileOper.profile();
                    profile.disallow();
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

                        Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> { //keep synchronize
                                    profile.disallowUnsafe()
                                            .setInventoryUnsafe(playerInv).allow();
                                    profileOper.pending(i -> {
                                        //unsafe exit

                                        profile.disallowUnsafe()
                                                .setInventoryUnsafe(playerInv).allow();
                                    });
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
