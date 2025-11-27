package g62221.labyrinthe.controller;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.view.MainView;

public class Controller {
    private final LabyrinthFacade facade;
    private MainView view;

    public Controller(LabyrinthFacade facade) {
        this.facade = facade;
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public void handleRotate() {
        facade.rotateExtraTile();
    }

    // Nouvelle méthode pour l'insertion manuelle via les boutons
    public void handleInsert(Direction dir, int index) {
        try {
            facade.insertTile(dir, index);
        } catch (Exception e) {
            if(view != null) view.showError(e.getMessage());
        }
    }

    // Méthode pour l'IA (toujours utile pour tester)
    public void handleAIPlay() {
        try {
            facade.playBot();
        } catch (Exception e) {
            if(view != null) view.showError(e.getMessage());
        }
    }

    public void handleMove(int row, int col) {
        try {
            facade.movePlayer(row, col);
        } catch (Exception e) {
            if(view != null) view.showError(e.getMessage());
        }
    }

    public void handleUndo() { facade.undo(); }
    public void handleRedo() { facade.redo(); }
}