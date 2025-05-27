package com.app.weather.util;

import com.app.weather.model.WeatherData;
import com.app.weather.model.WeatherData.HourlyPoint;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportUtil {

    /**
     * Eksportuje wszystkie metryki (time, temp, humidity, wind, precip, pressure)
     * z obiektu WeatherData do pliku CSV.
     */
    public static void exportToTxt(WeatherData data, String filename) {
        List<HourlyPoint> points = data.toHourlyPoints();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Time,Temperature,Humidity,WindSpeed,Precipitation,Pressure");
            bw.newLine();
            for (HourlyPoint p : points) {
                bw.write(p.getTime() + ","
                        + p.getTemperature() + ","
                        + p.getHumidity() + ","
                        + p.getWindSpeed() + ","
                        + p.getPrecipitation() + ","
                        + p.getPressure());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Eksportuje tylko jedną wybraną metrykę z listy punktów do pliku CSV.
     * @param points Lista punktów z wszystkimi metrykami.
     * @param metric Nazwa metryki: "Temperature", "Humidity", "Wind Speed", "Precipitation" lub "Pressure".
     * @param filename Ścieżka do pliku wyjściowego.
     */
    public static void exportToTxt(List<HourlyPoint> points, String metric, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Time," + metric);
            bw.newLine();
            for (HourlyPoint p : points) {
                String value;
                switch (metric) {
                    case "Humidity":      value = String.valueOf(p.getHumidity());     break;
                    case "Wind Speed":    value = String.valueOf(p.getWindSpeed());    break;
                    case "Precipitation": value = String.valueOf(p.getPrecipitation()); break;
                    case "Pressure":      value = String.valueOf(p.getPressure());     break;
                    default:              value = String.valueOf(p.getTemperature());  break;
                }
                bw.write(p.getTime() + "," + value);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
