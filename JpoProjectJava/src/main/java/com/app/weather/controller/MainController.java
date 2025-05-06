package com.app.weather.controller;

import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import com.app.weather.service.GeocodingService;
import com.app.weather.service.OpenMeteoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.time.LocalDate;
import java.util.Optional;

public class MainController {
    @FXML private TextField cityField;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private Button forecastBtn;
    @FXML private Label statusLabel;

    private final GeocodingService geoService = new GeocodingService();
    private final OpenMeteoService weatherService = new OpenMeteoService();

    @FXML
    private void initialize() {
        forecastBtn.setOnAction(e -> onForecast());
    }

    private void onForecast() {
        String city = cityField.getText();
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        statusLabel.setText("Fetching location...");
        Optional<Location> locOpt = geoService.geocode(city);
        if (locOpt.isEmpty()) {
            statusLabel.setText("Location not found");
            return;
        }
        statusLabel.setText("Fetching weather data...");
        Optional<WeatherData> dataOpt = weatherService.getHistoricalData(locOpt.get(), from, to);
        if (dataOpt.isEmpty()) {
            statusLabel.setText("No data");
            return;
        }
        showChart(dataOpt.get());
    }

    private void showChart(WeatherData weatherData) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/chart.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            ChartController ctrl = loader.getController();
            ctrl.setData(weatherData);
            stage.show();
        } catch (Exception ex) {
            statusLabel.setText("Error opening chart");
        }
    }
}