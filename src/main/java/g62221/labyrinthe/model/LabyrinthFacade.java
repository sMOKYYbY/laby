package g62221.labyrinthe.model;

/**
 * Main entry point for the Model. Encapsulates game logic and patterns.
 * Delegates actions to Game and manages history via CommandManager.
 */
public class LabyrinthFacade extends Observable implements Observer {
    private final Game game;
    private final CommandManager commandManager;

    /**
     * Constructor.
     */
    public LabyrinthFacade() {
        this.game = new Game();
        this.commandManager = new CommandManager();
        this.game.addObserver(this); // Listen to Game changes

    }

    /**
     * Starts the game.
     * @param nbPlayers number of players.
     */
    public void startGame(int nbPlayers) {
        commandManager.clear();
        game.start(nbPlayers);
        game.debugBoard();
    }

    /**
     * Inserts the extra tile (Command Pattern).
     * @param direction direction of insertion.
     * @param index row or column index.
     */
    public void insertTile(Direction direction, int index) {
        // Validation is done inside Game, but Command encapsulates the call
        try {
            Command cmd = new InsertTileCommand(game, direction, index);
            commandManager.execute(cmd);
        } catch (Exception e) {
            // If move is illegal (wrong phase), we don't execute command
            System.err.println(e.getMessage());
        }
    }

    /**
     * Moves the player (Command Pattern).
     * @param row target row.
     * @param col target col.
     */
    public void movePlayer(int row, int col) {
        try {
            Position target = new Position(row, col);
            Command cmd = new MovePlayerCommand(game, target);
            commandManager.execute(cmd);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Rotates the extra tile.
     */
    public void rotateExtraTile() {
        game.getBoard().getExtraTile().rotate();
        notifyObservers();
    }

    /**
     * Play a turn with AI (Strategy Pattern).
     */
    public void playBot() {
        Strategy strategy = new RandomStrategy();
        // 1. Slide
        if (game.getState() == Game.State.WAITING_FOR_SLIDE) {
            strategy.playInsert(this);
        }
        // 2. Move (AI re-checks state after slide)
        if (game.getState() == Game.State.WAITING_FOR_MOVE) {
            strategy.playMove(this);
        }
    }

    public void undo() { commandManager.undo(); notifyObservers(); }
    public void redo() { commandManager.redo(); notifyObservers(); }

    // --- Getters for View ---

    public Tile getTile(int r, int c) { return game.getBoard().getTile(r, c); }
    public Tile getExtraTile() { return game.getBoard().getExtraTile(); }
    public Position getPlayerPosition(int index) { return game.getPlayerPosition(index); }
    public int getCurrentPlayerIndex() { return game.getCurrentPlayerIndex(); }
    public Game.State getGameState() { return game.getState(); }
    public int getNbPlayers() { return game.getPlayersCount(); }
    public String getCurrentPlayerObjective() {
        return game.getPlayerCurrentObjective(game.getCurrentPlayerIndex());
    }

    public int getCurrentPlayerCardsCount() {
        return game.getPlayerCardsCount(game.getCurrentPlayerIndex());
    }

    public int getWinnerId() {
        return (game.getWinner() != null) ? game.getWinner().getId() : -1;
    }

    @Override
    public void update() {
        notifyObservers();
    }
}