package g62221.labyrinthe.model;

import java.util.*;

/**
 * Logique du plateau de jeu (Version CorrigÃ©e & ComplÃ¨te).
 */
public class Board {
    private final Tile[][] grid;
    private Tile extraTile;

    public Board() {
        this.grid = new Tile[7][7];
        initializeBoard();
    }

    private void initializeBoard() {
        // --- 1. PrÃ©paration des TrÃ©sors Mobiles (ClassÃ©s par forme) ---

        // Liste T : TrÃ©sors dessinÃ©s sur des tuiles Ã  3 embranchements
        List<String> mobileTreasuresT = new ArrayList<>(Arrays.asList(
                "goal_bat", "goal_dragon", "goal_ghost", "goal_ghost2", "goal_pig", "goal_witch"
        ));

        // Liste L : TrÃ©sors dessinÃ©s sur des tuiles Ã  2 embranchements (angle)
        List<String> mobileTreasuresL = new ArrayList<>(Arrays.asList(
                "goal_butteryfly", "goal_hibou", "goal_insecte", "goal_lezard", "goal_mouse", "goal_spider"
        ));

        Collections.shuffle(mobileTreasuresT);
        Collections.shuffle(mobileTreasuresL);

        int indexT = 0;
        int indexL = 0;

        // --- 2. CrÃ©ation de la pioche de tuiles mobiles ---
        List<Tile> mobileTiles = new ArrayList<>();

        // A. Les 6 tuiles T avec trÃ©sors
        for (int i = 0; i < 6; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.T, mobileTreasuresT.get(indexT++)));
        }

        // B. Les 6 tuiles L avec trÃ©sors
        for (int i = 0; i < 6; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.L, mobileTreasuresL.get(indexL++)));
        }

        // C. 10 tuiles L sans trÃ©sor
        for (int i = 0; i < 10; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.L, null));
        }

        // D. 12 tuiles I sans trÃ©sor
        for (int i = 0; i < 12; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.I, null));
        }

        Collections.shuffle(mobileTiles); // MÃ©lange final
        int tilePileIndex = 0;

        // --- 3. Remplissage du Plateau ---
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                boolean isFixed = (r % 2 == 0) && (c % 2 == 0);

                if (isFixed) {
                    // C'est ici que la magie opÃ¨re pour les cases fixes (0,0), (0,2), etc.
                    grid[r][c] = createFixedTile(r, c);
                } else {
                    if (tilePileIndex < mobileTiles.size()) {
                        grid[r][c] = mobileTiles.get(tilePileIndex++);
                    } else {
                        grid[r][c] = new Tile(Tile.Shape.I, 0, null, false);
                    }
                }
            }
        }

        // Tuile supplÃ©mentaire
        if (tilePileIndex < mobileTiles.size()) {
            this.extraTile = mobileTiles.get(tilePileIndex);
        } else {
            this.extraTile = new Tile(Tile.Shape.I, 0, null, false);
        }
    }

    /**
     * CrÃ©e une tuile fixe avec la BONNE orientation.
     */
    private Tile createFixedTile(int r, int c) {
        String key = r + "," + c;

        // --- COINS (Shape L) ---
        // Rotations calculÃ©es pour ouvrir vers l'intÃ©rieur
        switch (key) {
            case "0,0": return new Tile(Tile.Shape.L, 90, "fixed_tile_upleft_corner", true);
            case "0,6": return new Tile(Tile.Shape.L, 180, "fixed_tile_upright_corner", true);
            case "6,0": return new Tile(Tile.Shape.L, 0, "fixed_tile_downleft_corner", true);
            case "6,6": return new Tile(Tile.Shape.L, 270, "fixed_tile_downright_corner", true);
        }

        // --- AUTRES FIXES (Shape T) ---
        // Calcul de la rotation pour pointer vers le centre
        // Base T (0Â°) = LEFT+DOWN+RIGHT (Pointe vers le Bas)
        int rotation = 0;

        if (r == 0) rotation = 0;        // Ligne Haut : Pointe Bas (0Â°)
        else if (r == 6) rotation = 180; // Ligne Bas : Pointe Haut (180Â°)
        else if (c == 0) rotation = 270; // Col Gauche : Pointe Droite (270Â°)
        else if (c == 6) rotation = 90;  // Col Droite : Pointe Gauche (90Â°)
        else rotation = 0;               // Centre (2,2 ; 2,4 ; 4,2 ; 4,4) : Par dÃ©faut Bas

        String treasureName = getFixedTreasureName(key);
        if (treasureName != null) {
            return new Tile(Tile.Shape.T, rotation, treasureName, true);
        }

        return new Tile(Tile.Shape.T, 0, "goal_unknown", true);
    }

    private String getFixedTreasureName(String key) {
        return switch (key) {
            case "2,0" -> "goal_money";
            case "4,0" -> "goal_book";
            case "0,2" -> "goal_skull";
            case "2,2" -> "goal_keys";
            case "4,2" -> "goal_crown";
            case "6,2" -> "goal_map";
            case "0,4" -> "goal_sword";
            case "2,4" -> "goal_saphir";
            case "4,4" -> "goal_coffre";
            case "6,4" -> "goal_ring";
            case "2,6" -> "goal_helmet";
            case "4,6" -> "goal_candleholder";
            default -> null;
        };
    }

    private Tile createRandomTile(Tile.Shape shape, String treasure) {
        int randomRotation = new Random().nextInt(4) * 90;
        return new Tile(shape, randomRotation, treasure, false);
    }

    // --- LOGIQUE DE JEU ---

    public void slide(Direction dir, int index) {
        if (index % 2 == 0) throw new IllegalArgumentException("Impossible de bouger une ligne fixe !");

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
                    // Connexion mutuelle requise
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

    public Tile getTile(int row, int col) { return grid[row][col]; }
    public Tile getExtraTile() { return extraTile; }
}