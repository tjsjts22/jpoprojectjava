package com.app.weather.controller;

import com.app.weather.model.WeatherData;
import com.app.weather.model.WeatherData.HourlyPoint;
import com.app.weather.util.ExportUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class ChartController {
    @FXML private ComboBox<String> metricSelector;
    @FXML private LineChart<String, Number> chart;
    @FXML private Button exportBtn;
    private List<HourlyPoint> points;

    @FXML
    private void initialize() {
        // Ustaw domyślne metryki
        metricSelector.setItems(FXCollections.observableArrayList(
                "Temperature", "Humidity", "Wind Speed", "Precipitation", "Pressure"
        ));
        metricSelector.getSelectionModel().selectFirst();

        metricSelector.setOnAction(e -> updateChart());

        exportBtn.setOnAction(e -> {
            ExportUtil.exportToTxt(points, metricSelector.getValue(), "weather.txt");
            Stage stage = (Stage) exportBtn.getScene().getWindow();
            stage.close();
        });
    }

    public void setData(WeatherData data) {
        this.points = data.toHourlyPoints();
        updateChart();
    }

    /**
     * Przerysowuje wykres, pokazując tylko wybraną serię
     */
    private void updateChart() {
        chart.getData().clear();
        String metric = metricSelector.getValue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(metric);

        for (HourlyPoint p : points) {
            String t = p.getTime().toString();
            Number value;
            switch (metric) {
                case "Humidity":      value = p.getHumidity();    break;
                case "Wind Speed":    value = p.getWindSpeed();   break;
                case "Precipitation": value = p.getPrecipitation();break;
                case "Pressure":      value = p.getPressure();    break;
                default:              value = p.getTemperature(); break;
            }
            series.getData().add(new XYChart.Data<>(t, value));
        }

        chart.getData().add(series);
    }
}
