package g62221.meteo.controller;

import g62221.meteo.model.WeatherApi;
import g62221.meteo.model.WeatherException;
import g62221.meteo.view.InputView;
import g62221.meteo.view.WeatherView;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;

/**
 * MVC controller managing interactions between views and the weather model.
 * <p>
 * This controller:
 * <ul>
 *   <li>Registers the view as an observer of the model</li>
 *   <li>Binds view events (search button click)</li>
 *   <li>Launches weather data requests in a separate thread</li>
 *   <li>Handles errors and displays information messages</li>
 * </ul>
 * </p>
 * <p>
 * Thanks to the Observer pattern, the controller does not need to manually
 * update the view: it automatically receives notifications from the model.
 * </p>
 *
 * @author g62221
 * @version 3.0
 */
public class WeatherController {

    /** Weather data retrieval service (model) */
    private final WeatherApi api;

    /** View for entering search parameters */
    private final InputView input;

    /** View for displaying weather results */
    private final WeatherView view;

    /**
     * Constructs a new controller by linking the model and views.
     * <p>
     * Automatically registers the weather view as an observer of the model
     * to receive data change notifications.
     * </p>
     *
     * @param api the weather data retrieval service
     * @param input the view for entering parameters
     * @param view the view for displaying results
     */
    public WeatherController(WeatherApi api, InputView input, WeatherView view) {
        this.api = api;
        this.input = input;
        this.view = view;
        this.api.registerObserver(view);
    }

    /**
     * Binds the search button to the weather data retrieval action.
     * <p>
     * Must be called after controller construction to activate
     * user interactions.
     * </p>
     */
    public void bind() {
        input.getSearchButton().setOnAction(e -> onSearch());
    }

    /**
     * Handles the search action triggered by the button click.
     * <p>
     * Performs the following operations:
     * <ol>
     *   <li>Validates user inputs</li>
     *   <li>Resets the display</li>
     *   <li>Launches an asynchronous request in a separate thread</li>
     *   <li>Handles errors with information message display</li>
     * </ol>
     * </p>
     * <p>
     * The view update is automatic via the Observer pattern:
     * the model notifies the view as soon as data is retrieved.
     * </p>
     */
    private void onSearch() {
        String city = input.getCity();
        LocalDate date = input.getDate();

        if (city == null || city.isBlank() || date == null) {
            showInfo("Please enter a city and select a date.");
            view.setCity(city);
            view.setTemps(Double.NaN, Double.NaN);
            return;
        }

        view.setCity(city);
        view.setTemps(Double.NaN, Double.NaN); // Reset before search

        // Asynchronous request to avoid blocking the user interface
        new Thread(() -> {
            try {
                api.fetch(city, date); // The view will be notified automatically
            } catch (WeatherException ex) {
                Platform.runLater(() -> showInfo(ex.getMessage()));
            }
        }).start();
    }

    /**
     * Displays an information dialog box with the specified message.
     *
     * @param msg the message to display to the user
     */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
