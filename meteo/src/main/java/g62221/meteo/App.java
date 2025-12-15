package g62221.meteo;

import g62221.meteo.controller.WeatherController;
import g62221.meteo.model.WeatherApi;
import g62221.meteo.view.InputView;
import g62221.meteo.view.WeatherView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main entry point of the JavaFX weather application.
 * <p>
 * This class initializes and assembles the MVC (Model-View-Controller) components:
 * <ul>
 *   <li><strong>Model:</strong> {@link WeatherApi} - weather data service</li>
 *   <li><strong>Views:</strong> {@link InputView} and {@link WeatherView} - user interface</li>
 *   <li><strong>Controller:</strong> {@link WeatherController} - coordination logic</li>
 * </ul>
 * </p>
 * <p>
 * The application implements the Observer pattern for clear separation of responsibilities
 * and better code maintainability.
 * </p>
 *
 * @author g62221
 * @version 3.0
 */
public class App extends Application {

    /**
     * JavaFX entry point called at application startup.
     * <p>
     * Initializes MVC components, configures the scene, and displays the main window.
     * </p>
     *
     * @param stage the main window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        // Initialize views
        InputView inputView = new InputView();
        WeatherView weatherView = new WeatherView();

        // Initialize model (service)
        WeatherApi api = new WeatherApi();

        // Initialize controller (links model and views)
        WeatherController controller = new WeatherController(api, inputView, weatherView);

        // Build main layout
        VBox root = new VBox(20, weatherView.getNode(), inputView.getNode());
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Configure scene
        Scene scene = new Scene(root, 460, 460);
        stage.setTitle("Weather");
        stage.setScene(scene);
        stage.show();

        // Activate events via controller
        controller.bind();
    }

    /**
     * Standard Java entry point of the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
