package g62221.labyrinthe.model;

/**
 * Strategy interface for AI players.
 */
public interface Strategy {
    void playInsert(LabyrinthFacade facade);
    void playMove(LabyrinthFacade facade);
}