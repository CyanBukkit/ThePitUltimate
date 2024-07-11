package cn.charlotte.pit;

import cn.charlotte.pit.api.PitInternalHook;
import cn.charlotte.pit.api.PointsAPI;
import cn.charlotte.pit.buff.BuffFactory;
import cn.charlotte.pit.config.PitConfig;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.database.MongoDB;
import cn.charlotte.pit.enchantment.EnchantmentFactor;
import cn.charlotte.pit.event.OriginalTimeChangeEvent;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.events.EventsHandler;
import cn.charlotte.pit.game.Game;
import cn.charlotte.pit.hologram.HologramFactory;
import cn.charlotte.pit.item.ItemFactor;
import cn.charlotte.pit.medal.MedalFactory;
import cn.charlotte.pit.menu.admin.backpack.button.DupeItemButton;
import cn.charlotte.pit.minigame.MiniGameController;
import cn.charlotte.pit.movement.PlayerMoveHandler;
import cn.charlotte.pit.npc.NpcFactory;
import cn.charlotte.pit.perk.PerkFactory;
import cn.charlotte.pit.pet.PetFactory;
import cn.charlotte.pit.quest.QuestFactory;
import cn.charlotte.pit.runnable.*;
import cn.charlotte.pit.util.bossbar.BossBarHandler;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.dependencies.Dependency;
import cn.charlotte.pit.util.dependencies.DependencyManager;
import cn.charlotte.pit.util.dependencies.loaders.LoaderType;
import cn.charlotte.pit.util.dependencies.loaders.ReflectionClassLoader;
import cn.charlotte.pit.util.hologram.packet.PacketHologramRunnable;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.menu.MenuUpdateTask;
import cn.charlotte.pit.util.nametag.NametagHandler;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.rank.RankUtil;
import cn.charlotte.pit.util.sign.SignGui;
import cn.charlotte.pit.util.sound.SoundFactory;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.jnic.annotation.Include;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import spg.lgdev.iSpigot;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * @author EmptyIrony, Misoryan
 */

@Include
public class ThePit extends JavaPlugin implements PluginMessageListener {

