package com.app.weather.service;

import com.app.weather.cache.CacheClient;
import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import com.google.gson.Gson;

import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

public class OpenMeteoService {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private final HttpClient client;
    private final Gson gson = new Gson();
    private final CacheClient<WeatherData> cacheClient;

    // Konstruktor pozwala wstrzyknąć klienta cache
    public OpenMeteoService(CacheClient<WeatherData> cacheClient) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.cacheClient = cacheClient;
    }

    public Optional<WeatherData> getForecast(Location loc) {
        String uri = String.format(
                "%s?latitude=%.6f&longitude=%.6f&hourly=temperature_2m,relativehumidity_2m,windspeed_10m,precipitation,pressure_msl",
                BASE_URL, loc.latitude(), loc.longitude());
        return fetchData(loc, null, null, uri);
    }

    public Optional<WeatherData> getHistoricalData(Location loc, LocalDate from, LocalDate to) {
        String uri = String.format(
                "%s?latitude=%.6f&longitude=%.6f&start_date=%s&end_date=%s&hourly=temperature_2m,relativehumidity_2m,windspeed_10m,precipitation,pressure_msl",
                BASE_URL, loc.latitude(), loc.longitude(), from, to);
        return fetchData(loc, from, to, uri);
    }

    private Optional<WeatherData> fetchData(Location loc, LocalDate from, LocalDate to, String uri) {
        // Budowanie klucza cache
        String key = loc.latitude() + "," + loc.longitude();
        if (from != null && to != null) {
            key += ":" + from + ":" + to;
        }
        System.out.println("[CACHE] Checking key: " + key);

        // Sprawdzenie cache
        Optional<WeatherData> cached = cacheClient.get(key);
        if (cached.isPresent()) {
            System.out.println("[CACHE] Hit! Returning cached data for " + key);
            return cached;
        }
        System.out.println("[CACHE] Miss! Calling HTTP for " + uri);

        // Wywołanie HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .build();

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println("[HTTP] Attempt " + attempt + ", status: " + response.statusCode());
                if (response.statusCode() == 200) {
                    WeatherData data = gson.fromJson(response.body(), WeatherData.class);
                    System.out.println("[HTTP] Parsed data points: " + data.toHourlyPoints().size());
                    // Zapis do cache
                    cacheClient.put(key, data);
                    System.out.println("[CACHE] Stored data under key: " + key);
                    return Optional.of(data);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("[HTTP] Error on attempt " + attempt + ": " + e.getMessage());
            }
        }

        System.out.println("[CACHE] No data available after retries for " + key);
        return Optional.empty();
    }
}