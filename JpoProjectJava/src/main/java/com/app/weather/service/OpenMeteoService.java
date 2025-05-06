package com.app.weather.service;

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

    public OpenMeteoService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Optional<WeatherData> getForecast(Location loc) {
        String uri = String.format(
                "%s?latitude=%.6f&longitude=%.6f&hourly=temperature_2m,relativehumidity_2m",
                BASE_URL, loc.latitude(), loc.longitude());
        return sendRequest(uri);
    }

    public Optional<WeatherData> getHistoricalData(Location loc, LocalDate from, LocalDate to) {
        String uri = String.format(
                "%s/history?latitude=%.6f&longitude=%.6f&start_date=%s&end_date=%s&hourly=temperature_2m",
                BASE_URL, loc.latitude(), loc.longitude(), from, to);
        return sendRequest(uri);
    }

    private Optional<WeatherData> sendRequest(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .build();

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    WeatherData data = gson.fromJson(response.body(), WeatherData.class);
                    return Optional.of(data);
                }
            } catch (IOException | InterruptedException e) {
                // retry
            }
        }

        return Optional.empty();
    }
}
