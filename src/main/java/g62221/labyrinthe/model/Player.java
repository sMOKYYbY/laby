package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents a player in the game.
 * Manages position, objective cards, and collected treasures.
 */
public class Player {
    private final int id;
    private final Position startPosition;
    private Position currentPosition;
    private final Stack<String> objectives;
    private String currentObjective;
    private final List<String> foundObjectives;

    /**
     * Creates a new player.
     *
     * @param id The player's ID.
     * @param startPosition The starting position on the board.
     */
    public Player(int id, Position startPosition) {
        this.id = id;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        this.objectives = new Stack<>();
        this.foundObjectives = new ArrayList<>();
    }

    /**
     * Assigns a stack of objective cards to the player.
     * The top card becomes the current objective.
     *
     * @param cards The stack of treasure names.
     */
    public void setObjectives(Stack<String> cards) {
        this.objectives.addAll(cards);
        if (!objectives.isEmpty()) {
            this.currentObjective = objectives.pop();
        }
    }

    /**
     * Marks the current objective as found.
     * Adds it to the found list and reveals the next objective from the stack.
     */
    public void objectiveFound() {
        if (this.currentObjective != null) {
            this.foundObjectives.add(this.currentObjective);
        }

        if (!objectives.isEmpty()) {
            this.currentObjective = objectives.pop();
        } else {
            this.currentObjective = null; // No more objectives, must return to start.
        }
    }

    /**
     * Checks if the player has found all their objectives.
     *
     * @return true if the objective stack is empty and current objective is null.
     */
    public boolean hasFinishedObjectives() {
        return objectives.isEmpty() && currentObjective == null;
    }

    public Position getPosition() { return currentPosition; }
    public void setPosition(Position pos) { this.currentPosition = pos; }
    public Position getStartPosition() { return startPosition; }
    public String getCurrentObjective() { return currentObjective; }

    /**
     * Gets the count of remaining cards (stack + current).
     * @return The number of cards left.
     */
    public int getCardsRemaining() { return objectives.size() + (currentObjective != null ? 1 : 0); }

    public int getId() { return id; }
    public List<String> getFoundObjectives() { return foundObjectives; }
}