package cn.charlotte.pit.data.operator;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import io.papermc.paper.util.maplist.ObjectMapList;
import io.papermc.paper.util.maplist.SafeObjectMapList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
@ToString
public class PackedOperator {

    ThePit pit;
    ReentrantLock lock = new ReentrantLock();
    long lastHeartBeat = 0;

    Player lastBoundPlayer = null;
    @NotNull
    PlayerProfile profile = PlayerProfile.NONE_PROFILE;
    public PackedOperator(ThePit inst){
        this.pit = inst;
    }
    public PlayerProfile profile(){
        return profile;
    }
    public boolean isLoaded(){
        return profile != PlayerProfile.NONE_PROFILE && profile.isLoaded();
    }
    public void ifLoaded(Runnable runnable){
        if(isLoaded()){
            runnable.run();
        }
    }
    public void heartBeat(){
        this.lastHeartBeat = System.currentTimeMillis();
    }
    public void loadAs(UUID uuid,String name) {
        lastBoundPlayer = Bukkit.getPlayer(uuid);
        operations.add(() -> {
            loadAs0(uuid, name);
        });
        this.heartBeat();
    }
    public void loadAs(PlayerProfile profile) {

        if(profile == PlayerProfile.NONE_PROFILE){
            return;
        }
        lastBoundPlayer = Bukkit.getPlayer(profile.getPlayerUuid());
        this.profile = profile;
        this.heartBeat();
    }
    public void loadAs0(UUID uuid,String name){
        if(profile != PlayerProfile.NONE_PROFILE){
            return;
        }
        PlayerProfile rawProfile = PlayerProfile.loadPlayerProfileByUuid(uuid);
        if (rawProfile == null) {
            rawProfile = new PlayerProfile(uuid, name);
        } else {
            PlayerProfile.loadMail(rawProfile,uuid);
        }
        PlayerProfile.bootstrapProfile(rawProfile);
        profile = rawProfile;
    }

    SafeObjectMapList<Runnable> operations = new SafeObjectMapList<>(); //safer
    Set<Runnable> pendingExecuting = new CopyOnWriteArraySet<>();
    public boolean hasAnyOperation() {
        return !operations.isEmpty() && !this.pendingExecuting.isEmpty();
    }
    boolean fireExit = false;
    boolean quitFlag = false;
    public synchronized boolean save(boolean fireExit,boolean quitFlag) {
        if (System.currentTimeMillis() - lastHeartBeat > 1000) {
            this.fireExit = fireExit;
            if (this.fireExit) {
                if (!quitFlag) {
                    pending(prof -> prof.save(null));
                }
            }
        }
        if (quitFlag && !this.quitFlag) {
            pending(prof -> prof.save(null));
            this.quitFlag = true;
        }
        return true;
    }
    public void pendingIfLoaded(Consumer<PlayerProfile> profile){
        if(isLoaded()){
            pending(profile);
        }
    }
    public void pending(Consumer<PlayerProfile> profile){
        offerOperation(() -> {
            profile.accept(PackedOperator.this.profile);
        });
    }
    public void offerOperation(Runnable runnable) {
        operations.add(runnable);
    }
    public void tick() {
        if(lastBoundPlayer != null){
            if(isLoaded()){
                Player player = Bukkit.getPlayer(this.profile.getPlayerUuid());
                if(player != null){
                    this.lastBoundPlayer = player;
                }
            }
        }
        if (operations.isEmpty()) {
            return;
        }
        Runnable operation = EMPTY_RUNNABLE;
        for (Runnable next : operations) {
            if (!pendingExecuting.contains(next)) {
                operation = next;
                break;
            }
        }
        if(!pendingExecuting.contains(operation)) {
            pendingExecuting.add(operation);
            final Runnable operationFinaled = operation;
            Bukkit.getScheduler().runTaskAsynchronously(pit, () -> {
                long lastTime = System.currentTimeMillis();
                operationFinaled.run();
                operations.remove(operationFinaled);
                pendingExecuting.remove(operationFinaled);
                Bukkit.getLogger().info("A io operation was finished took " + (System.currentTimeMillis() - lastTime));
            });
        }
    }
    private static final Runnable EMPTY_RUNNABLE = () -> {};
    public UUID getUniqueId(){
        return this.profile.getPlayerUuid();
    }
    public void wipe(PlayerProfile wProfile){
        pending(i -> {
            Bukkit.getScheduler().runTask(pit, () -> {
                Bukkit.getPlayer(i.getPlayerUuid()).kickPlayer("working");
            });
            this.profile = wProfile;
        });
    }
    @SneakyThrows
    public void waitForLoad(){
        while (!this.isLoaded()) {
            Thread.onSpinWait();
        }
    }
    public void pendingUntilLoaded(Consumer<PlayerProfile> profileConsumer) {
        if(isLoaded()){
            this.pending(profileConsumer);
            return;
        }
        new BukkitRunnable() {
            public void run() {
                if (isLoaded()) {
                    this.cancel();
                    pendingIfLoaded(profileConsumer);
                }
            }
        }.runTaskTimerAsynchronously(pit,0,5);
    }
}
