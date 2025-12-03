package g62221.labyrinthe.model;

/**
 * Interface defining the contract for the Command design pattern.
 * <p>
 * This interface ensures that every action in the game (like moving a player or inserting a tile)
 * can be executed and reversed (undone). This is the foundation of the Undo/Redo feature.
 * </p>
 */
public interface Command {

    /**
     * Executes the encapsulated action.
     * This method contains the logic to perform the specific modification on the game model.
     */
    void execute(); // Exécute l'action (ex: jouer un coup)

    /**
     * Reverts the effects of the executed action.
     * This method restores the game model to its state prior to the execution of this command.
     */
    void undo(); // Annule l'action pour revenir à l'état précédent (ex: Ctrl+Z)
}