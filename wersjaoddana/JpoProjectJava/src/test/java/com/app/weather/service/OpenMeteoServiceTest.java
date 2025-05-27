package com.app.weather.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import java.util.Optional;

public class OpenMeteoServiceTest {
    @Test
    void testGetForecast() {
        OpenMeteoService svc = new OpenMeteoService();
        Location loc = new Location("Warsaw", 52.2297, 21.0122);
        Optional<WeatherData> data = svc.getForecast(loc);
        assertTrue(data.isPresent(), "Oczekiwano danych pogodowych");
    }
}