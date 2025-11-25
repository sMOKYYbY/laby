package g62221.labyrinthe.model;

/**
 * Enumeration representing the four cardinal directions.
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int deltaRow;
    private final int deltaCol;

    Direction(int deltaRow, int deltaCol) {
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
    }

    /**
     * Returns the row delta.
     * @return the change in row index.
     */
    public int getDeltaRow() {
        return deltaRow;
    }

    /**
     * Returns the column delta.
     * @return the change in column index.
     */
    public int getDeltaCol() {
        return deltaCol;
    }

    /**
     * Returns the opposite direction.
     * @return the opposite direction.
     */
    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}