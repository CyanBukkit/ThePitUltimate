package net.mizukilab.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PlayerInv;
import cn.charlotte.pit.event.PitProfileLoadedEvent;
import net.mizukilab.pit.data.operator.PackedOperator;
import net.mizukilab.pit.data.operator.ProfileOperator;
import net.mizukilab.pit.util.PitProfileUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @Author: KleeLoveLife
 * @Date: 2025/4/12 10:54
 */
//@AutoRegister
public class DataListener implements Listener {

    public DataListener() {

    }


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PackedOperator orLoadOperator = ((ProfileOperator) ThePit.getInstance().getProfileOperator()).getOrLoadOperator(player);
        orLoadOperator.pendingUntilLoaded(prof -> {
            if (statusCheck(orLoadOperator)) return;
            //post init, when checked
            orLoadOperator.heartBeat();
            this.whenLoaded(prof);
        });
        event.setJoinMessage(null);
    }

    private static boolean statusCheck(PackedOperator orLoadOperator) {
        byte code = orLoadOperator.profile().code;
        if (code == -2) {
            orLoadOperator.fail(new Exception("Status equals to 2, kicking"));
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ((ProfileOperator) ThePit.getInstance().getProfileOperator())
                .operatorStrict(event.getPlayer()).ifPresent(profileOper -> {
                    PlayerProfile profile = profileOper.profile();
                    //synchronize
                    PlayerInv playerInv = PlayerInv.fromPlayerInventory(event.getPlayer().getInventory());
                    profile.disallow();
                    checkIllegalProfile(profile);
                    profile.setLogin(false); //我草泥马
                        //fire at post
                    profile.setInventoryUnsafe(playerInv).allow();
                    triggerDeath(event, profile);
                    //handle Death
                    profile.setBounty(0);
                });
    }

    private static void triggerDeath(PlayerQuitEvent event, PlayerProfile profile) {
        CombatListener instance = CombatListener.INSTANCE;
        if (instance != null && !profile.getCombatTimer().hasExpired()) {
            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> {
                instance.handlePlayerDeath(event.getPlayer(), null, false);
            });
        }
    }

    private static void checkIllegalProfile(PlayerProfile profile) {
        long time = System.currentTimeMillis();
        profile.setLastLogoutTime(time);

        long totalPlayedTime = profile.getTotalPlayedTime();
        profile.setTotalPlayedTime(totalPlayedTime + profile.getLastLogoutTime() - profile.getLastLoginTime());
        //reset if data have an error (old bug)
        long calculatedTime = time - profile.getRegisterTime();
        if (totalPlayedTime > calculatedTime) {
            profile.setTotalPlayedTime(0);
        }
    }



    public void whenLoaded(PlayerProfile load) {
        Player player = Bukkit.getPlayer(load.getPlayerUuid());
        updateLoginTime(load);

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

    private static void updateLoginTime(PlayerProfile load) {
        if (load.getRegisterTime() <= 1) {
            load.setRegisterTime(System.currentTimeMillis());
        }
        load.setLastLoginTime(System.currentTimeMillis());
    }
}
