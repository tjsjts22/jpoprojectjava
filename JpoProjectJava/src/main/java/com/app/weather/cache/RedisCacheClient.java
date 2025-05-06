package com.app.weather.cache;

import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import java.util.Optional;

public class RedisCacheClient<T> implements CacheClient<T> {
    private final Jedis jedis = new Jedis("localhost");
    private final Gson gson = new Gson();
    private final Class<T> type;

    public RedisCacheClient(Class<T> type) { this.type = type; }

    @Override
    public Optional<T> get(String key) {
        String json = jedis.get(key);
        return json == null ? Optional.empty() : Optional.of(gson.fromJson(json, type));
    }

    @Override
    public void put(String key, T value) {
        jedis.set(key, gson.toJson(value));
    }
}