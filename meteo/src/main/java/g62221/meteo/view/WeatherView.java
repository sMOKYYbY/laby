package g62221.meteo.view;

import g62221.meteo.model.Observer;
import g62221.meteo.model.WeatherData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * JavaFX view displaying weather information (city and temperatures).
 * <p>
 * This class implements the Observer pattern to automatically receive
 * weather data updates from the model ({@link g62221.meteo.model.WeatherApi}).
 * </p>
 * <p>
 * The interface includes:
 * <ul>
 *   <li>City name in black</li>
 *   <li>Maximum temperature in red</li>
 *   <li>Minimum temperature in blue</li>
 *   <li>A static weather icon</li>
 * </ul>
 * </p>
 *
 * @author g62221
 * @version 3.0
 * @see Observer
 */
public class WeatherView implements Observer {

    /** Label displaying the city name */
    private final Label cityLabel = new Label("--");

    /** Label displaying the maximum temperature */
    private final Label maxLabel = new Label("--°");

    /** Label displaying the minimum temperature */
    private final Label minLabel = new Label("--°");

    /** Weather icon */
    private final ImageView icon = new ImageView();

    /** Main container of the view */
    private final VBox root = new VBox(20);

    /**
     * {@inheritDoc}
     * <p>
     * This method uses {@code Platform.runLater()} to ensure that user interface
     * updates are performed on the JavaFX thread, even if the notification
     * comes from a background thread.
     * </p>
     */
    @Override
    public void updateWeatherObject(WeatherData data) {
        Platform.runLater(() -> {
            setCity(data.city());
            setTemps(data.tempMin(), data.tempMax());
        });
    }

    /**
     * Constructs a new weather view with initialized graphical components.
     * <p>
     * Configures styles, fonts, icon, and layout of visual elements.
     * </p>
     */
    public WeatherView() {
        cityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        cityLabel.setStyle("-fx-text-fill: black;");

        maxLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        maxLabel.setStyle("-fx-text-fill: red;");

        minLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        minLabel.setStyle("-fx-text-fill: blue;");

        icon.setImage(new Image(getClass().getResourceAsStream("/icons/weather.png")));
        icon.setFitWidth(100);
        icon.setFitHeight(100);

        HBox temps = new HBox(10, maxLabel, minLabel);
        temps.setAlignment(Pos.CENTER);

        root.getChildren().addAll(temps, icon, cityLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
    }

    /**
     * Returns the root JavaFX node of this view for inclusion in a scene.
     *
     * @return the root container of type {@link Node}
     */
    public Node getNode() {
        return root;
    }

    /**
     * Sets the city name to display.
     *
     * @param city the city name
     */
    public void setCity(String city) {
        cityLabel.setText(city);
    }

    /**
     * Sets the minimum and maximum temperatures to display.
     * <p>
     * If a value is {@code Double.NaN}, displays "--°" instead.
     * </p>
     *
     * @param tmin the minimum temperature in degrees Celsius
     * @param tmax the maximum temperature in degrees Celsius
     */
    public void setTemps(double tmin, double tmax) {
        if (Double.isNaN(tmax)) {
            maxLabel.setText("max: --°");
        } else {
            maxLabel.setText("max: " + tmax + "°");
        }

        if (Double.isNaN(tmin)) {
            minLabel.setText("min: --°");
        } else {
            minLabel.setText("min: " + tmin + "°");
        }
    }

    /**
     * Resets all display fields to their default values ("--").
     */
    public void reset() {
        cityLabel.setText("--");
        maxLabel.setText("--°");
        minLabel.setText("--°");
    }
}
