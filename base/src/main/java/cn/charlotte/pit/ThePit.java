package cn.charlotte.pit;

import cn.charlotte.pit.actionbar.IActionBarManager;
import cn.charlotte.pit.api.PitInternalHook;
import cn.charlotte.pit.api.PointsAPI;
import cn.charlotte.pit.buff.BuffFactory;
import cn.charlotte.pit.config.PitConfig;
import cn.charlotte.pit.data.FixedRewardData;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.operator.IProfilerOperator;
import cn.charlotte.pit.database.MongoDB;
import cn.charlotte.pit.enchantment.EnchantmentFactor;
import cn.charlotte.pit.event.OriginalTimeChangeEvent;
import cn.charlotte.pit.events.EventFactory;
import cn.charlotte.pit.events.EventsHandler;
import cn.charlotte.pit.hologram.HologramFactory;
import cn.charlotte.pit.item.IItemFactory;
import cn.charlotte.pit.item.ItemFactor;
import cn.charlotte.pit.medal.MedalFactory;
import cn.charlotte.pit.minigame.MiniGameController;
import cn.charlotte.pit.movement.PlayerMoveHandler;
import cn.charlotte.pit.npc.NpcFactory;
import cn.charlotte.pit.perk.PerkFactory;
import cn.charlotte.pit.pet.PetFactory;
import cn.charlotte.pit.quest.QuestFactory;
import cn.charlotte.pit.runnable.DayNightCycleRunnable;
import cn.charlotte.pit.runnable.LeaderBoardRunnable;
import cn.charlotte.pit.runnable.ProfileLoadRunnable;
import cn.charlotte.pit.runnable.RebootRunnable;
import cn.charlotte.pit.trade.Game;
import cn.charlotte.pit.license.MagicLoader;
import cn.charlotte.pit.util.BannerUtil;
import cn.charlotte.pit.util.bossbar.BossBarHandler;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.dependencies.Dependency;
import cn.charlotte.pit.util.dependencies.DependencyManager;
import cn.charlotte.pit.util.dependencies.loaders.LoaderType;
import cn.charlotte.pit.util.dependencies.loaders.ReflectionClassLoader;
import cn.charlotte.pit.util.hologram.packet.PacketHologramRunnable;
import cn.charlotte.pit.util.menu.MenuUpdateTask;
import cn.charlotte.pit.util.nametag.NametagHandler;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.rank.RankUtil;
import cn.charlotte.pit.util.sign.SignGui;
import cn.charlotte.pit.util.sound.SoundFactory;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.slf4j.Logger;
import pku.yim.license.PluginProxy;
import pku.yim.license.Resource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import spg.lgdev.iSpigot;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;


/**
 * @author EmptyIrony, Misoryan, KleeLoveLife, Rabbit0w0, Araykal
 */
public class ThePit extends JavaPlugin implements PluginMessageListener, PluginProxy {


    public static PitInternalHook api;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ThePit.class);
    private static boolean DEBUG_SERVER = false;
    private static String bungeeServerName;
    private static ThePit instance;


    @Getter
    private MongoDB mongoDB;
    @Getter
    private JedisPool jedis;
    @Getter
    private PitConfig pitConfig;
    @Setter
    @Getter
    private EnchantmentFactor enchantmentFactor;
    @Getter
    private NpcFactory npcFactory;
    @Getter
    private NametagHandler nametagHandler;
    private IItemFactory factory;
    @Getter
    private Game game;
    @Getter
    private MedalFactory medalFactory;
    @Getter
    private PerkFactory perkFactory;
    @Getter
    private BuffFactory buffFactory;
    @Getter
    private HologramFactory hologramFactory;
    @Getter
    private EventFactory eventFactory;
    @Getter
    private PlayerMoveHandler movementHandler;
    @Getter
    private QuestFactory questFactory;
    @Getter
    private SignGui signGui;
    @Getter
    private BossBarHandler bossBar;

    @Getter
    private ItemFactor itemFactor;
    @Getter
    private RebootRunnable rebootRunnable;
    @Getter
    private MiniGameController miniGameController;
    @Getter
    private SoundFactory soundFactory;
    @Getter
    private PetFactory petFactory;
    @Setter
    private IProfilerOperator profileOperator;
    private PlayerPointsAPI playerPoints;
    private LuckPerms luckPerms;

    @Setter
    @Getter
    private PointsAPI pointsAPI;

    @Getter
    private String serverId;
    @Getter
    private BukkitAudiences audiences;
    @Setter
    @Getter
    private IActionBarManager actionBarManager;

    public static boolean isDEBUG_SERVER() {
        return ThePit.DEBUG_SERVER;
    }

    public static ThePit getInstance() {
        return ThePit.instance;
    }

    public static String getBungeeServerName() {
        return bungeeServerName == null ? "THEPIT" : bungeeServerName.toUpperCase();
    }

    public IProfilerOperator getProfileOperator() {
        return profileOperator;
    }

    private static void setBungeeServerName(String name) {
        bungeeServerName = name;
    }


    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);
        instance = this;
        BannerUtil.printFileContent("banner.txt");
        serverId = RandomUtil.forRandomScoreboardString();

        saveDefaultConfig();

        hookPlayerPoints();
        hookLuckPerms();

        boolean whiteList = Bukkit.getServer().hasWhitelist();
        Bukkit.getServer().setWhitelist(true);

        iSpigot spigot = new iSpigot();
        Bukkit.getServer().getPluginManager().registerEvents(spigot, this);

        this.loadConfig();

        this.loadDatabase();