    public static PitInternalHook api;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ThePit.class);
    private static boolean DEBUG_SERVER = false;
    private static String bungeeServerName;
    private static ThePit instance;


    private MongoDB mongoDB;
    private JedisPool jedis;
    private PitConfig pitConfig;
    private EnchantmentFactor enchantmentFactor;
    private NpcFactory npcFactory;
    private NametagHandler nametagHandler;
    private Game game;
    private MedalFactory medalFactory;
    private PerkFactory perkFactory;
    private BuffFactory buffFactory;
    private HologramFactory hologramFactory;
    private EventFactory eventFactory;
    private PlayerMoveHandler movementHandler;
    private QuestFactory questFactory;
    private SignGui signGui;
    private BossBarHandler bossBar;
    private ItemFactor itemFactor = new ItemFactor();
    private RebootRunnable rebootRunnable;
    private MiniGameController miniGameController;
    private SoundFactory soundFactory;
    private PetFactory petFactory;

    private PlayerPointsAPI playerPoints;
    private LuckPerms luckPerms;

    private PointsAPI pointsAPI;

    private String serverId;

    private BukkitAudiences audiences;

    public static boolean isDEBUG_SERVER() {
        return ThePit.DEBUG_SERVER;
    }

    public static ThePit getInstance() {
        return ThePit.instance;
    }

    public static String getBungeeServerName() {
        return bungeeServerName == null ? "THEPIT" : bungeeServerName.toUpperCase();
    }

    private static void setBungeeServerName(String name) {
        bungeeServerName = name;
    }

    @Override
    public void onLoad() {
        this.loadDepend();
    }

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);
        DupeItemButton dupeItemButton = new DupeItemButton();
        if (dupeItemButton.isDupe() == DupeItemButton.DumpType.SUCCESSFULLY) {
            if (h()) {
                instance = this;
                sendLogs("\n" +
                        "§d////////////////////////////////////////////////////////////////////\n" +
                        "§d//                          _ooOoo_                               //\n" +
                        "§d//                         o8888888o                              //\n" +
                        "§d//                         88\" . \"88                              //\n" +
                        "§d//                         (| ^_^ |)                              //\n" +
                        "§d//                         O\\  =  /O                              //\n" +
                        "§d//                      ____/`---'\\____                           //\n" +
                        "§d//                    .'  \\\\|     |//  `.                         //\n" +
                        "§d//                   /  \\\\|||  :  |||//  \\                        //\n" +
                        "§d//                  /  _||||| -:- |||||-  \\                       //\n" +
                        "§d//                  |   | \\\\\\  -  /// |   |                       //\n" +
                        "§d//                  | \\_|  ''\\---/''  |   |                       //\n" +
                        "§d//                  \\  .-\\__  `-`  ___/-. /                       //\n" +
                        "§d//                ___`. .'  /--.--\\  `. . ___                     //\n" +
                        "§d//              .\"\" '<  `.___\\_<|>_/___.'  >'\"\".                  //\n" +
                        "§d//            | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |                 //\n" +
                        "§d//            \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /                 //\n" +
                        "§d//      ========`-.____`-.___\\_____/___.-`____.-'========         //\n" +
                        "§d//                           `=---='                              //\n" +
                        "§d//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //\n" +
                        "§d//            佛祖保佑       永不宕机      永无BUG                //\n" +
                        "§d////////////////////////////////////////////////////////////////////");
                sendLogs("§cPowered By §eEmptyIrony huanmeng_qwq Araykal.");
                sendLogs("§cSupport to §bNyacho NetWork.");
                serverId = RandomUtil.randomStr();

                saveDefaultConfig();

                hookPlayerPoints();
                hookLuckPerms();

                boolean whiteList = Bukkit.getServer().hasWhitelist();
                Bukkit.getServer().setWhitelist(true);

                iSpigot spigot = new iSpigot();
                Bukkit.getServer().getPluginManager().registerEvents(spigot, this);

                this.loadConfig();

                this.loadDatabase();

                this.loadMenu();
                this.loadNpc();
                this.loadGame();
                this.loadMedals();
                this.loadBuffs();
                this.loadHologram();
                this.loadSound();
                this.loadPerks();
                this.loadEnchantment();
                this.loadQuest();
                this.loadEvents();

                try {
                    this.loadMoveHandler();
                } catch (Exception ignored) {
                }

                this.loadQuest();
                this.initBossBar();

                this.initPet();

                this.signGui = new SignGui(this);

                this.rebootRunnable = new RebootRunnable();
                this.rebootRunnable.runTaskTimerAsynchronously(this, 20, 20);

                this.miniGameController = new MiniGameController();
                this.miniGameController.runTaskTimerAsynchronously(this, 1, 1);

                new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(new AutoSaveRunnable(), 1, 1, TimeUnit.MINUTES);
                new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(new DayNightCycleRunnable(), 1, 1, TimeUnit.SECONDS);

                Bukkit.getWorlds().forEach(w -> {
                    w.getEntities().forEach(e -> {
                        if (e instanceof ArmorStand) {
                            e.remove();
                        }
                    });
                });
//            this.printBanner();

                new Thread(new LeaderBoardRunnable()).start();

                try {
                    EventsHandler.INSTANCE.loadFromDatabase();
                } catch (Exception ignored) {

                }

                for (World world : Bukkit.getWorlds()) {
                    world.setGameRuleValue("keepInventory", "true");
                    world.setGameRuleValue("mobGriefing", "false");
                    world.setGameRuleValue("doDaylightCycle", "false");
                }
                this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
                this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
                FixedRewardData.Companion.refreshAll();
                Bukkit.getServer().setWhitelist(whiteList);
                new ProfileLoadRunnable(this);
                PitMain.start();
            } else {
                while (!h()) {
                    sendLogs("§c???");
                }
            }
        }
    }

    public void sendLogs(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    @Override
    public void onDisable() {
        PacketHologramRunnable.deSpawnAll();
        synchronized (Bukkit.getOnlinePlayers()) {
            CC.boardCast("&6&l公告! &7正在执行关闭服务器...");

            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                    if (profile.isLoaded()) {
                        profile.setInventory(InventoryUtil.playerInventoryFromPlayer(player));
                        profile.save();
                        CC.boardCast("&6&l公告! &7正在保存 " + player.getDisplayName() + " 玩家的数据...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CC.boardCast("&6&l公告! &7正在关闭服务器...");
        }
    }

    public static boolean h() {
        try {
            InetAddress address = InetAddress.getByName("thepit.nyacho.cn");
            return address.isReachable(5000);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!"BungeeCord".equals(channel)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        //setServerName
        if ("GetServer".equals(subchannel)) {
            setBungeeServerName(in.readUTF());
        }
    }

    public void connect(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public static boolean callTimeChange(long time) {
        final OriginalTimeChangeEvent event = new OriginalTimeChangeEvent(time);
        event.callEvent();
        return event.isCancelled();
    }

    private void initPet() {
        this.petFactory = new PetFactory();
        this.petFactory.init();
        Bukkit.getPluginManager().registerEvents(this.petFactory, this);
    }

    private void initBossBar() {
        this.bossBar = new BossBarHandler();
        Bukkit.getPluginManager().registerEvents(this.bossBar, this);
    }

    private void loadSound() {
        this.soundFactory = new SoundFactory();
        this.soundFactory.init();
    }

    private void loadRedisLock() {
        PitConfig pitConfig = ThePit.getInstance().getPitConfig();
    }

    private void loadHologram() {
        this.hologramFactory = new HologramFactory();
        this.hologramFactory.init();
    }

    private void loadMenu() {
        this.getServer().getScheduler().runTaskTimer(this, new MenuUpdateTask(), 20L, 20L);
    }

    public void loadEnchantment() {
        this.enchantmentFactor = new EnchantmentFactor();
    }

    public void loadPerks() {
        this.perkFactory = new PerkFactory();
    }

    private void loadMedals() {
        this.medalFactory = new MedalFactory();
        this.medalFactory.init();
        getServer().getPluginManager().registerEvents(this.medalFactory, this);
    }

    private void loadBuffs() {
        this.buffFactory = new BuffFactory();
        this.buffFactory.init();
    }

    private void loadGame() {
        this.game = new Game();
        this.game.initRunnable();
    }

    @SneakyThrows
    private void loadMoveHandler() {
        this.movementHandler = new PlayerMoveHandler();
        iSpigot.INSTANCE.addMovementHandler(this.movementHandler);
    }

    public void loadQuest() {
        this.questFactory = new QuestFactory();
    }

    public void loadListener() {
        /*Collection<Class<?>> classes = ClassUtil.getClassesInPackage(this, "cn.charlotte.pit");
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(AutoRegister.class)) {
                if (Listener.class.isAssignableFrom(clazz)) {
                    try {
                        Bukkit.getPluginManager()
                                .registerEvents((Listener) clazz.newInstance(), ThePit.getInstance());
                    } catch (Exception ignored) {

                    }
                }
            }
        }*/
    }

    private void loadConfig() {
        log.info("Loading configuration...");
        this.pitConfig = new PitConfig(this);
        this.pitConfig.load();
        log.info("Loaded configuration!");

        DEBUG_SERVER = this.pitConfig.isDebugServer();
        if (DEBUG_SERVER) {
            this.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void permissionCheckOnJoin(PlayerLoginEvent event) {
                    final Player player = event.getPlayer();
                    if (pitConfig.isDebugServerPublic()) {
                        final String name = RankUtil.getPlayerRealColoredName(player.getUniqueId());
                        if (name.contains("&7") || name.contains("§7")) {
                            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "你所在的用户组当前无法进入此分区!");
                        }
                    } else if (!player.isOp() && !player.hasPermission("thepit.admin")) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "此分区当前未开放,开放时间请关注官方公告!");
                    }
                }
            }, this);
        }

        if (pitConfig.isRedisEnable()) {
            jedis = new JedisPool(
                    new GenericObjectPoolConfig(),
                    pitConfig.getRedisAddress(),
                    pitConfig.getRedisPort(),
                    Protocol.DEFAULT_TIMEOUT,
                    pitConfig.getRedisPassword(),
                    false
            );
        }
    }

    private void loadDatabase() {
        log.info("Loading mongodb...");
        this.mongoDB = new MongoDB();
        this.mongoDB.connect();
        log.info("Loaded mongodb!");
    }

    private void loadNpc() {
        log.info("Loading NPCFactory...");
        this.npcFactory = new NpcFactory();
        log.info("Loaded NPCFactory!");
    }

    public void loadEvents() {
        log.info("Loading Events...");
        this.eventFactory = new EventFactory();
        log.info("Loaded Events!");
    }

    private void loadDepend() {
        DependencyManager dependencyManager = new DependencyManager(this, new ReflectionClassLoader(this));
        dependencyManager.loadDependencies(
                new Dependency(
                        "websocket",
                        "org.java-websocket",
                        "Java-WebSocket",
                        "1.5.4",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "annotations",
                        "org.jetbrains",
                        "annotations",
                        "13.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "kotlin-stdlib-common",
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib-common",
                        "1.4.32",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "kotlin-stdlib-jdk7",
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib-jdk7",
                        "1.4.32",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "kotlin-stdlib",
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib",
                        "1.4.32",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "kotlin-stdlib-jdk8",
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib-jdk8",
                        "1.4.32",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Junit",
                        "junit",
                        "junit",
                        "4.11",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Apache Http Client",
                        "org.apache.httpcomponents",
                        "httpclient",
                        "4.4",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Pool2",
                        "org.apache.commons",
                        "commons-pool2",
                        "2.4.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Apache Http Core",
                        "org.apache.httpcomponents",
                        "httpcore",
                        "4.4",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Apache Logging",
                        "commons-logging",
                        "commons-logging",
                        "1.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoDB",
                        "org.mongodb",
                        "mongo-java-driver",
                        "3.12.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Jedis",
                        "redis.clients",
                        "jedis",
                        "2.9.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoJack",
                        "org.mongojack",
                        "mongojack",
                        "4.8.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Annotations",
                        "com.fasterxml.jackson.core",
                        "jackson-annotations",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Databind",
                        "com.fasterxml.jackson.core",
                        "jackson-databind",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Core",
                        "com.fasterxml.jackson.core",
                        "jackson-core",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-DataType",
                        "com.fasterxml.jackson.datatype",
                        "jackson-datatype-jsr310",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Bson4Jackson",
                        "de.undercouch",
                        "bson4jackson",
                        "2.9.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Redisson",
                        "org.redisson",
                        "redisson",
                        "3.0.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Netty-common",
                        "io.netty",
                        "netty-common",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Netty-codec",
                        "io.netty",
                        "netty-codec",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-buffer",
                        "io.netty",
                        "netty-buffer",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-transport",
                        "io.netty",
                        "netty-transport",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-handler",
                        "io.netty",
                        "netty-handler",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "reactor-core",
                        "io.projectreactor",
                        "reactor-core",
                        "3.3.9.RELEASE",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "rxjava",
                        "io.reactivex.rxjava2",
                        "rxjava",
                        "2.2.19",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "cache-api",
                        "javax.cache",
                        "cache-api",
                        "1.0.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "byte-buddy",
                        "net.bytebuddy",
                        "byte-buddy",
                        "1.10.14",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling-river",
                        "org.jboss.marshalling",
                        "jboss-marshalling-river",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jodd-bean",
                        "org.jodd",
                        "jodd-bean",
                        "5.1.6",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "snakeyaml",
                        "org.yaml",
                        "snakeyaml",
                        "1.26",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "slf4j-api",
                        "org.slf4j",
                        "slf4j-api",
                        "1.7.30",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling-river",
                        "org.jboss.marshalling",
                        "jboss-marshalling-river",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling",
                        "org.jboss.marshalling",
                        "jboss-marshalling",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jackson-dataformat-yaml",
                        "com.fasterxml.jackson.dataformat",
                        "jackson-dataformat-yaml",
                        "2.11.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-all",
                        "io.netty",
                        "netty-all",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "guava",
                        "com.google.guava",
                        "guava",
                        "29.0-jre",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "asm",
                        "org.ow2.asm",
                        "asm",
                        "7.3.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "asm-commons",
                        "org.ow2.asm",
                        "asm-commons",
                        "7.3.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "asm-tree",
                        "org.ow2.asm",
                        "asm-tree",
                        "7.3.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "asm-util",
                        "org.ow2.asm",
                        "asm-util",
                        "7.3.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "nashorn-core",
                        "org.openjdk.nashorn",
                        "nashorn-core",
                        "15.3",
                        LoaderType.REFLECTION
                )
        );
    }

    public MongoDB getMongoDB() {
        return this.mongoDB;
    }

    public PitConfig getPitConfig() {
        return this.pitConfig;
    }

    public EnchantmentFactor getEnchantmentFactor() {
        return this.enchantmentFactor;
    }

    public NpcFactory getNpcFactory() {
        return this.npcFactory;
    }

    public NametagHandler getNametagHandler() {
        return this.nametagHandler;
    }

    public Game getGame() {
        return this.game;
    }

    public MedalFactory getMedalFactory() {
        return this.medalFactory;
    }

    public PerkFactory getPerkFactory() {
        return this.perkFactory;
    }

    public BuffFactory getBuffFactory() {
        return this.buffFactory;
    }

    public HologramFactory getHologramFactory() {
        return this.hologramFactory;
    }

    public EventFactory getEventFactory() {
        return this.eventFactory;
    }

    public PlayerMoveHandler getMovementHandler() {
        return this.movementHandler;
    }

    public QuestFactory getQuestFactory() {
        return this.questFactory;
    }

    public SignGui getSignGui() {
        return this.signGui;
    }

    public BossBarHandler getBossBar() {
        return this.bossBar;
    }

    public ItemFactor getItemFactor() {
        return this.itemFactor;
    }

    public RebootRunnable getRebootRunnable() {
        return this.rebootRunnable;
    }

    public MiniGameController getMiniGameController() {
        return this.miniGameController;
    }

    public SoundFactory getSoundFactory() {
        return this.soundFactory;
    }

    public PetFactory getPetFactory() {
        return this.petFactory;
    }

    /**
     * Validate that we have access to PlayerPoints
     *
     * @return True if we have PlayerPoints, else false.
     */
    private boolean hookPlayerPoints() {
        this.playerPoints = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
        return playerPoints != null;
    }

    /**
     * Accessor for other parts of your plugin to retrieve PlayerPoints.
     *
     * @return PlayerPoints plugin instance
     */
    public PlayerPointsAPI getPlayerPoints() {
        if (hookPlayerPoints()) {
            return playerPoints;
        }
        return null;
    }

    private boolean hookLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            LuckPermsProvider.get();
        }
        return luckPerms != null;
    }

    public LuckPerms getLuckPerms() {
        if (hookLuckPerms()) {
            return luckPerms;
        }
        return null;
    }

    public String getServerId() {
        return serverId;
    }

    public User getLuckPermsUser(UUID uuid) {
        try {
            UserManager userManager = luckPerms.getUserManager();
            CompletableFuture<User> userFuture = userManager.loadUser(uuid);
            return userFuture.join();
        } catch (RejectedExecutionException | CompletionException e) {
            if (Bukkit.getPlayer(uuid).isOnline()) {
                return luckPerms.getUserManager().getUser(uuid);
            } else {
                return null;
            }
        }
    }

    public JedisPool getJedis() {
        return jedis;
    }

    public PointsAPI getPointsAPI() {
        return pointsAPI;
    }

    public void setPointsAPI(PointsAPI pointsAPI) {
        this.pointsAPI = pointsAPI;
    }

    public Object getLuckPermsUserPrefix(UUID uuid) {
        try {
            UserManager userManager = luckPerms.getUserManager();
            CompletableFuture<User> userFuture = userManager.loadUser(uuid);
            return userFuture.thenApplyAsync(user -> user.getCachedData().getMetaData().getPrefix());
        } catch (RejectedExecutionException | CompletionException e) {
            if (Bukkit.getPlayer(uuid).isOnline()) {
                return Objects.requireNonNull(luckPerms.getUserManager().getUser(uuid)).getCachedData().getMetaData().getPrefix();
            } else {
                return null;
            }
        }
    }

    public static void setApi(PitInternalHook api) {
        ThePit.api = api;
    }

    public static PitInternalHook getApi() {
        return api;
    }


    public BukkitAudiences getAudiences() {
        return audiences;
    }
}
