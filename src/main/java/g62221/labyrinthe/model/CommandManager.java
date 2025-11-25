package g62221.labyrinthe.model;

import java.util.Stack;

/**
 * Invoker managing Undo/Redo stacks.
 */
public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Executes a command and pushes it to history.
     * @param cmd the command to execute.
     */
    public void execute(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    /**
     * Undoes the last command.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    /**
     * Redoes the last undone command.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }

    /**
     * Clears history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}