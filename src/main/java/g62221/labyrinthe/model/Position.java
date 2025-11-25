package g62221.labyrinthe.model;

/**
 * Record representing a coordinate on the board.
 * @param row the row index.
 * @param col the column index.
 */
public record Position(int row, int col) {
    /**
     * Computes the next position given a direction.
     * @param dir the direction.
     * @return the new position.
     */
    public Position next(Direction dir) {
        return new Position(row + dir.getDeltaRow(), col + dir.getDeltaCol());
    }
}