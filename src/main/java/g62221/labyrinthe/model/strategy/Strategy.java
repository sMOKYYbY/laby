package g62221.labyrinthe.model.strategy;

import g62221.labyrinthe.model.facade.LabyrinthFacade;

/**
 * Interface defining the behavior of an AI bot.
 * <p>
 * This interface is the core of the <b>Strategy Design Pattern</b> used in this project.
 * It encapsulates the logic for an AI player's turn, allowing different algorithms
 * (behaviors/difficulty levels) to be swapped dynamically without changing the game code.
 * </p>
 */
public interface Strategy {

    /**
     * Executes the logic for the tile insertion phase.
     * <p>
     * The strategy implementation should decide which row/column to slide
     * and call the appropriate method on the facade.
     * </p>
     *
     * @param facade The game facade to interact with the model.
     */
    // Phase 1 du tour : L'IA calcule et joue l'insertion de la tuile.
    // Cela permet de définir "comment" l'IA choisit de modifier le labyrinthe.
    void playInsert(LabyrinthFacade facade);

    /**
     * Executes the logic for the pawn movement phase.
     * <p>
     * The strategy implementation should decide the best target position
     * for the pawn and call the move method on the facade.
     * </p>
     *
     * @param facade The game facade to interact with the model.
     */
    // Phase 2 du tour : L'IA calcule et joue le déplacement de son pion.
    // On peut avoir ici une IA aléatoire ou une IA intelligente (BFS) selon la classe qui implémente cette interface.
    void playMove(LabyrinthFacade facade);
}