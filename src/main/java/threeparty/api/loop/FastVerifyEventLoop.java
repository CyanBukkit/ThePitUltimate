package threeparty.api.loop;

import io.netty.util.concurrent.SingleThreadEventExecutor;
import threeparty.api.ThePitAdmin;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class FastVerifyEventLoop implements Closeable {
    ExecutorService localExecutor = Executors.newSingleThreadExecutor();
    List<Consumer<Object>> loops = new CopyOnWriteArrayList<>();
    ThePitAdmin admin;
    public FastVerifyEventLoop(ThePitAdmin admin){
        this.admin = admin;
    }
    public CompletableFuture<Object> loop(Object object) {
        CompletableFuture<Object> objectCompletableFuture = new CompletableFuture<>();
        localExecutor.execute(() -> {
            loops.forEach(i -> {
                i.accept(object);
            });
            objectCompletableFuture.complete(object);
        });
        return objectCompletableFuture;
    }

    public void inject(Consumer<Object> consumer) {
        if (loops.contains(consumer)) {
            throw new RuntimeException("inject loop failed because of the duplication of your consumer");
        }
        loops.add(consumer);
    }

    @Override
    public void close() throws IOException {
        admin.destroyEventLoop(this);
    }
}
