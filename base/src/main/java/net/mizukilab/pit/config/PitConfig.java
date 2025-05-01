package net.mizukilab.pit.config;

import net.mizukilab.pit.util.configuration.Configuration;
import net.mizukilab.pit.util.configuration.annotations.ConfigData;
import net.mizukilab.pit.util.configuration.annotations.ConfigSerializer;
import net.mizukilab.pit.util.configuration.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: EmptyIrony
 * @Date: 2020/12/30 22:02
 */
public class PitConfig extends Configuration {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int maxLevel = 130; //Mirror
    private boolean tradeEnable = true;
    private boolean PVPEnable = true;
    public List<String> animationForEpicEvent;

    public int periodForEpicEvent;
    @ConfigData(
            path = "validate.state"
    )
    private String state;
    @ConfigData(
            path = "validate.token"
    )
    private String token;
    @ConfigData(
            path = "service.mongodb.ip"
    )
    private String mongoDBAddress;
    @ConfigData(
            path = "service.mongodb.port"
    )
    private int mongoDBPort;

    @ConfigData(
            path = "service.mongodb.database"
    )
    private String databaseName;

    @ConfigData(
            path = "service.mongodb.user"
    )
    private String mongoUser;

    @ConfigData(
            path = "service.mongodb.password"
    )
    private String mongoPassword;

    @ConfigData(
            path = "service.redis.enable"
    )
    private boolean redisEnable;

    @ConfigData(
            path = "service.redis.ip"
    )
    private String redisAddress;
    @ConfigData(
            path = "service.redis.port"
    )
    private int redisPort;

    @ConfigData(
            path = "service.redis.password"
    )
    private String redisPassword;

