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
        // 1. On annule le glissement du plateau
        game.getBoard().slide(dir.opposite(), index);

        // 2. On revient à l'état "Attente d'insertion"
        game.forceState(Game.State.WAITING_FOR_SLIDE);


    }
}