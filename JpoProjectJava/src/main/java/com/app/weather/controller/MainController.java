package com.app.weather.controller;

import com.app.weather.cache.FileCacheClient;
import com.app.weather.cache.CacheClient;
import com.app.weather.model.Location;
import com.app.weather.model.WeatherData;
import com.app.weather.service.GeocodingService;
import com.app.weather.service.OpenMeteoService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class MainController {
    @FXML private RadioButton forecastRadio;
    @FXML private RadioButton historyRadio;
    @FXML private RadioButton cityRadio;
    @FXML private RadioButton coordsRadio;
    @FXML private HBox cityInputBox;
    @FXML private HBox coordsInputBox;
    @FXML private GridPane dateBox;
    @FXML private TextField cityField;
    @FXML private TextField latField;
    @FXML private TextField lonField;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private Button forecastBtn;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;

    private final GeocodingService geoService = new GeocodingService();
    private final OpenMeteoService weatherService;

    public MainController() {
        CacheClient<WeatherData> cache;
        try {
            cache = new FileCacheClient<>(WeatherData.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize cache", e);
        }
        this.weatherService = new OpenMeteoService(cache);
    }

    @FXML
    private void initialize() {
        // ToggleGroup for mode
        ToggleGroup modeGroup = new ToggleGroup();
        forecastRadio.setToggleGroup(modeGroup);
        historyRadio.setToggleGroup(modeGroup);
        modeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isHistory = historyRadio.isSelected();
            dateBox.setVisible(isHistory);
            forecastBtn.setText(isHistory ? "Show History" : "Show Forecast");
        });

        // ToggleGroup for location method
        ToggleGroup locGroup = new ToggleGroup();
        cityRadio.setToggleGroup(locGroup);
        coordsRadio.setToggleGroup(locGroup);
        locGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isCity = cityRadio.isSelected();
            cityInputBox.setVisible(isCity);
            coordsInputBox.setVisible(!isCity);
        });

        // Default values
        fromDate.setValue(LocalDate.now().minusDays(1));
        toDate.setValue(LocalDate.now());
        progressIndicator.setVisible(false);

        forecastBtn.setOnAction(e -> onForecast());
    }

    private void onForecast() {
        System.out.println("onForecast called");
        String cityOrCoords = cityRadio.isSelected()
                ? "City: " + cityField.getText()
                : "Coords: (" + latField.getText() + "," + lonField.getText() + ")";
        System.out.println(cityOrCoords);
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        boolean isHistory = historyRadio.isSelected();
        System.out.println("Mode: " + (isHistory ? "History" : "Forecast")
                + ", Date range: " + from + " to " + to);

        statusLabel.setText("Fetching location...");
        progressIndicator.setVisible(true);

        Optional<Location> locOpt;
        if (cityRadio.isSelected()) {
            locOpt = geoService.geocode(cityField.getText());
        } else {
            double lat = Double.parseDouble(latField.getText());
            double lon = Double.parseDouble(lonField.getText());
            locOpt = Optional.of(new Location("", lat, lon));
        }
        System.out.println("geocode returned: " + locOpt);

        if (locOpt.isEmpty()) {
            progressIndicator.setVisible(false);
            statusLabel.setText("Location not found");
            return;
        }

        statusLabel.setText(isHistory ? "Fetching historical data..." : "Fetching forecast...");
        Optional<WeatherData> dataOpt = isHistory
                ? weatherService.getHistoricalData(locOpt.get(), fromDate.getValue(), toDate.getValue())
                : weatherService.getForecast(locOpt.get());
        System.out.println("data returned: " + dataOpt
                + (dataOpt.isPresent() ? ", points: " + dataOpt.get().toHourlyPoints().size() : ""));

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
            Scene scene = new Scene(loader.load(), 1600, 900);
            scene.getStylesheets().add(
                    getClass().getResource("/styles/style.css").toExternalForm()
            );
            Stage stage = new Stage();
            stage.setTitle("Weather Data");
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