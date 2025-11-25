package g62221.labyrinthe.model;

/**
 * Command to insert a tile.
 */
public class InsertTileCommand implements Command {
    private final Game game;
    private final Direction dir;
    private final int index;

    public InsertTileCommand(Game game, Direction dir, int index) {
        this.game = game;
        this.dir = dir;
        this.index = index;
    }

    @Override
    public void execute() {
        game.insertTile(dir, index);
    }

    @Override
    public void undo() {
        // Revert is complex: slide back opposite way.
        // For this project level, we force slide back and force state revert.
        // Note: Real player expulsion revert is hard, this is a simplified undo.
        game.getBoard().slide(dir.opposite(), index);
        game.forceState(Game.State.WAITING_FOR_SLIDE); // Helper needed in Game
        game.previousPlayer(); // Helper needed in Game
    }
}