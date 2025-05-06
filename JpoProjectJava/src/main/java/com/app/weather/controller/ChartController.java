package com.app.weather.controller;

import com.app.weather.model.WeatherData;
import com.app.weather.model.WeatherData.HourlyPoint;
import com.app.weather.util.ExportUtil;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.util.List;

public class ChartController {
    @FXML private LineChart<String, Number> chart;
    @FXML private Button exportBtn;
    private WeatherData data;

    public void setData(WeatherData data) {
        this.data = data;
        plot();
    }

    private void plot() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        List<HourlyPoint> points = data.toHourlyPoints();
        for (HourlyPoint point : points) {
            series.getData().add(
                    new XYChart.Data<>(point.getTime().toString(), point.getTemperature())
            );
        }
        chart.getData().add(series);
    }

    @FXML
    private void initialize() {
        exportBtn.setOnAction(e -> {
            ExportUtil.exportToTxt(data, "weather.txt");
            Stage stage = (Stage) exportBtn.getScene().getWindow();
            stage.close();
        });
    }
}
