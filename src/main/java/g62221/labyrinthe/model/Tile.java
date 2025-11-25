package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tile on the board with specific shape and orientation.
 */
public class Tile {
    public enum Shape { I, L, T }

    private final Shape shape;
    private int rotation; // 0, 90, 180, 270
    private final String treasure;
    private final boolean fixed;

    /**
     * Constructor.
     * @param shape shape type.
     * @param rotation initial rotation in degrees.
     * @param treasure name of the treasure or null.
     * @param fixed true if tile is immovable.
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
        this.rotation = (this.rotation + 90) % 360;
    }

    /**
     * Gets the list of open directions based on shape and rotation.
     * @return list of open directions.
     */
    public List<Direction> getConnectors() {
        List<Direction> baseConnectors = new ArrayList<>();
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

        List<Direction> rotatedConnectors = new ArrayList<>();
        int steps = rotation / 90;

        for (Direction dir : baseConnectors) {
            Direction newDir = dir;
            for (int i = 0; i < steps; i++) {
                newDir = rotateDirection90(newDir);
            }
            rotatedConnectors.add(newDir);
        }
        return rotatedConnectors;
    }

    private Direction rotateDirection90(Direction dir) {
        return switch (dir) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
    }

    /**
     * Gets the shape.
     * @return shape.
     */
    public Shape getShape() { return shape; }

    /**
     * Gets the rotation.
     * @return rotation.
     */
    public int getRotation() { return rotation; }

    /**
     * Gets the treasure.
     * @return treasure name.
     */
    public String getTreasure() { return treasure; }

    /**
     * Checks if fixed.
     * @return true if fixed.
     */
    public boolean isFixed() { return fixed; }

    /**
     * Checks if has treasure.
     * @return true if has treasure.
     */
    public boolean hasTreasure() { return treasure != null; }
}