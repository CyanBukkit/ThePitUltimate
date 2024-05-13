package cn.charlotte.pit.redis;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.PitConfig;
import org.redisson.Redisson;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @Author: EmptyIrony
 * @Date: 2021/1/17 18:27
 */
public class RedisClient {
    private RedissonClient client;

    public RedisClient() {
    }

    public void init() {
        Config config = new Config();
        PitConfig pitConfig = ThePit.getInstance().getPitConfig();
        config.useSingleServer().setAddress(pitConfig.getRedisAddress() + ":" + pitConfig.getRedisPort());
        this.client = Redisson.create(config);

    }

    public RReadWriteLock getLock(String name) {
        return this.client.getReadWriteLock(name);
    }

    public RedissonClient getClient() {
        return this.client;
    }
}
