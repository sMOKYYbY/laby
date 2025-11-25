package g62221.labyrinthe.controller;

import g62221.labyrinthe.model.LabyrinthFacade;

/**
 * Controller.
 */
public class Controller {
    private final LabyrinthFacade facade;

    public Controller(LabyrinthFacade facade) {
        this.facade = facade;
    }

    public void handleRotate() {
        facade.rotateExtraTile();
    }

    public void handleInsert() {
        // Trigger Bot move for insertion phase
        facade.playBot();
    }

    public void handleMove(int row, int col) {
        facade.movePlayer(row, col);
    }

    public void handleUndo() { facade.undo(); }
    public void handleRedo() { facade.redo(); }
}