//                this.loadOperator(); //operator

        this.loadItemFactor();
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
        new DayNightCycleRunnable().runTaskTimerAsynchronously(this, 20, 20);

        Bukkit.getWorlds().forEach(w -> w.getEntities().forEach(e -> {
            if (e instanceof ArmorStand) {
                e.remove();
            }
            if (e instanceof Item it) {
                if (it.getItemStack().getType() == Material.GOLD_INGOT) {
                    it.remove(); //garbage remove pieces 修复内存碎片整合慢问题
                }
            }
        }));
//            this.printBanner();

        new LeaderBoardRunnable().runTaskTimerAsynchronously(this, 0, 12000);

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

        //Bridgeing
        MagicLoader.hook();
        MagicLoader.ensureIsLoaded();
    }

    private void loadItemFactor() {
        this.itemFactor = new ItemFactor();
    }


    public void sendLogs(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    @Override
    public void onDisable() {
        PacketHologramRunnable.deSpawnAll();
        synchronized (Bukkit.getOnlinePlayers()) {
            CC.boardCast0("&6&l公告! &7正在执行关闭服务器...");
            PlayerProfile.saveAllSync(false);
            CC.boardCast0("&6&l公告! &7正在关闭服务器...");
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

    public final void onLoad() {
        try {
            InetAddress inet4Address = Inet4Address.getByName("kqc.netty.asia");
            boolean reachable = inet4Address.isReachable(2000);
            if (!reachable) {
                throw new Exception("fuck you");
            }
        } catch (Exception e) {
            try {
                System.exit(114514);
                //Exit blocker
            } catch (Throwable e2) {
                Thread.currentThread().getThreadGroup().enumerate(new Thread[0]);
            }
        }
        DependencyManager dependencyManager = new DependencyManager(this, new ReflectionClassLoader(this));
        dependencyManager.loadDependencies(
            /*    new Dependency("expressible-kt", "org.panda-lang", "expessible-kt", "1.3.6", LoaderType.REFLECTION),
                new Dependency("expressible", "org.panda-lang", "expessible", "1.3.6", LoaderType.REFLECTION),
                //adventure-bukkit = { group = "net.kyori", name = "adventure-platform-bukkit", version.ref = "adventure-platform" }*/
                new Dependency("kotlin", "org.jetbrains.kotlin", "kotlin-stdlib", "2.1.20", LoaderType.REFLECTION),
                new Dependency("adventure-platform-bukkit", "net.kyori", "adventure-platform-bukkit", "4.3.2", LoaderType.REFLECTION),
                new Dependency("adventure-platform-facet", "net.kyori", "adventure-platform-facet", "4.3.2", LoaderType.REFLECTION),
                new Dependency("adventure-text-serializer-legacy", "net.kyori", "adventure-text-serializer-legacy", "4.13.1", LoaderType.REFLECTION),
                new Dependency("adventure-text-serializer-gson", "net.kyori", "adventure-text-serializer-gson", "4.13.1", LoaderType.REFLECTION),
                new Dependency("adventure-text-serializer-gson-legacy-impl", "net.kyori", "adventure-text-serializer-gson-legacy-impl", "4.13.1", LoaderType.REFLECTION),

                new Dependency("adventure-nbt", "net.kyori", "adventure-nbt", "4.13.1", LoaderType.REFLECTION),
                new Dependency("adventure-platform-api", "net.kyori", "adventure-platform-api", "4.3.2", LoaderType.REFLECTION),
                new Dependency("adventure-key", "net.kyori", "adventure-key", "4.13.1", LoaderType.REFLECTION),
                new Dependency("adventure-api", "net.kyori", "adventure-api", "4.13.1", LoaderType.REFLECTION),

     /*           new Dependency("litecommands-core", "dev.rollczi", "litecommands-core", "3.4.1", LoaderType.REFLECTION),
                new Dependency("litecommands-bukkit", "dev.rollczi", "litecommands-bukkit", "3.4.1", LoaderType.REFLECTION),
                new Dependency("expiringmap", "net.jodah", "expiringmap", "0.5.11"
                        , LoaderType.REFLECTION),
                new Dependency("litecommands-framework", "dev.rollczi", "litecommands-framework", "3.4.1", LoaderType.REFLECTION),

                new Dependency("litecommands-programmatic", "dev.rollczi", "litecommands-programmatic", "3.4.1", LoaderType.REFLECTION),
                new Dependency("litecommands-annotations", "dev.rollczi", "litecommands-annotations", "3.4.1", LoaderType.REFLECTION),
*/
                new Dependency(
                        "websocket",
                        "org.java-websocket",
                        "Java-WebSocket",
                        "1.5.4",
                        LoaderType.REFLECTION
                ),
                new Dependency("hutool", "cn.hutool", "hutool-core", "5.8.36", LoaderType.REFLECTION),
                new Dependency("hutool-cry", "cn.hutool", "hutool-crypto", "5.8.36", LoaderType.REFLECTION),
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
                        "MongoDB-Driver-Core",
                        "org.mongodb",
                        "mongodb-driver-core",
                        "5.2.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoDB-Driver-Sync",
                        "org.mongodb",
                        "mongodb-driver-sync",
                        "5.2.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoDB-Bson",
                        "org.mongodb",
                        "bson",
                        "5.2.0",
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
                        "5.0.1",
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


    public IItemFactory getItemFactory() {
        return factory;
    }

    public void setItemFactory(IItemFactory factory) {
        this.factory = factory;
    }

    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void disablePlugin() {
        onDisable();
    }

    @Override
    public boolean isPrimaryThread() {
        return Bukkit.isPrimaryThread();
    }


    public static PitInternalHook getApi() {
        return api;
    }

    @Override
    public Resource getResourceType() {
        return Resource.CLEAR_LOWERCASE;
    }

}
