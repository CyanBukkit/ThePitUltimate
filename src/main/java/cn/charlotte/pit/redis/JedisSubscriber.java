package cn.charlotte.pit.redis;

import cn.charlotte.pit.util.chat.CC;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber extends JedisPubSub {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private final JedisHelper helper;
    private final Jedis jedis;

    private final IJedisSubscription subscription;
    private final Thread subscriptionThread;

    public JedisSubscriber(JedisHelper helper, IJedisSubscription subscription) {
        this.helper = helper;
        this.subscription = subscription;
        this.jedis = new Jedis();

        helper.attemptAuth(this.jedis);

        subscriptionThread = new Thread(() -> this.jedis.subscribe(this, "pit"));
        subscriptionThread.start();
    }

    public void cleanup() {
        if (subscriptionThread != null && subscriptionThread.isAlive()) {
            subscriptionThread.stop();
        }
        this.unsubscribe();
    }

    @Override
    public void onMessage(String channel, String message) {
        CC.boardCast("msg");
        try {
            JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
            this.subscription.handleMessage(channel, object);
        } catch (JsonParseException e) {
            //TODO better debug
            System.out.println("Received message that could not be parsed");
        }
    }

    public JedisHelper getHelper() {
        return this.helper;
    }

    public Jedis getJedis() {
        return this.jedis;
    }

    public IJedisSubscription getSubscription() {
        return this.subscription;
    }

    public Thread getSubscriptionThread() {
        return this.subscriptionThread;
    }
}
