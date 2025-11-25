package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Core game logic handling turns, players, and rules.
 */
public class Game extends Observable {

    private final Board board;
    private final List<Position> players; // Simplified: List of positions for players 0, 1, 2, 3
    private int currentPlayerIndex;

    public enum State { WAITING_FOR_SLIDE, WAITING_FOR_MOVE }
    private State currentState;

    /**
     * Constructor.
     */
    public Game() {
        this.board = new Board();
        this.players = new ArrayList<>();
        this.currentState = State.WAITING_FOR_SLIDE;
    }

    /**
     * Starts the game.
     * @param nbPlayers number of players.
     */
    public void start(int nbPlayers) {
        players.clear();
        // Setup starting positions (Corners)
        Position[] starts = { new Position(0,0), new Position(0,6), new Position(6,0), new Position(6,6) };
        for (int i = 0; i < nbPlayers; i++) {
            players.add(starts[i]);
        }
        currentPlayerIndex = 0;
        currentState = State.WAITING_FOR_SLIDE;
        notifyObservers();
    }

    /**
     * Inserts a tile.
     * @param dir direction.
     * @param index index.
     */
    public void insertTile(Direction dir, int index) {
        if (currentState != State.WAITING_FOR_SLIDE) {
            throw new IllegalStateException("You must move your player, not slide a tile.");
        }

        board.slide(dir, index);

        // Handle player expulsion (if player is on the edge being pushed)
        handlePlayerExpulsion(dir, index);

        currentState = State.WAITING_FOR_MOVE;
        notifyObservers();
    }

    /**
     * Moves current player.
     * @param destination target.
     */
    public void movePlayer(Position destination) {
        if (currentState != State.WAITING_FOR_MOVE) {
            throw new IllegalStateException("You must insert a tile first.");
        }

        Position currentPos = players.get(currentPlayerIndex);

        // Use Board BFS to validate path
        if (!board.getReachablePositions(currentPos).contains(destination)) {
            throw new IllegalArgumentException("Path is blocked.");
        }

        players.set(currentPlayerIndex, destination);

        // End of turn -> Next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentState = State.WAITING_FOR_SLIDE;

        notifyObservers();
    }

    private void handlePlayerExpulsion(Direction dir, int index) {
        for (int i = 0; i < players.size(); i++) {
            Position p = players.get(i);
            // Logic to wrap around player if pushed off board
            // Example for pushing RIGHT on row 'index':
            if (dir == Direction.RIGHT && p.row() == index) {
                int newCol = p.col() + 1;
                if (newCol > 6) newCol = 0; // Wrap to left
                players.set(i, new Position(p.row(), newCol));
            }
            // Similar logic needed for LEFT, UP, DOWN...
            else if (dir == Direction.LEFT && p.row() == index) {
                int newCol = p.col() - 1;
                if (newCol < 0) newCol = 6;
                players.set(i, new Position(p.row(), newCol));
            } else if (dir == Direction.DOWN && p.col() == index) {
                int newRow = p.row() + 1;
                if (newRow > 6) newRow = 0;
                players.set(i, new Position(newRow, p.col()));
            } else if (dir == Direction.UP && p.col() == index) {
                int newRow = p.row() - 1;
                if (newRow < 0) newRow = 6;
                players.set(i, new Position(newRow, p.col()));
            }
        }
    }

    // Getters for Facade
    public Board getBoard() { return board; }
    public Position getPlayerPosition(int index) { return players.get(index); }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public State getState() { return currentState; }
    // --- Helper methods for Undo/Redo commands ---

    public int getPlayersCount() { return players.size(); }

    public int getPreviousPlayerIndex() {
        return (currentPlayerIndex - 1 + players.size()) % players.size();
    }

    public void previousPlayer() {
        currentPlayerIndex = getPreviousPlayerIndex();
    }

    public void forceState(State state) {
        this.currentState = state;
    }

    public void teleportPlayer(int playerIndex, Position pos) {
        players.set(playerIndex, pos);
    }
}