package g62221.labyrinthe.model;


/**
 * Command to move a player.
 */
public class MovePlayerCommand implements Command {
    private final Game game;
    private final Position startPos;
    private final Position endPos;

    public MovePlayerCommand(Game game, Position endPos) {
        this.game = game;
        this.endPos = endPos;
        this.startPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
    }

    @Override
    public void execute() {
        game.movePlayer(endPos);
    }

    @Override
    public void undo() {
        // Teleport player back and revert turn
        game.teleportPlayer(game.getPreviousPlayerIndex(), startPos);
        game.forceState(Game.State.WAITING_FOR_MOVE);
        game.previousPlayer();
    }
}