package cn.charlotte.pit.redis;

import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.Set;

public class JedisHelper {

    private final JedisPool pool;
    private final JedisCredentials credentials;
    private final JedisPublisher publisher;
    private final Set<JedisSubscriber> subscribers = new HashSet<>();

    public JedisHelper(JedisCredentials credentials) {
        this.credentials = credentials;
        this.pool = new JedisPool(this.getCredentials().getAddress(), this.credentials.getPort());

        try (Jedis jedis = this.pool.getResource()) {
            attemptAuth(jedis);
            this.publisher = new JedisPublisher(this);
        }
    }

    public void close() {
        subscribers.stream()
                .filter(JedisPubSub::isSubscribed)
                .forEach(JedisPubSub::unsubscribe);
        if (!this.pool.isClosed()) {
            this.pool.close();
        }
    }

    public boolean isActive() {
        return this.pool != null && !this.pool.isClosed();
    }

    public void attemptAuth(Jedis jedis) {
        if (this.credentials.isAuth()) {
            jedis.auth(this.credentials.getPassword()); // TODO: Check this status code, and potentially block responses to avoid code errors.
        }
    }

    public void write(Enum payloadID, JsonObject data, String channel) {
        JsonObject object = new JsonObject();

        object.addProperty("payload", payloadID.name());
        object.add("data", data == null ? new JsonObject() : data);

        publisher.write(channel, object);
    }

    public <T> T runCommand(IRedisCommand<T> redisCommand) {
        Jedis jedis = this.pool.getResource();
        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();

            if (jedis != null) {
                jedis.close();
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    public JedisPool getPool() {
        return this.pool;
    }

    public JedisCredentials getCredentials() {
        return this.credentials;
    }

    public JedisPublisher getPublisher() {
        return this.publisher;
    }

    public Set<JedisSubscriber> getSubscribers() {
        return this.subscribers;
    }
}
