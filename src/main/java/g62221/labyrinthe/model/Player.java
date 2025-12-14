package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents a player in the game.
 * <p>
 * This class manages the player's state, including their position on the board,
 * their secret stack of objective cards, and the treasures they have already collected.
 * </p>
 */
public class Player {
    private final int id;
    private final Position startPosition;
    private Position currentPosition;
    private final Stack<String> objectives;
    private String currentObjective;
    private final List<String> foundObjectives;

    /**
     * Constructs a new player.
     *
     * @param id            The unique identifier for the player.
     * @param startPosition The starting coordinates on the board.
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
     * <p>
     * The top card is immediately popped to become the current objective.
     * </p>
     *
     * @param cards The stack of treasure names to find.
     */
    public void setObjectives(Stack<String> cards) {
        this.objectives.addAll(cards);
        // On définit immédiatement le premier objectif à trouver en piochant le dessus du paquet
        if (!objectives.isEmpty()) {
            this.currentObjective = objectives.pop();
        }
    }

    /**
     * Marks the current objective as found and reveals the next one.
     * <p>
     * If the stack is empty after finding the current objective, the current objective becomes null,
     * indicating the player must now return to their start position.
     * </p>
     */
    public void objectiveFound() {
        // Ajout de l'objectif actuel à la liste des succès ("J'ai trouvé !")
        if (this.currentObjective != null) {
            this.foundObjectives.add(this.currentObjective);
        }

        // Passage à la carte suivante ou fin de la chasse aux trésors
        if (!objectives.isEmpty()) {
            this.currentObjective = objectives.pop();
        } else {
            this.currentObjective = null; // Plus d'objectifs, il faut rentrer à la base !
        }
    }

    // --- Memento Pattern for Undo/Redo ---

    /**
     * Immutable record representing a snapshot of the player's data at a specific time.
     * Used for saving and restoring state (Undo/Redo).
     */
    public record PlayerState(
            String currentObj,
            List<String> stackContent,
            List<String> foundContent
    ) {}

    /**
     * Creates a defensive copy of the player's current state.
     *
     * @return A {@link PlayerState} containing copies of the objectives and found items.
     */
    public PlayerState saveState() {
        // Création d'une sauvegarde (Copie défensive des listes pour éviter les références partagées)
        // C'est indispensable pour que le Undo ne soit pas affecté par les modifications futures.
        return new PlayerState(
                this.currentObjective,
                new ArrayList<>(this.objectives),
                new ArrayList<>(this.foundObjectives)
        );
    }

    /**
     * Restores the player's state from a saved snapshot.
     *
     * @param state The state to restore.
     */
    public void restoreState(PlayerState state) {
        // Restauration complète des données depuis la sauvegarde
        this.currentObjective = state.currentObj();

        this.objectives.clear();
        this.objectives.addAll(state.stackContent());

        this.foundObjectives.clear();
        this.foundObjectives.addAll(state.foundContent());
    }

    /**
     * Checks if the player has collected all their assigned treasures.
     *
     * @return true if the objective stack is empty and there is no active objective.
     */
    public boolean hasFinishedObjectives() {
        // Le joueur a fini sa mission si sa pile est vide ET qu'il n'a plus d'objectif en cours
        return objectives.isEmpty() && currentObjective == null;
    }

    // --- Getters and Setters ---

    public Position getPosition() { return currentPosition; }

    /**
     * Updates the player's position on the board.
     * @param pos The new coordinates.
     */
    public void setPosition(Position pos) { this.currentPosition = pos; }

    public Position getStartPosition() { return startPosition; }

    public String getCurrentObjective() { return currentObjective; }

    /**
     * Calculates the total number of cards remaining to be found.
     * @return The count of remaining objectives (stack + current).
     */
    public int getCardsRemaining() {
        return objectives.size() + (currentObjective != null ? 1 : 0);
    }

    public int getId() { return id; }

    public List<String> getFoundObjectives() { return foundObjectives; }
}