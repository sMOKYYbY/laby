package g62221.labyrinthe.model;

import java.util.*;

/**
 * Represents the game board logic and pathfinding.
 */
public class Board {
    private final Tile[][] grid;
    private Tile extraTile;

    /**
     * Constructor. Initializes the grid.
     */
    public Board() {
        this.grid = new Tile[7][7];
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialization logic (simplified for brevity, similar to previous version)
        // Ensure to populate all 7x7 cells.
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                boolean isFixed = (r % 2 == 0) && (c % 2 == 0);
                Tile.Shape shape = isFixed ? Tile.Shape.T : Tile.Shape.L;
                grid[r][c] = new Tile(shape, 0, null, isFixed);
            }
        }
        this.extraTile = new Tile(Tile.Shape.I, 0, null, false);
    }

    /**
     * Slides a row or column.
     * @param dir direction.
     * @param index index.
     */
    public void slide(Direction dir, int index) {
        // Same slide logic as provided in previous step
        if (index % 2 == 0) throw new IllegalArgumentException("Fixed line");

        Tile newExtra = null;
        if (dir == Direction.RIGHT) {
            newExtra = grid[index][6];
            System.arraycopy(grid[index], 0, grid[index], 1, 6);
            grid[index][0] = extraTile;
        } else if (dir == Direction.LEFT) {
            newExtra = grid[index][0];
            System.arraycopy(grid[index], 1, grid[index], 0, 6);
            grid[index][6] = extraTile;
        } else if (dir == Direction.DOWN) {
            newExtra = grid[6][index];
            for (int i = 6; i > 0; i--) grid[i][index] = grid[i - 1][index];
            grid[0][index] = extraTile;
        } else if (dir == Direction.UP) {
            newExtra = grid[0][index];
            for (int i = 0; i < 6; i++) grid[i][index] = grid[i + 1][index];
            grid[6][index] = extraTile;
        }
        extraTile = newExtra;
    }

    /**
     * Calculates all reachable positions from a starting point using BFS.
     * @param start the starting position.
     * @return a set of all reachable positions.
     */
    public Set<Position> getReachablePositions(Position start) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            Tile currentTile = grid[current.row()][current.col()];

            for (Direction dir : currentTile.getConnectors()) {
                Position neighborPos = current.next(dir);

                if (isValid(neighborPos)) {
                    Tile neighborTile = grid[neighborPos.row()][neighborPos.col()];
                    // Check if neighbor connects back to current (Mutual connection)
                    if (neighborTile.getConnectors().contains(dir.opposite()) && !visited.contains(neighborPos)) {
                        visited.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }
        return visited;
    }

    private boolean isValid(Position p) {
        return p.row() >= 0 && p.row() < 7 && p.col() >= 0 && p.col() < 7;
    }

    /**
     * Gets a tile.
     * @param row row index.
     * @param col col index.
     * @return the tile.
     */
    public Tile getTile(int row, int col) {
        return grid[row][col];
    }

    /**
     * Gets the extra tile.
     * @return extra tile.
     */
    public Tile getExtraTile() {
        return extraTile;
    }
}