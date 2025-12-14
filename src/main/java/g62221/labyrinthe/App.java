package g62221.labyrinthe;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.facade.LabyrinthFacade;
import g62221.labyrinthe.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main entry point for the Labyrinth application.
 * <p>
 * This class extends {@link Application} to manage the JavaFX lifecycle.
 * Its primary responsibility is to bootstrap the MVC architecture by instantiating
 * the Model (Facade), View, and Controller, and linking them together.
 * </p>
 */
public class App extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method initializes the primary stage, sets up the custom window style,
     * creates the MVC components, and establishes the bidirectional communication
     * between the View and the Controller.
     * </p>
     *
     * @param primaryStage The primary window of the application provided by the JavaFX platform.
     */
    @Override
    public void start(Stage primaryStage) {
        // 1. Configuration de la fenêtre
        // On retire les bordures Windows classiques (Croix, Réduire, Titre) pour avoir un look 100% personnalisé.
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // 2. Instanciation des composants MVC (Le "Cœur" de l'architecture)

        // Le Modèle (La Façade qui cache la complexité du jeu)
        LabyrinthFacade facade = new LabyrinthFacade();

        // La Vue (L'interface graphique qui observe le Modèle)
        MainView view = new MainView(primaryStage, facade);

        // Le Contrôleur (Le chef d'orchestre qui reçoit les inputs de la Vue)
        Controller controller = new Controller(facade);

        // 3. Liaison (Binding) des composants
        // C'est ici qu'on connecte les fils :
        // - La Vue doit connaître le Controller pour lui envoyer les clics (ex: "J'ai cliqué sur la flèche").
        view.setController(controller);

        // - Le Controller doit connaître la Vue pour afficher des erreurs (ex: "Mouvement impossible !").
        controller.setView(view);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}