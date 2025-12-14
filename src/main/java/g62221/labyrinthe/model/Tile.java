package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single tile on the game board.
 * <p>
 * A tile is defined by its pathway shape (I, L, T), its current orientation (rotation),
 * and optionally a treasure. It acts as a node in the graph structure used for pathfinding.
 * </p>
 */
public class Tile {

    /**
     * Enumeration of the three possible pathway shapes.
     */
    public enum Shape {
        /** Straight line (Pipe). Opens Opposite sides. */
        I,
        /** Corner (Angle). Opens adjacent sides. */
        L,
        /** T-Junction. Opens three sides. */
        T
    }

    private final Shape shape;
    private int rotation; // 0, 90, 180, 270 degrees
    private final String treasure;
    private final boolean fixed;

    /**
     * Constructs a new Tile.
     *
     * @param shape    The geometric shape of the path (I, L, or T).
     * @param rotation The initial rotation in degrees (0, 90, 180, 270).
     * @param treasure The name of the treasure on this tile, or null if none.
     * @param fixed    True if the tile is glued to the board (cannot be slid).
     */
    public Tile(Shape shape, int rotation, String treasure, boolean fixed) {
        this.shape = shape;
        this.rotation = rotation;
        this.treasure = treasure;
        this.fixed = fixed;
    }

    /**
     * Rotates the tile 90 degrees clockwise.
     */
    public void rotate() {
        // Utilisation du modulo pour garder l'angle entre 0 et 360
        this.rotation = (this.rotation + 90) % 360;
    }

    /**
     * Rotates the tile 90 degrees counter-clockwise.
     */
    public void rotateCounterClockwise() {
        // Astuce mathématique : en Java, le modulo peut renvoyer un négatif.
        // On ajoute 360 avant le modulo pour garantir un angle positif (ex: -90 + 360 = 270).
        this.rotation = (this.rotation - 90 + 360) % 360;
    }

    /**
     * Computes the open directions based on shape and current rotation.
     * <p>
     * This method calculates which sides of the tile are "open" by taking the base shape
     * and applying the current rotation dynamically.
     * </p>
     *
     * @return A list of Directions where this tile has an opening.
     */
    public List<Direction> getConnectors() {
        List<Direction> baseConnectors = new ArrayList<>();

        // 1. Définition des connexions de base (comme si la rotation était 0°)
        switch (shape) {
            case I -> {
                baseConnectors.add(Direction.UP);
                baseConnectors.add(Direction.DOWN);
            }
            case L -> {
                baseConnectors.add(Direction.UP);
                baseConnectors.add(Direction.RIGHT);
            }
            case T -> {
                baseConnectors.add(Direction.LEFT);
                baseConnectors.add(Direction.DOWN);
                baseConnectors.add(Direction.RIGHT);
            }
        }

        // 2. Application de la rotation dynamique sur chaque connecteur
        List<Direction> rotatedConnectors = new ArrayList<>();
        int steps = rotation / 90; // Nombre de quarts de tour à effectuer

        for (Direction dir : baseConnectors) {
            Direction newDir = dir;
            // On tourne la direction autant de fois que nécessaire
            for (int i = 0; i < steps; i++) {
                newDir = rotateDirection90(newDir);
            }
            rotatedConnectors.add(newDir);
        }
        return rotatedConnectors;
    }

    /**
     * Helper method to rotate a single cardinal direction 90 degrees clockwise.
     *
     * @param dir The original direction.
     * @return The new rotated direction.
     */
    private Direction rotateDirection90(Direction dir) {
        return switch (dir) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
    }

    // --- Getters ---

    /**
     * Gets the geometric shape of the tile.
     * @return The shape (I, L, or T).
     */
    public Shape getShape() { return shape; }

    /**
     * Gets the current rotation in degrees.
     * @return The rotation (0, 90, 180, 270).
     */
    public int getRotation() { return rotation; }

    /**
     * Gets the name of the treasure on this tile.
     * @return The treasure identifier, or null if empty.
     */
    public String getTreasure() { return treasure; }

    /**
     * Checks if the tile is fixed to the board.
     * @return true if immovable, false if it can slide.
     */
    public boolean isFixed() { return fixed; }

    /**
     * Checks if the tile holds a treasure.
     * @return true if a treasure is present.
     */
    public boolean hasTreasure() { return treasure != null; }
}