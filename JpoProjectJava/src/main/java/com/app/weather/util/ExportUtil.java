package com.app.weather.util;

import com.app.weather.model.WeatherData;
import com.app.weather.model.WeatherData.HourlyPoint;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportUtil {
    public static void exportToTxt(WeatherData data, String filename) {
        List<HourlyPoint> points = data.toHourlyPoints();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (HourlyPoint point : points) {
                bw.write(point.getTime() + ": " + point.getTemperature());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
