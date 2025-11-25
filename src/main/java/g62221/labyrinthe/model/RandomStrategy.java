package g62221.labyrinthe.model;

import java.util.List;
import java.util.Random;

/**
 * Random strategy implementation.
 */
public class RandomStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public void playInsert(LabyrinthFacade facade) {
        Direction[] dirs = Direction.values();
        Direction d = dirs[random.nextInt(dirs.length)];

        // Pick a moveable index (1, 3, 5)
        int[] indices = {1, 3, 5};
        int idx = indices[random.nextInt(indices.length)];

        facade.insertTile(d, idx);
    }

    @Override
    public void playMove(LabyrinthFacade facade) {
        // Move to a random reachable position (or stay put)
        // Since facade doesn't expose BFS directly for simplicity, 
        // AI will just try to stay put or move to center for now.
        // Or simply: Do nothing (pass turn).
        int currentPlayer = facade.getCurrentPlayerIndex();
        Position current = facade.getPlayerPosition(currentPlayer);
        facade.movePlayer(current.row(), current.col());
    }
}