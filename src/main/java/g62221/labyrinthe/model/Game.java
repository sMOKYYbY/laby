package g62221.labyrinthe.model;

import java.util.*;

/**
 * Core game logic class. Manages the board, players, turns, game state, and rules.
 * Extends {@link Observable} to notify views of state changes.
 */
public class Game extends Observable {

    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private Player winner;

    /**
     * Represents the possible states of a turn.
     */
    public enum State { WAITING_FOR_SLIDE, WAITING_FOR_MOVE, GAME_OVER }
    private State currentState;

    // Stores the last move to enforce the "Anti-Return" rule.
    private Direction forbiddenDirection = null;
    private int forbiddenIndex = -1;

    /**
     * Constructs a new Game instance.
     */
    public Game() {
        this.board = new Board();
        this.players = new ArrayList<>();
        this.currentState = State.WAITING_FOR_SLIDE;
    }

    /**
     * Starts a new game with the specified number of players.
     * Resets the board, players, and distributes objective cards.
     *
     * @param nbPlayers The number of players (2 to 4).
     */
    public void start(int nbPlayers) {
        players.clear();
        winner = null;
        forbiddenDirection = null;
        forbiddenIndex = -1;

        // 1. Create players at starting corners
        Position[] starts = { new Position(0,0), new Position(0,6), new Position(6,0), new Position(6,6) };
        for (int i = 0; i < nbPlayers; i++) {
            players.add(new Player(i, starts[i]));
        }

        // 2. Distribute objective cards
        distributeCards();

        currentPlayerIndex = 0;
        currentState = State.WAITING_FOR_SLIDE;
        notifyObservers();
    }

    /**
     * Checks if the current player is an AI bot.
     * Convention: Player 0 is Human, others are Bots.
     *
     * @return true if the current player is a bot.
     */
    public boolean isCurrentPlayerBot() {
        return currentPlayerIndex != 0;
    }

    /**
     * Distributes the 24 treasure cards randomly and equally among players.
     */
    private void distributeCards() {
        List<String> allTreasures = new ArrayList<>(Arrays.asList(
                "goal_bat", "goal_butteryfly", "goal_dragon", "goal_ghost", "goal_ghost2", "goal_hibou",
                "goal_insecte", "goal_lezard", "goal_mouse", "goal_pig", "goal_spider", "goal_witch",
                "goal_book", "goal_candleholder", "goal_coffre", "goal_crown", "goal_helmet", "goal_keys",
                "goal_map", "goal_money", "goal_ring", "goal_saphir", "goal_skull", "goal_sword"
        ));
        Collections.shuffle(allTreasures);

        int cardsPerPlayer = allTreasures.size() / players.size();
        int deckIndex = 0;

        for (Player p : players) {
            Stack<String> hand = new Stack<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                hand.push(allTreasures.get(deckIndex++));
            }
            p.setObjectives(hand);
        }
    }

    /**
     * Inserts the extra tile into the board.
     * Applies the Anti-Return rule to prevent reversing the previous move immediately.
     *
     * @param dir   The direction of insertion.
     * @param index The row or column index.
     * @throws IllegalArgumentException if the move violates the anti-return rule or game phase.
     */
    public void insertTile(Direction dir, int index) {
        if (currentState != State.WAITING_FOR_SLIDE) return;

        // Anti-Return Rule Validation
        if (dir == forbiddenDirection && index == forbiddenIndex) {
            throw new IllegalArgumentException("Forbidden move! You cannot reverse the previous slide.");
        }

        board.slide(dir, index);
        handlePlayerExpulsion(dir, index);

        // Update forbidden move for next player
        this.forbiddenDirection = dir.opposite();
        this.forbiddenIndex = index;

        currentState = State.WAITING_FOR_MOVE;
        notifyObservers();
    }

    /**
     * Moves the current player to the destination if the path is valid.
     * Checks for objective completion and victory conditions.
     *
     * @param destination The target position.
     * @throws IllegalArgumentException if the path is blocked.
     */
    public void movePlayer(Position destination) {
        if (currentState != State.WAITING_FOR_MOVE) return;

        Player currentP = players.get(currentPlayerIndex);

        if (!board.getReachablePositions(currentP.getPosition()).contains(destination)) {
            throw new IllegalArgumentException("Path is blocked!");
        }

        // Move player
        currentP.setPosition(destination);

        // Logic: Check Objective and Victory
        checkObjective(currentP);

        if (checkVictory(currentP)) {
            currentState = State.GAME_OVER;
            this.winner = currentP;
        } else {
            // Next turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentState = State.WAITING_FOR_SLIDE;
        }

        notifyObservers();
    }

    /**
     * Checks if the player has reached their current objective.
     * If so, marks it as found and reveals the next one.
     */
    private void checkObjective(Player p) {
        String target = p.getCurrentObjective();
        Tile currentTile = board.getTile(p.getPosition().row(), p.getPosition().col());

        if (target != null && currentTile.hasTreasure() && target.equals(currentTile.getTreasure())) {
            p.objectiveFound();
        }
    }

    /**
     * Checks if the player has won.
     * Condition: All objectives found AND returned to start position.
     */
    private boolean checkVictory(Player p) {
        return p.hasFinishedObjectives() && p.getPosition().equals(p.getStartPosition());
    }

    /**
     * Handles the logic when a player is pushed off the board by a tile insertion.
     * The player wraps around to the opposite side.
     */
    private void handlePlayerExpulsion(Direction dir, int index) {
        for (Player p : players) {
            Position pos = p.getPosition();
            if (dir == Direction.RIGHT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() + 1) % 7));
            } else if (dir == Direction.LEFT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() - 1 + 7) % 7));
            } else if (dir == Direction.DOWN && pos.col() == index) {
                p.setPosition(new Position((pos.row() + 1) % 7, pos.col()));
            } else if (dir == Direction.UP && pos.col() == index) {
                p.setPosition(new Position((pos.row() - 1 + 7) % 7, pos.col()));
            }
        }
    }

    // --- Getters ---

    public Board getBoard() { return board; }
    public Position getPlayerPosition(int index) { return players.get(index).getPosition(); }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public State getState() { return currentState; }
    public int getPlayersCount() { return players.size(); }

    public String getPlayerCurrentObjective(int index) { return players.get(index).getCurrentObjective(); }
    public int getPlayerCardsCount(int index) { return players.get(index).getCardsRemaining(); }
    public List<String> getPlayerFoundObjectives(int index) { return players.get(index).getFoundObjectives(); }
    public Player getWinner() { return winner; }
    public Direction getForbiddenDirection() { return forbiddenDirection; }
    public int getForbiddenIndex() { return forbiddenIndex; }

    // --- Undo/Redo Helpers ---

    /**
     * Calculates the index of the previous player.
     * @return The previous player index.
     */
    public int getPreviousPlayerIndex() {
        return (currentPlayerIndex - 1 + players.size()) % players.size();
    }

    /**
     * Reverts the turn to the previous player.
     */
    public void previousPlayer() {
        this.currentPlayerIndex = getPreviousPlayerIndex();
    }

    public void forceState(State state) { this.currentState = state; }

    public void teleportPlayer(int index, Position pos) {
        players.get(index).setPosition(pos);
    }
}