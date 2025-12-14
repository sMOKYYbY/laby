package g62221.labyrinthe.model;

/**
 * Immutable record representing coordinates on the game board (Row, Column).
 * <p>
 * This simple data structure is used throughout the game to locate tiles and players.
 * Being a record, it automatically provides implementation for {@code equals()},
 * {@code hashCode()}, and {@code toString()}.
 * </p>
 *
 * @param row The row index (Vertical Y axis).
 * @param col The column index (Horizontal X axis).
 */
public record Position(int row, int col) {

    /**
     * Calculates the adjacent position in a given direction.
     * <p>
     * This method returns a new Position shifted by the direction's delta.
     * <b>Note:</b> It does not check if the new position is valid (inside the board boundaries).
     * </p>
     *
     * @param dir The direction to move towards.
     * @return A new Position object representing the target coordinates.
     */
    public Position next(Direction dir) {
        // Calcul de la nouvelle position en ajoutant le delta de la direction (ex: ligne - 1 pour UP).
        // Comme c'est un 'record', l'objet est immuable : on renvoie une nouvelle instance.
        return new Position(row + dir.getDeltaRow(), col + dir.getDeltaCol());
    }
}