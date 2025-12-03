package g62221.labyrinthe.controller;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.view.MainView;

/**
 * Controller component of the MVC architecture.
 * <p>
 * It acts as a bridge between the View (UI) and the Model (LabyrinthFacade).
 * It intercepts user actions from the View and triggers the appropriate logic in the Model.
 * </p>
 */
public class Controller {
    private final LabyrinthFacade facade;
    private MainView view;

    /**
     * Constructs a new Controller with the specified model facade.
     *
     * @param facade The facade to the game model.
     */
    public Controller(LabyrinthFacade facade) {
        this.facade = facade;
    }

    /**
     * Sets the view associated with this controller.
     * Necessary to display error messages back to the user.
     *
     * @param view The main view of the application.
     */
    public void setView(MainView view) {
        this.view = view;
    }

    /**
     * Handles the action of rotating the extra tile in hand.
     */
    public void handleRotate() {
        // Appelle la façade pour effectuer une rotation de 90 degrés sur la tuile bonus
        facade.rotateExtraTile();
    }

    /**
     * Handles the action of inserting a tile into the board.
     *
     * @param dir   The direction of insertion (UP, DOWN, LEFT, RIGHT).
     * @param index The index of the row or column.
     */
    public void handleInsert(Direction dir, int index) {
        try {
            // Tente d'insérer la tuile via la façade.
            // Le modèle se chargera de vérifier si le coup est valide.
            facade.insertTile(dir, index);
        } catch (Exception e) {
            // Si une erreur survient (ex: coup interdit anti-retour), on l'affiche à l'écran
            if (view != null) view.showError(e.getMessage());
        }
    }

    /**
     * Triggers the AI bot's turn logic.
     * This is typically called by the View's automated loop.
     */
    public void handleAIPlay() {
        try {
            // Demande à l'IA de calculer et jouer son meilleur coup
            facade.playBot();
        } catch (Exception e) {
            // Capture et affiche toute erreur survenue pendant le tour de l'IA
            if (view != null) view.showError(e.getMessage());
        }
    }

    /**
     * Handles the action of moving the player's pawn.
     *
     * @param row The target row index.
     * @param col The target column index.
     */
    public void handleMove(int row, int col) {
        try {
            // Tente de déplacer le joueur vers la case cliquée.
            // Le modèle lancera une exception si le chemin est bloqué par un mur.
            facade.movePlayer(row, col);
        } catch (Exception e) {
            // Affiche "Chemin bloqué" ou autre erreur directement sur l'interface
            if (view != null) view.showError(e.getMessage());
        }
    }

    /**
     * Undoes the last command executed (insertion or movement).
     */
    public void handleUndo() {
        // Annule la dernière action en dépilant la commande de l'historique
        facade.undo();
    }

    /**
     * Redoes the last undone command.
     */
    public void handleRedo() {
        // Refait l'action précédemment annulée
        facade.redo();
    }
}