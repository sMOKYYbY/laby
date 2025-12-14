package g62221.labyrinthe;

/**
 * Wrapper entry point for the application.
 * <p>
 * This class serves as a workaround for JavaFX runtime issues when the application
 * is packaged as a "Fat JAR" or a native executable. It separates the main execution
 * flow from the JavaFX {@link javafx.application.Application} class loading, preventing
 * the "JavaFX runtime components are missing" error.
 * </p>
 */
public class Launcher {

    /**
     * Main method that simply delegates execution to the real application entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // C'est une astuce technique indispensable pour créer un exécutable (.exe).
        // Cette classe sert de "tremplin" : elle ne dépend pas de JavaFX, donc elle se charge,
        // puis elle appelle App.main() une fois que tout est prêt.
        App.main(args);
    }
}