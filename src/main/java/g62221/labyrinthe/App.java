package g62221.labyrinthe;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize Model
        LabyrinthFacade facade = new LabyrinthFacade();

        // 2. Initialize View
        MainView view = new MainView(primaryStage, facade);

        // 3. Initialize Controller
        Controller controller = new Controller(facade);

        // 4. Bind Controller to View
        view.setController(controller);
    }

    /**
     * Entry point.
     * @param args arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}