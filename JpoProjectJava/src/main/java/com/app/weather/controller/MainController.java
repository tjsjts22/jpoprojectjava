package com.app.weather.controller;

import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import com.app.weather.service.GeocodingService;
import com.app.weather.service.OpenMeteoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class MainController {
    @FXML
    private TextField cityField;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private Button forecastBtn;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label statusLabel;

    private final GeocodingService geoService = new GeocodingService();
    private final OpenMeteoService weatherService = new OpenMeteoService();

    @FXML
    private void initialize() {
        fromDate.setValue(LocalDate.now().minusDays(1));
        toDate.setValue(LocalDate.now());
        progressIndicator.setVisible(false);
        forecastBtn.setOnAction(e -> onForecast());
    }

    private void onForecast() {
        System.out.println("onForecast called");
        String city = cityField.getText();
        System.out.println("City entered: " + city);
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        System.out.println("Date range: " + from + " to " + to);

        statusLabel.setText("Fetching location...");
        progressIndicator.setVisible(true);

        Optional<Location> locOpt = geoService.geocode(city);
        System.out.println("geocode returned: " + locOpt);

        if (locOpt.isEmpty()) {
            progressIndicator.setVisible(false);
            statusLabel.setText("Location not found");
            return;
        }

        statusLabel.setText("Fetching weather data...");
        Optional<WeatherData> dataOpt = weatherService.getHistoricalData(locOpt.get(), from, to);
        System.out.println("getHistoricalData returned: " + dataOpt);
        dataOpt.ifPresent(data -> System.out.println("Data points: " + data.toHourlyPoints().size()));

        progressIndicator.setVisible(false);
        if (dataOpt.isEmpty()) {
            statusLabel.setText("No data");
            return;
        }

        showChart(dataOpt.get());
    }

    private void showChart(WeatherData weatherData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chart.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/styles/style.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Weather History");
            stage.setScene(scene);

            ChartController controller = loader.getController();
            controller.setData(weatherData);
            stage.show();
        } catch (Exception ex) {
            progressIndicator.setVisible(false);
            statusLabel.setText("Error opening chart");
            ex.printStackTrace();
        }
    }
}