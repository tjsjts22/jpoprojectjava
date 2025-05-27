package com.app.weather.cache;

import java.util.Optional;

public interface CacheClient<T> {
    Optional<T> get(String key);
    void put(String key, T value);
}