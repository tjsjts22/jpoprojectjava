package com.app.weather.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherData {
    public HourlyData hourly;

    public List<HourlyPoint> toHourlyPoints() {
        List<HourlyPoint> list = new ArrayList<>();
        int n = hourly.time.size();
        for (int i = 0; i < n; i++) {
            LocalDateTime t = LocalDateTime.parse(hourly.time.get(i));
            double temp = hourly.temperature.get(i);
            double hum = hourly.relativeHumidity.get(i);
            double wind = hourly.windSpeed.get(i);
            double precip = hourly.precipitation.get(i);
            double pres = hourly.pressure.get(i);
            list.add(new HourlyPoint(t, temp, hum, wind, precip, pres));
        }
        return list;
    }

    public static class HourlyData {
        public List<String> time;
        @SerializedName("temperature_2m")
        public List<Double> temperature;
        @SerializedName("relativehumidity_2m")
        public List<Double> relativeHumidity;
        @SerializedName("windspeed_10m")
        public List<Double> windSpeed;
        public List<Double> precipitation;
        @SerializedName("pressure_msl")
        public List<Double> pressure;
    }

    public static class HourlyPoint {
        private final LocalDateTime time;
        private final double temperature;
        private final double humidity;
        private final double windSpeed;
        private final double precipitation;
        private final double pressure;

        public HourlyPoint(LocalDateTime time, double temperature, double humidity,
                           double windSpeed, double precipitation, double pressure) {
            this.time = time;
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.precipitation = precipitation;
            this.pressure = pressure;
        }
        public LocalDateTime getTime()      { return time; }
        public double        getTemperature() { return temperature; }
        public double        getHumidity()    { return humidity; }
        public double        getWindSpeed()   { return windSpeed; }
        public double        getPrecipitation() { return precipitation; }
        public double        getPressure()    { return pressure; }
    }
}