    @ConfigData(
            path = "arenaHighestY"
    )
    private int arenaHighestY;
    @ConfigData(
            path = "loc.spawn"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> spawnLocations = new ArrayList<>();
    @ConfigData(
            path = "loc.npc.shop"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location shopNpcLocation;
    @ConfigData(
            path = "loc.npc.quest"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location questNpcLocation;
    @ConfigData(
            path = "loc.npc.perk"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location perkNpcLocation;

    @ConfigData(path = "server-name")
    private String serverName = "&e天坑乱斗";

    @ConfigData(
            path = "loc.npc.prestige"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location prestigeNpcLocation;
    @ConfigData(
            path = "loc.npc.status"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location statusNpcLocation;
    @ConfigData(
            path = "loc.npc.keeper"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location keeperNpcLocation;
    @ConfigData(
            path = "loc.npc.mail"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location mailNpcLocation;
    @ConfigData(
            path = "loc.npc.genesis_demon"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location genesisDemonNpcLocation;
    @ConfigData(
            path = "loc.npc.genesis_angel"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location genesisAngelNpcLocation;

    @ConfigData(
            path = "loc.npc.sewers_fish"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location sewersFishNpcLocation;

    @ConfigData(
            path = "loc.hologram.spawn"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location hologramLocation;
    @ConfigData(
            path = "loc.hologram.leaderBoard"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location leaderBoardHologram;
    @ConfigData(
            path = "loc.hologram.helper"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location helperHologramLocation;
    @ConfigData(
            path = "loc.region.pitA"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location pitLocA;
    @ConfigData(
            path = "loc.region.pitB"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location pitLocB;
    @ConfigData(
            path = "loc.region.enchant"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location enchantLocation;

    @ConfigData(
            path = "loc.region.sewers"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location sewersLocation;
    @ConfigData(
            path = "loc.events.hamburger.shop"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location hamburgerShopLoc;
    @ConfigData(
            path = "loc.events.hamburger.villager.a"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> hamburgerNpcLocA = new ArrayList<>();
    @ConfigData(
            path = "loc.events.spire.spireLoc"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location spireLoc;
    //每层塔地面中心坐标 (1~9)
    @ConfigData(
            path = "loc.events.spire.spireFloorLocations"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> spireFloorLoc = new ArrayList<>();
    @ConfigData(
            path = "loc.events.spire.spireFloorY"
    )
    private List<Integer> floorY = new ArrayList<>();
    @ConfigData(
            path = "loc.events.hamburger.villager.a-offer" //the villager who offer the ham
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location hamburgerOfferNpcLocA;
    @ConfigData(
            path = "loc.events.rage.middle" //the middle point of rage pit
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location ragePitMiddle;
    @ConfigData(
            path = "loc.events.rage.radius" //the radius of rage pit
    )
    private int ragePitRadius;
    @ConfigData(
            path = "loc.events.rage.height" //the height of rage pit
    )
    private int ragePitHeight;
    @ConfigData(
            path = "loc.portal.posA" //Middle portal posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location portalPosA;

    @ConfigData(
            path = "loc.portal.posB" //Middle portal posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location portalPosB;
    @ConfigData(path = "loc.events.dragon-egg.loc")
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location dragonEggLoc;
    @ConfigData(
            path = "loc.events.cake.a.posA" //cake posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneAPosA;


    @ConfigData(
            path = "loc.events.cake.a.posB" //cake posB
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneAPosB;
    @ConfigData(
            path = "loc.events.cake.b.posA" //cake posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneBPosA;
    @ConfigData(
            path = "loc.events.cake.b.posB" //cake posB
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneBPosB;
    @ConfigData(
            path = "loc.events.cake.c.posA" //cake posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneCPosA;
    @ConfigData(
            path = "loc.events.cake.c.posB" //cake posB
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneCPosB;
    @ConfigData(
            path = "loc.events.cake.d.posA" //cake posA
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneDPosA;
    @ConfigData(
            path = "loc.events.cake.d.posB" //cake posB
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private Location cakeZoneDPosB;

    @ConfigData(
            path = "loc.Genesis.angel"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> angelSpawns = new ArrayList<>();

    @ConfigData(
            path = "loc.Genesis.demon"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> demonSpawns = new ArrayList<>();

    @ConfigData(
            path = "loc.packages"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> packageLocations = new ArrayList<>();

    @ConfigData(
            path = "debug.debugServer"
    )
    private boolean debugServer;
    @ConfigData(
            path = "debug.public"
    )
    private boolean debugServerPublic;
    @ConfigData(
            path = "debug.infinityNpcLoc"
    )
    private Location infinityNpcLocation;
    @ConfigData(
            path = "debug.ienchantNpcLoc"
    )
    private Location enchantNpcLocation;

    @ConfigData(
            path = "curfew.enable"
    )
    private boolean curfewEnable;
    @ConfigData(
            path = "curfew.start"
    )
    private int curfewStart;
    @ConfigData(
            path = "curfew.end"
    )
    private int curfewEnd;

    @ConfigData(
            path = "loc.Sewers.chests"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> sewersChestsLocations = new ArrayList<>();

    @ConfigData(
            path = "loc.squads"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> squadsLocations = new ArrayList<>();

    @ConfigData(
            path = "loc.blockHead"
    )
    @ConfigSerializer(serializer = LocationSerializer.class)
    private List<Location> blockHeadLocations = new ArrayList<>();

    @ConfigData(
            path = "genesis-start-date"
    )
    private long genesisStartDate = 1675339795842L;

    public PitConfig(JavaPlugin plugin) {
        super(plugin);
    }

    public boolean isGenesisEnable() {
        try {
            return System.currentTimeMillis() >= getGenesisStartTime() && System.currentTimeMillis() < getGenesisEndTime();
        } catch (Exception ignored) {
            return false;
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");

    public long getGenesisStartTime() {
        return genesisStartDate;
    }

    public long getGenesisOriginalEndTime() {
        return getGenesisStartTime() + 16 * 24 * 60 * 60 * 1000;
    }

    public long getGenesisEndTime() {
        long endTime = getGenesisOriginalEndTime();
        while (endTime < System.currentTimeMillis()) {
            endTime += 56 * 24 * 60 * 60 * 1000L;
        }
        return endTime;
    }

    //Season X: From Season X-1 End To Season X End
    public int getGenesisSeason() {
        int season = 1;
        long endTime = getGenesisOriginalEndTime();
        while (endTime < System.currentTimeMillis()) {
            endTime += 56L * 24 * 60 * 60 * 1000;
            season++;
        }
        return season;
    }

    public String getToken() {
        return token;
    }

    public String getState() {
        return state;
    }

    public int getCurfewStart() {
        return curfewStart;
    }

    public DateFormat getDf() {
        return df;
    }

    public boolean isTradeEnable() {
        return tradeEnable;
    }

    public boolean isPVPEnable() {
        return PVPEnable;
    }

    public String getMongoDBAddress() {
        return mongoDBAddress;
    }

    public int getMongoDBPort() {
        return mongoDBPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getMongoUser() {
        return mongoUser;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public boolean isRedisEnable() {
        return redisEnable;
    }

    public String getRedisAddress() {
        return redisAddress;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public int getArenaHighestY() {
        return arenaHighestY;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Location getHologramLocation() {
        return hologramLocation;
    }

    public Location getShopNpcLocation() {
        return shopNpcLocation;
    }

    public Location getQuestNpcLocation() {
        return questNpcLocation;
    }

    public Location getPerkNpcLocation() {
        return perkNpcLocation;
    }

    public String getServerName() {
        return serverName;
    }

    public Location getPrestigeNpcLocation() {
        return prestigeNpcLocation;
    }

    public Location getStatusNpcLocation() {
        return statusNpcLocation;
    }

    public Location getKeeperNpcLocation() {
        return keeperNpcLocation;
    }

    public Location getMailNpcLocation() {
        return mailNpcLocation;
    }

    public Location getGenesisDemonNpcLocation() {
        return genesisDemonNpcLocation;
    }

    public Location getGenesisAngelNpcLocation() {
        return genesisAngelNpcLocation;
    }

    public Location getLeaderBoardHologram() {
        return leaderBoardHologram;
    }

    public Location getHelperHologramLocation() {
        return helperHologramLocation;
    }

    public Location getPitLocA() {
        return pitLocA;
    }

    public Location getPitLocB() {
        return pitLocB;
    }

    public Location getEnchantLocation() {
        return enchantLocation;
    }

    public Location getHamburgerShopLoc() {
        return hamburgerShopLoc;
    }

    public List<Location> getHamburgerNpcLocA() {
        return hamburgerNpcLocA;
    }

    public Location getSpireLoc() {
        return spireLoc;
    }

    public List<Location> getSpireFloorLoc() {
        return spireFloorLoc;
    }

    public List<Integer> getFloorY() {
        return floorY;
    }

    public Location getHamburgerOfferNpcLocA() {
        return hamburgerOfferNpcLocA;
    }

    public Location getRagePitMiddle() {
        return ragePitMiddle;
    }

    public int getRagePitRadius() {
        return ragePitRadius;
    }

    public int getRagePitHeight() {
        return ragePitHeight;
    }

    public Location getPortalPosA() {
        return portalPosA;
    }

    public Location getPortalPosB() {
        return portalPosB;
    }

    public Location getCakeZoneAPosA() {
        return cakeZoneAPosA;
    }

    public Location getCakeZoneAPosB() {
        return cakeZoneAPosB;
    }

    public Location getCakeZoneBPosA() {
        return cakeZoneBPosA;
    }

    public Location getDragonEggLoc() {
        return dragonEggLoc;
    }

    public void setDragonEggLoc(Location dragonEggLoc) {
        this.dragonEggLoc = dragonEggLoc;
    }

    public Location getCakeZoneBPosB() {
        return cakeZoneBPosB;
    }

    public Location getCakeZoneCPosA() {
        return cakeZoneCPosA;
    }

    public Location getCakeZoneCPosB() {
        return cakeZoneCPosB;
    }

    public Location getCakeZoneDPosA() {
        return cakeZoneDPosA;
    }

    public Location getCakeZoneDPosB() {
        return cakeZoneDPosB;
    }

    public List<Location> getAngelSpawns() {
        return angelSpawns;
    }

    public List<Location> getDemonSpawns() {
        return demonSpawns;
    }

    public List<Location> getPackageLocations() {
        return packageLocations;
    }

    public boolean isDebugServer() {
        return debugServer;
    }

    public boolean isDebugServerPublic() {
        return debugServerPublic;
    }

    public Location getInfinityNpcLocation() {
        return infinityNpcLocation;
    }

    public Location getEnchantNpcLocation() {
        return enchantNpcLocation;
    }


    public boolean isCurfewEnable() {
        return curfewEnable;
    }

    public int getCurfewEnd() {
        return curfewEnd;
    }

    public List<Location> getSewersChestsLocations() {
        return sewersChestsLocations;
    }

    public List<Location> getSquadsLocations() {
        return squadsLocations;
    }

    public List<Location> getBlockHeadLocations() {
        return blockHeadLocations;
    }

    public long getGenesisStartDate() {
        return genesisStartDate;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDf(DateFormat df) {
        this.df = df;
    }

    public void setTradeEnable(boolean tradeEnable) {
        this.tradeEnable = tradeEnable;
    }

    public void setPVPEnable(boolean PVPEnable) {
        this.PVPEnable = PVPEnable;
    }

    public void setMongoDBAddress(String mongoDBAddress) {
        this.mongoDBAddress = mongoDBAddress;
    }

    public void setMongoDBPort(int mongoDBPort) {
        this.mongoDBPort = mongoDBPort;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setMongoUser(String mongoUser) {
        this.mongoUser = mongoUser;
    }

    public void setMongoPassword(String mongoPassword) {
        this.mongoPassword = mongoPassword;
    }

    public void setRedisEnable(boolean redisEnable) {
        this.redisEnable = redisEnable;
    }

    public void setRedisAddress(String redisAddress) {
        this.redisAddress = redisAddress;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public void setArenaHighestY(int arenaHighestY) {
        this.arenaHighestY = arenaHighestY;
    }

    public void setSpawnLocations(List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    public void setShopNpcLocation(Location shopNpcLocation) {
        this.shopNpcLocation = shopNpcLocation;
    }

    public void setQuestNpcLocation(Location questNpcLocation) {
        this.questNpcLocation = questNpcLocation;
    }

    public void setPerkNpcLocation(Location perkNpcLocation) {
        this.perkNpcLocation = perkNpcLocation;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setPrestigeNpcLocation(Location prestigeNpcLocation) {
        this.prestigeNpcLocation = prestigeNpcLocation;
    }

    public void setStatusNpcLocation(Location statusNpcLocation) {
        this.statusNpcLocation = statusNpcLocation;
    }

    public void setKeeperNpcLocation(Location keeperNpcLocation) {
        this.keeperNpcLocation = keeperNpcLocation;
    }

    public void setMailNpcLocation(Location mailNpcLocation) {
        this.mailNpcLocation = mailNpcLocation;
    }

    public void setGenesisDemonNpcLocation(Location genesisDemonNpcLocation) {
        this.genesisDemonNpcLocation = genesisDemonNpcLocation;
    }

    public void setGenesisAngelNpcLocation(Location genesisAngelNpcLocation) {
        this.genesisAngelNpcLocation = genesisAngelNpcLocation;
    }

    public void setSewersFishNpcLocation(Location sewersFishNpcLocation) {
        this.sewersFishNpcLocation = sewersFishNpcLocation;
    }

    public Location getSewersFishNpcLocation() {
        return sewersFishNpcLocation;
    }

    public void setLeaderBoardHologram(Location leaderBoardHologram) {
        this.leaderBoardHologram = leaderBoardHologram;
    }

    public void setHelperHologramLocation(Location helperHologramLocation) {
        this.helperHologramLocation = helperHologramLocation;
    }

    public void setPitLocA(Location pitLocA) {
        this.pitLocA = pitLocA;
    }

    public void setPitLocB(Location pitLocB) {
        this.pitLocB = pitLocB;
    }

    public void setEnchantLocation(Location enchantLocation) {
        this.enchantLocation = enchantLocation;
    }

    public void setSewersLocation(Location sewersLocation) {
        this.sewersLocation = sewersLocation;
    }

    public Location getSewersLocation() {
        return sewersLocation;
    }

    public void setHamburgerShopLoc(Location hamburgerShopLoc) {
        this.hamburgerShopLoc = hamburgerShopLoc;
    }

    public void setHamburgerNpcLocA(List<Location> hamburgerNpcLocA) {
        this.hamburgerNpcLocA = hamburgerNpcLocA;
    }

    public void setSpireLoc(Location spireLoc) {
        this.spireLoc = spireLoc;
    }

    public void setSpireFloorLoc(List<Location> spireFloorLoc) {
        this.spireFloorLoc = spireFloorLoc;
    }

    public void setFloorY(List<Integer> floorY) {
        this.floorY = floorY;
    }

    public void setHamburgerOfferNpcLocA(Location hamburgerOfferNpcLocA) {
        this.hamburgerOfferNpcLocA = hamburgerOfferNpcLocA;
    }

    public void setRagePitMiddle(Location ragePitMiddle) {
        this.ragePitMiddle = ragePitMiddle;
    }

    public void setRagePitRadius(int ragePitRadius) {
        this.ragePitRadius = ragePitRadius;
    }

    public void setRagePitHeight(int ragePitHeight) {
        this.ragePitHeight = ragePitHeight;
    }

    public void setPortalPosA(Location portalPosA) {
        this.portalPosA = portalPosA;
    }

    public void setPortalPosB(Location portalPosB) {
        this.portalPosB = portalPosB;
    }

    public void setCakeZoneAPosA(Location cakeZoneAPosA) {
        this.cakeZoneAPosA = cakeZoneAPosA;
    }

    public void setCakeZoneAPosB(Location cakeZoneAPosB) {
        this.cakeZoneAPosB = cakeZoneAPosB;
    }

    public void setCakeZoneBPosA(Location cakeZoneBPosA) {
        this.cakeZoneBPosA = cakeZoneBPosA;
    }

    public void setCakeZoneBPosB(Location cakeZoneBPosB) {
        this.cakeZoneBPosB = cakeZoneBPosB;
    }

    public void setCakeZoneCPosA(Location cakeZoneCPosA) {
        this.cakeZoneCPosA = cakeZoneCPosA;
    }

    public void setCakeZoneCPosB(Location cakeZoneCPosB) {
        this.cakeZoneCPosB = cakeZoneCPosB;
    }

    public void setCakeZoneDPosA(Location cakeZoneDPosA) {
        this.cakeZoneDPosA = cakeZoneDPosA;
    }

    public void setCakeZoneDPosB(Location cakeZoneDPosB) {
        this.cakeZoneDPosB = cakeZoneDPosB;
    }

    public void setAngelSpawns(List<Location> angelSpawns) {
        this.angelSpawns = angelSpawns;
    }

    public void setDemonSpawns(List<Location> demonSpawns) {
        this.demonSpawns = demonSpawns;
    }

    public void setPackageLocations(List<Location> packageLocations) {
        this.packageLocations = packageLocations;
    }

    public void setDebugServer(boolean debugServer) {
        this.debugServer = debugServer;
    }

    public void setDebugServerPublic(boolean debugServerPublic) {
        this.debugServerPublic = debugServerPublic;
    }

    public void setInfinityNpcLocation(Location infinityNpcLocation) {
        this.infinityNpcLocation = infinityNpcLocation;
    }

    public void setEnchantNpcLocation(Location enchantNpcLocation) {
        this.enchantNpcLocation = enchantNpcLocation;
    }

    public void setCurfewEnable(boolean curfewEnable) {
        this.curfewEnable = curfewEnable;
    }

    public void setCurfewStart(int curfewStart) {
        this.curfewStart = curfewStart;
    }

    public void setCurfewEnd(int curfewEnd) {
        this.curfewEnd = curfewEnd;
    }

    public void setSewersChestsLocations(List<Location> sewersChestsLocations) {
        this.sewersChestsLocations = sewersChestsLocations;
    }

    public void setSquadsLocations(List<Location> squadsLocations) {
        this.squadsLocations = squadsLocations;
    }

    public void setBlockHeadLocations(List<Location> blockHeadLocations) {
        this.blockHeadLocations = blockHeadLocations;
    }

    public void setGenesisStartDate(long genesisStartDate) {
        this.genesisStartDate = genesisStartDate;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}
