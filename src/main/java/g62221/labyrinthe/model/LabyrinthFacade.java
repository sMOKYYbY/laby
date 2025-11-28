package g62221.labyrinthe.model;

import java.util.*;

/**
 * Facade pattern entry point. Provides a simplified interface to the game logic
 * for the Controller and View, and manages the AI logic.
 */
public class LabyrinthFacade extends Observable implements Observer {
    private final Game game;
    private final CommandManager commandManager;

    public LabyrinthFacade() {
        this.game = new Game();
        this.commandManager = new CommandManager();
        this.game.addObserver(this);
    }

    /**
     * Starts a new game.
     * @param nbPlayers Number of players.
     */
    public void startGame(int nbPlayers) {
        commandManager.clear();
        game.start(nbPlayers);
    }

    /**
     * Inserts a tile into the board.
     *
     * @param direction Direction of insertion.
     * @param index Row or column index.
     * @return true if successful, false if the move was forbidden (anti-return).
     */
    public boolean insertTile(Direction direction, int index) {
        try {
            Command cmd = new InsertTileCommand(game, direction, index);
            commandManager.execute(cmd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Moves the current player.
     *
     * @param row Target row.
     * @param col Target column.
     */
    public void movePlayer(int row, int col) {
        try {
            Position target = new Position(row, col);
            Command cmd = new MovePlayerCommand(game, target);
            commandManager.execute(cmd);
        } catch (Exception e) {
            // Error handling ignored for cleaner console output
        }
    }

    /**
     * Rotates the extra tile in hand.
     */
    public void rotateExtraTile() {
        game.getBoard().getExtraTile().rotate();
        notifyObservers();
    }

    public void undo() { commandManager.undo(); notifyObservers(); }
    public void redo() { commandManager.redo(); notifyObservers(); }

    /**
     * Executes the AI bot logic for the current turn.
     * Strategies include: finding the treasure, testing all insertions, or random move.
     */
    public void playBot() {
        if (!game.isCurrentPlayerBot()) return;

        String objective = getCurrentPlayerObjective();
        Position targetPos = (objective != null) ? findTreasurePosition(objective) : new Position(3,3);

        // Fallback if target is not on board (e.g. on extra tile)
        if (targetPos == null) {
            playRandomMove();
            return;
        }

        int[] indices = {1, 3, 5};
        Direction[] dirs = Direction.values();

        // 1. Simulation: Try all valid moves to reach target
        for (int idx : indices) {
            for (Direction dir : dirs) {
                boolean success = insertTile(dir, idx);
                if (success) {
                    Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
                    Set<Position> reachable = game.getBoard().getReachablePositions(botPos);

                    // Re-evaluate target pos as it might have shifted
                    Position currentTarget = (objective != null) ? findTreasurePosition(objective) : new Position(3,3);

                    if (currentTarget != null && reachable.contains(currentTarget)) {
                        movePlayer(currentTarget.row(), currentTarget.col());
                        return; // Winning move found
                    }
                    undo(); // Revert simulation
                }
            }
        }

        // 2. Fallback: Random move if no solution found
        playRandomMove();
    }

    private void playRandomMove() {
        Random rand = new Random();
        int[] indices = {1, 3, 5};

        boolean inserted = false;
        while (!inserted) {
            inserted = insertTile(Direction.values()[rand.nextInt(4)], indices[rand.nextInt(3)]);
        }

        Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
        List<Position> reachable = new ArrayList<>(game.getBoard().getReachablePositions(botPos));

        if (!reachable.isEmpty()) {
            Position dest = reachable.get(rand.nextInt(reachable.size()));
            movePlayer(dest.row(), dest.col());
        } else {
            movePlayer(botPos.row(), botPos.col());
        }
    }

    private Position findTreasurePosition(String treasureName) {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                Tile t = game.getBoard().getTile(r, c);
                if (t.hasTreasure() && t.getTreasure().equals(treasureName)) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    // --- Getters ---
    public boolean isCurrentPlayerBot() { return game.isCurrentPlayerBot(); }
    public Tile getTile(int r, int c) { return game.getBoard().getTile(r, c); }
    public Tile getExtraTile() { return game.getBoard().getExtraTile(); }
    public Position getPlayerPosition(int index) { return game.getPlayerPosition(index); }
    public int getCurrentPlayerIndex() { return game.getCurrentPlayerIndex(); }
    public Game.State getGameState() { return game.getState(); }
    public int getNbPlayers() { return game.getPlayersCount(); }

    public String getCurrentPlayerObjective() { return game.getPlayerCurrentObjective(game.getCurrentPlayerIndex()); }
    public String getPlayerCurrentObjective(int index) { return game.getPlayerCurrentObjective(index); }

    public int getCurrentPlayerCardsCount() { return game.getPlayerCardsCount(game.getCurrentPlayerIndex()); }
    public int getPlayerCardsCount(int index) { return game.getPlayerCardsCount(index); }

    public List<String> getPlayerFoundObjectives(int index) { return game.getPlayerFoundObjectives(index); }
    public int getWinnerId() { return (game.getWinner() != null) ? game.getWinner().getId() : -1; }
    public Direction getForbiddenDirection() { return game.getForbiddenDirection(); }
    public int getForbiddenIndex() { return game.getForbiddenIndex(); }

    @Override
    public void update() { notifyObservers(); }
}