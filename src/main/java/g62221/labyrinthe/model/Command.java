package g62221.labyrinthe.model;

/**
 * Interface for the Command pattern.
 */
public interface Command {
    /**
     * Executes the action.
     */
    void execute();

    /**
     * Reverts the action.
     */
    void undo();
}