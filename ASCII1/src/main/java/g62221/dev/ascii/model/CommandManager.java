package g62221.dev.ascii.model;

import java.util.Stack;

/**
 * Gère l’historique des commandes selon le patron Command (undo/redo).
 *
 * Maintient deux piles :
 * - undoStack : commandes déjà effectuées et pouvant être annulées
 * - redoStack : commandes annulées et pouvant être refaites
 * Toutes les opérations du modèle passent par une commande déposée ici.
 */
public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Exécute une commande et l’ajoute à l’historique. Vide la pile redo.
     *
     * @param command la commande à exécuter
     */
    public void doCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Annule la dernière commande exécutée.
     * Si aucune commande n’a été effectuée, ne fait rien.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.unexecute();
            redoStack.push(command);
        }
    }

    /**
     * Re-exécute la dernière commande annulée.
     * Si aucune commande n’a été annulée, ne fait rien.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}
