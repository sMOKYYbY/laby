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
        LabyrinthFacade facade = new LabyrinthFacade();
        MainView view = new MainView(primaryStage, facade);
        Controller controller = new Controller(facade);

        // Connexion double sens
        view.setController(controller);
        controller.setView(view); // Important pour afficher les erreurs !
    }

    /**
     * Entry point.
     * @param args arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}