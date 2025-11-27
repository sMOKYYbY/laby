package g62221.labyrinthe.model;

import java.util.Stack;

public class Player {
    private final int id;
    private final Position startPosition;
    private Position currentPosition;
    private final Stack<String> objectives; // Pile de cartes (noms des trésors)
    private String currentObjective; // Objectif actuel visible

    public Player(int id, Position startPosition) {
        this.id = id;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        this.objectives = new Stack<>();
    }

    public void setObjectives(Stack<String> cards) {
        this.objectives.addAll(cards);
        revealNextObjective();
    }

    /**
     * Révèle le prochain objectif si la pile n'est pas vide.
     */
    public void revealNextObjective() {
        if (!objectives.isEmpty()) {
            this.currentObjective = objectives.pop();
        } else {
            this.currentObjective = null; // Plus d'objectif = doit retourner au départ
        }
    }

    public boolean hasFinishedObjectives() {
        return objectives.isEmpty() && currentObjective == null;
    }

    public Position getPosition() { return currentPosition; }
    public void setPosition(Position pos) { this.currentPosition = pos; }
    public Position getStartPosition() { return startPosition; }
    public String getCurrentObjective() { return currentObjective; }
    public int getCardsRemaining() { return objectives.size() + (currentObjective != null ? 1 : 0); }
    public int getId() { return id; }
}