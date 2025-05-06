package com.app.weather.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherData {
    public HourlyData hourly;

    /** Konwertuje surowe dane na listę (czas, temperatura). */
    public List<HourlyPoint> toHourlyPoints() {
        List<HourlyPoint> list = new ArrayList<>();
        int size = Math.min(hourly.time.size(), hourly.temperature.size());
        for (int i = 0; i < size; i++) {
            LocalDateTime t = LocalDateTime.parse(hourly.time.get(i));
            double temp = hourly.temperature.get(i);
            list.add(new HourlyPoint(t, temp));
        }
        return list;
    }

    public static class HourlyData {
        public List<String> time;
        @SerializedName("temperature_2m")
        public List<Double> temperature;
    }

    public static class HourlyPoint {
        private final LocalDateTime time;
        private final double temperature;
        public HourlyPoint(LocalDateTime time, double temperature) {
            this.time = time;
            this.temperature = temperature;
        }
        public LocalDateTime getTime()     { return time; }
        public double        getTemperature() { return temperature; }
    }
}
