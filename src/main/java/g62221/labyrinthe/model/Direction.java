package g62221.labyrinthe.model;

/**
 * Enumeration representing the four cardinal directions used for movement and orientation.
 * <p>
 * Each direction is associated with a row delta and a column delta,
 * facilitating calculations on the 2D grid (row-major order).
 * </p>
 */
public enum Direction {
    /**
     * Direction pointing upwards (decreases row index).
     */
    UP(-1, 0),

    /**
     * Direction pointing downwards (increases row index).
     */
    DOWN(1, 0),

    /**
     * Direction pointing left (decreases column index).
     */
    LEFT(0, -1),

    /**
     * Direction pointing right (increases column index).
     */
    RIGHT(0, 1);

    /**
     * The change in the row index when moving in this direction.
     */
    // Delta ligne : -1 pour monter, +1 pour descendre (l'axe Y est inversé en informatique)
    private final int deltaRow;

    /**
     * The change in the column index when moving in this direction.
     */
    // Delta colonne : -1 pour gauche, +1 pour droite
    private final int deltaCol;

    /**
     * Constructor for the direction enum.
     *
     * @param deltaRow The change in the row index (vertical movement).
     * @param deltaCol The change in the column index (horizontal movement).
     */
    Direction(int deltaRow, int deltaCol) {
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
    }

    /**
     * Gets the change in the row index associated with this direction.
     *
     * @return The row delta (e.g., -1 for UP, 1 for DOWN).
     */
    public int getDeltaRow() {
        return deltaRow;
    }

    /**
     * Gets the change in the column index associated with this direction.
     *
     * @return The column delta (e.g., -1 for LEFT, 1 for RIGHT).
     */
    public int getDeltaCol() {
        return deltaCol;
    }

    /**
     * Returns the opposite cardinal direction.
     * <p>
     * This is useful for the "Anti-Return" rule (a player cannot push back a tile
     * in the opposite direction immediately after).
     * </p>
     *
     * @return The direction directly opposite to the current one.
     */
    public Direction opposite() {
        // Retourne la direction opposée : Haut <-> Bas, Gauche <-> Droite.
        // C'est indispensable pour implémenter la règle "Anti-Retour" (interdiction d'annuler le coup précédent).
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}