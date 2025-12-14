package g62221.labyrinthe.model.command;

import java.util.Stack;

/**
 * Invoker class responsible for managing the execution history of commands.
 * <p>
 * This class implements the Invoker part of the Command design pattern.
 * It maintains two stacks (Undo and Redo) to track the history of actions performed
 * in the game, allowing users to revert or re-apply changes.
 * </p>
 */
public class CommandManager {
    // Stack to store executed commands that can be undone
    private final Stack<Command> undoStack = new Stack<>();
    // Stack to store undone commands that can be redone
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Executes a command and adds it to the history.
     * <p>
     * This method performs the command's action, pushes it onto the undo stack,
     * and clears the redo stack (as a new action invalidates the redo history).
     * </p>
     *
     * @param cmd The command to execute.
     */
    public void execute(Command cmd) {
        // Exécute l'action encapsulée dans la commande (ex: insérer une tuile)
        cmd.execute();

        // Ajoute cette commande au sommet de la pile d'annulation pour pouvoir revenir en arrière
        undoStack.push(cmd);

        // Vide la pile de rétablissement car une nouvelle action "écrase" le futur possible
        redoStack.clear();
    }

    /**
     * Reverts the last executed command.
     * <p>
     * Moves the command from the undo stack to the redo stack and calls its {@code undo()} method.
     * Does nothing if the undo stack is empty.
     * </p>
     */
    public void undo() {
        // Vérifie s'il y a des actions dans l'historique à annuler
        if (!undoStack.isEmpty()) {
            // Récupère la dernière commande jouée
            Command cmd = undoStack.pop();

            // Appelle la méthode d'annulation spécifique de la commande
            cmd.undo();

            // Place la commande dans la pile "Redo" au cas où on voudrait la refaire
            redoStack.push(cmd);
        }
    }

    /**
     * Re-executes the last undone command.
     * <p>
     * Moves the command from the redo stack back to the undo stack and calls its {@code execute()} method.
     * Does nothing if the redo stack is empty.
     * </p>
     */
    public void redo() {
        // Vérifie s'il y a des actions annulées qui peuvent être rétablies
        if (!redoStack.isEmpty()) {
            // Récupère la dernière commande annulée
            Command cmd = redoStack.pop();

            // Ré-exécute l'action
            cmd.execute();

            // Remet la commande dans l'historique principal
            undoStack.push(cmd);
        }
    }

    /**
     * Clears the entire command history.
     * <p>
     * Resets both the undo and redo stacks. Useful when starting a new game.
     * </p>
     */
    public void clear() {
        // Vide complètement les historiques pour repartir à neuf
        undoStack.clear();
        redoStack.clear();
    }
}