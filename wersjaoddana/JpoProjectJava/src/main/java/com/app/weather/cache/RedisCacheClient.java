package com.app.weather.cache;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Optional;

public class RedisCacheClient<T> implements CacheClient<T> {
    private final Class<T> type;
    private final JedisPool pool = new JedisPool("localhost", 6379);
    private final Gson gson = new Gson();
    private final int ttl;

    public RedisCacheClient(Class<T> type, int ttlSeconds) {
        this.type = type;
        this.ttl  = ttlSeconds;
    }

    @Override
    public Optional<T> get(String key) {
        try (Jedis j = pool.getResource()) {
            String json = j.get(key);
            if (json == null) return Optional.empty();
            return Optional.of(gson.fromJson(json, type));
        }
    }

    @Override
    public void put(String key, T value) {
        try (Jedis j = pool.getResource()) {
            String json = gson.toJson(value);
            if (ttl > 0) {
                // tu ustawiamy TTL
                j.setex(key, ttl, json);
            } else {
                j.set(key, json);
            }
        }
    }
}
