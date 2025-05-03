package net.mizukilab.pit.map;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.events.trigger.type.IEpicEvent;
import cn.charlotte.pit.events.trigger.type.INormalEvent;
import com.google.common.base.Predicates;
import net.mizukilab.pit.config.ConfigManager;
import net.mizukilab.pit.config.PitWorldConfig;
import net.mizukilab.pit.util.cooldown.Cooldown;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class MapSelector {
    Cooldown switchCooldown
    ThePit thePit;
    ConfigManager config;
    public MapSelector(ThePit thePit){
        ConfigManager configManager = thePit.getConfigManager();
        Validate.notNull(configManager);
        config = configManager;
        this.thePit = thePit;
    }
    public void switchMap(){
        config.nextConfig();
        config.synchronizeLegacy();
        reset();
    }
    public void reset(){
        reInitNpc();
        reTeleportEntities();
        resetPlayerStatus();
    }
    public void switchMapIndexed(int index){
        config.setCursor(index);
        config.synchronizeLegacy();
        reset();
    }
    public void teleportIntoSpawn(Entity entity) {
        List<Location> spawnLocations = config.getSelectedWorldConfig()
                .getSpawnLocations();
        if(spawnLocations.isEmpty()){
            entity.sendMessage("未设置出生点");
            return;
        }
        Location location = spawnLocations
                .get(ThreadLocalRandom.current().nextInt(spawnLocations.size()));
        entity.teleport(location);
        if(entity instanceof Player){
            ((Player) entity).setBedSpawnLocation(location);
        }
    }
    public void reTeleportEntities() {
        Bukkit.getOnlinePlayers().forEach(this::teleportIntoSpawn);
    }
    public void reInitNpc(){
        thePit.getNpcFactory().reload();
    }
    public void resetPlayerStatus(){
        thePit.getProfileOperator().forEach(i -> {
            if (i.isLoaded()) {
                PlayerProfile profile = i.profile();
                profile.getCombatTimer().fastExpired();
                profile.deActiveMegaSteak();
            }
        });
    }
    public int getRemainTime(){
        return (int)(switchCooldown.getRemaining()/1000L);
    }
    public void tick(){
        if(switchCooldown.hasExpired()) {
            EventFactory eventFactory = ThePit.getInstance().getEventFactory();
            IEpicEvent activeEpicEvent = eventFactory.getActiveEpicEvent();
            if (activeEpicEvent != null) {
                eventFactory.inactiveEvent(activeEpicEvent);
                return;
            }
            INormalEvent normalEvent = eventFactory.getActiveNormalEvent();
            if(normalEvent != null){
                eventFactory.safeInactiveEvent(normalEvent);
                return;
            }
            switchMap();
            switchCooldown.reset();
            Bukkit.broadcastMessage()
        }
    }


}
