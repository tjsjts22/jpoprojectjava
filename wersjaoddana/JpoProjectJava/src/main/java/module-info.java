module weather.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires redis.clients.jedis;
    requires java.net.http;

    opens com.app.weather to javafx.graphics, javafx.fxml;
    opens com.app.weather.controller to javafx.fxml;
    opens com.app.weather.model to com.google.gson;
}