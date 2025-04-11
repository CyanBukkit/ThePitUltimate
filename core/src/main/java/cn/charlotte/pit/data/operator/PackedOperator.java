package cn.charlotte.pit.data.operator;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

@ToString
public class PackedOperator implements IOperator {

    ThePit pit;
    //ReentrantLock lock = new ReentrantLock();
    //Didn't have any lock required
    long lastHeartBeat = 0;

    Player lastBoundPlayer = null;
    @NotNull
    PlayerProfile profile = PlayerProfile.NONE_PROFILE;

    public PackedOperator(ThePit inst) {
        this.pit = inst;
    }

    public PlayerProfile profile() {
        return profile;
    }

    public boolean isLoaded() {
        return profile != PlayerProfile.NONE_PROFILE && profile.isLoaded();
    }

    public void ifLoaded(Runnable runnable) {
        if (isLoaded()) {
            runnable.run();
        }
    }

    public void heartBeat() {
        Player player = Bukkit.getPlayer(profile.getPlayerUuid());
        if (player != null && player.isOnline()) {
            this.quitFlag = false;
            this.fireExit = false;
        }
        this.lastHeartBeat = System.currentTimeMillis();
    }

    public void loadAs(UUID uuid, String name) {
        lastBoundPlayer = Bukkit.getPlayer(uuid);
        synchronized (operations) {
            operations.add(() -> {
                loadAs0(uuid, name);
            });
        }
        this.heartBeat();
        ;
    }

    public void loadAs(PlayerProfile profile) {

        if (profile == PlayerProfile.NONE_PROFILE) {
            return;
        }
        lastBoundPlayer = Bukkit.getPlayer(profile.getPlayerUuid());
        this.profile = profile;
        this.heartBeat();
    }

    public void loadAs0(UUID uuid, String name) {
        if (profile != PlayerProfile.NONE_PROFILE) {
            return;
        }
        PlayerProfile rawProfile = PlayerProfile.loadPlayerProfileByUuid(uuid);
        if (rawProfile == null) {
            rawProfile = new PlayerProfile(uuid, name);
        } else {
            PlayerProfile.loadMail(rawProfile, uuid);
        }
        PlayerProfile.bootstrapProfile(rawProfile);
        profile = rawProfile;
    }

    final ObjectArrayList<Runnable> operations = new ObjectArrayList<>(); //safer
    Set<Runnable> pendingExecuting = new CopyOnWriteArraySet<>(); //正常情况下就一个

    public boolean hasAnyOperation() {
        synchronized (operations) {
            return !operations.isEmpty() && !this.pendingExecuting.isEmpty();
        }
    }

    boolean fireExit = false;
    boolean quitFlag = false;

    public synchronized boolean save(boolean fireExit, boolean quitFlag) {
        if (System.currentTimeMillis() - lastHeartBeat > 1000) {
            this.fireExit = fireExit;
            if (this.fireExit) {
                if (!quitFlag) {
                    pending(prof -> {
                        prof.disallowUnsafe();
                        prof.save(null);
                        prof.allow();
                    });
                }
            }
            return true;
        }
        if (quitFlag && !this.quitFlag) {
            pending(prof -> {
                PlayerProfile playerProfile = prof.disallowUnsafe();
                PlayerProfile save = playerProfile.save(null);
                pending(unk -> {
                    save.allow();
                });
            });
            this.quitFlag = true;
        }
        return true;
    }

    public void pendingIfLoaded(Consumer<PlayerProfile> profile) {
        if (isLoaded()) {
            pending(profile);
        }
    }

    public void pending(Consumer<PlayerProfile> profile) {
        offerOperation(() -> {
            profile.accept(PackedOperator.this.profile);
        });
    }

    public Promise promise(Consumer<PlayerProfile> profile) {
        Promise promise = new Promise();
        offerOperation(() -> {
            profile.accept(PackedOperator.this.profile);
            promise.ret();
        });
        return promise;
    }

    public void offerOperation(Runnable runnable) {
        synchronized (operations) {
            operations.add(runnable);
        }
    }

    public void tick() {
        if (lastBoundPlayer != null) {
            if (isLoaded()) {
                Player player = Bukkit.getPlayer(this.profile.getPlayerUuid());
                if (player != null && player.isOnline()) {
                    this.lastBoundPlayer = player;
                }
            }
        }
        synchronized (operations) {
            if (operations.isEmpty()) {
                return;
            }

            if (!pendingExecuting.isEmpty()) {
                return; //保持有序性。。。
            }

            Runnable operation = EMPTY_RUNNABLE;
            for (Runnable next : operations) {
                operation = next;
                break;
            }

            pendingExecuting.add(operation);
            final Runnable operationFinaled = operation;
            Bukkit.getScheduler().runTaskAsynchronously(pit, () -> {
                operationFinaled.run();
                operations.remove(operationFinaled);
                pendingExecuting.remove(operationFinaled);
            });
        }

    }

    private static final Runnable EMPTY_RUNNABLE = () -> {
    };

    public UUID getUniqueId() {
        return this.profile.getPlayerUuid();
    }

    public void wipe(PlayerProfile wProfile) {
        pending(i -> {
            Bukkit.getScheduler().runTask(pit, () -> {
                Bukkit.getPlayer(i.getPlayerUuid()).kickPlayer("working");
            });
            this.profile = wProfile;
        });
    }

    @SneakyThrows
    public void waitForLoad() {
        while (!this.isLoaded()) {
            Thread.onSpinWait();
        }
    }

    public Promise pendingUntilLoadedPromise(Consumer<PlayerProfile> profileConsumer) {
        Promise promise = new Promise();
        pendingUntilLoaded(prof -> {
            profileConsumer.accept(prof);
            promise.ret();
        });
        return promise;
    }

    public void pendingUntilLoaded(Consumer<PlayerProfile> profileConsumer) {
        if (isLoaded()) {
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
        }.runTaskTimerAsynchronously(pit, 0, 5);
    }
}
