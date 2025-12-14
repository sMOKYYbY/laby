package g62221.labyrinthe.model.strategy;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.facade.LabyrinthFacade;
import g62221.labyrinthe.model.Position;

import java.util.Random;

/**
 * Implementation of a random strategy for AI players.
 * <p>
 * This strategy makes decisions purely by chance, without considering the game state
 * or objectives. It is suitable for an "Easy" difficulty level or for testing purposes.
 * </p>
 */
public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    /**
     * Executes a random tile insertion.
     * <p>
     * Chooses a random direction and a random moveable row/column index (1, 3, or 5).
     * </p>
     *
     * @param facade The interface to interact with the game model.
     */
    @Override
    public void playInsert(LabyrinthFacade facade) {
        Direction[] dirs = Direction.values();
        // Choix d'une direction au hasard (Haut, Bas, Gauche, Droite)
        Direction d = dirs[random.nextInt(dirs.length)];

        // Choix d'une ligne ou colonne mobile (indices impairs uniquement : 1, 3, 5)
        int[] indices = {1, 3, 5};
        int idx = indices[random.nextInt(indices.length)];

        // Exécution de l'insertion via la façade
        facade.insertTile(d, idx);
    }

    /**
     * Executes a move for the AI player.
     * <p>
     * In this basic implementation, the AI simply stays in its current position to pass its turn.
     * A more advanced version would use pathfinding to move towards a reachable tile.
     * </p>
     *
     * @param facade The interface to interact with the game model.
     */
    @Override
    public void playMove(LabyrinthFacade facade) {
        // Récupération de la position actuelle du bot
        int currentPlayer = facade.getCurrentPlayerIndex();
        Position current = facade.getPlayerPosition(currentPlayer);

        // Pour cette stratégie aléatoire basique, le bot décide de ne pas bouger
        // Il valide simplement sa position actuelle pour finir son tour.
        // (Note : Pour une vraie IA, on utiliserait ici un algo pour choisir une case voisine)
        facade.movePlayer(current.row(), current.col());
    }
}