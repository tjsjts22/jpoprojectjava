package com.app.weather.cache;

import com.google.gson.Gson;
import java.io.*;
import java.nio.file.*;
import java.util.Optional;

public class FileCacheClient<T> implements CacheClient<T> {
    private final Path dir = Paths.get("cache");
    private final Gson gson = new Gson();
    private final Class<T> type;

    public FileCacheClient(Class<T> type) throws IOException {
        this.type = type;
        Files.createDirectories(dir);
    }

    @Override
    public Optional<T> get(String key) {
        Path file = dir.resolve(key + ".json");
        if (!Files.exists(file)) return Optional.empty();
        try (Reader r = Files.newBufferedReader(file)) {
            return Optional.of(gson.fromJson(r, type));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, T value) {
        Path file = dir.resolve(key + ".json");
        try (Writer w = Files.newBufferedWriter(file)) {
            gson.toJson(value, w);
        } catch (IOException ignored) {}
    }
}