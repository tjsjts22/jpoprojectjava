package com.app.weather.service;

import com.app.weather.model.Location;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.time.Duration;


public class GeocodingService {
    private static final String GEOCODE_URL =
            "https://nominatim.openstreetmap.org/search?format=json&q=%s";
    private final HttpClient client;
    private final Gson gson = new Gson();

    public GeocodingService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Optional<Location> geocode(String cityName) {
        String uri = String.format(GEOCODE_URL, cityName.replace(" ", "%20"));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("User-Agent", "weather-app/1.0")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonArray results = JsonParser.parseString(response.body()).getAsJsonArray();
                if (results.size() > 0) {
                    JsonElement first = results.get(0);
                    double lat = first.getAsJsonObject().get("lat").getAsDouble();
                    double lon = first.getAsJsonObject().get("lon").getAsDouble();
                    return Optional.of(new Location(cityName, lat, lon));
                }
            }
        } catch (IOException | InterruptedException e) {
            // handle exception
        }
        return Optional.empty();
    }
}