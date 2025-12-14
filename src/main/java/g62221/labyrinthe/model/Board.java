package g62221.labyrinthe.model;

import java.util.*;

/**
 * Represents the game board logic, managing the grid of tiles, sliding mechanics, and pathfinding.
 * <p>
 * The board consists of a 7x7 grid containing both fixed and mobile tiles.
 * It also holds one extra tile used for sliding rows and columns.
 * </p>
 */
public class Board {
    private final Tile[][] grid;
    private Tile extraTile;

    /**
     * Constructs a new Board and initializes the grid configuration.
     */
    public Board() {
        this.grid = new Tile[7][7];
        initializeBoard();
    }

    /**
     * Initializes the board with fixed tiles (corners, treasures) and randomly distributed mobile tiles.
     * <p>
     * Ensures that specific treasures are placed on fixed tiles and that mobile treasures
     * are correctly assigned to 'T' or 'L' shapes based on their visual representation.
     * </p>
     */
    public void initializeBoard() {
        // --- 1. Préparation des Trésors Mobiles (Classés par forme) ---

        // Liste T : Trésors dessinés sur des tuiles à 3 embranchements (Forme T)
        List<String> mobileTreasuresT = new ArrayList<>(Arrays.asList(
                "goal_bat", "goal_dragon", "goal_ghost", "goal_ghost2", "goal_pig", "goal_witch"
        ));

        // Liste L : Trésors dessinés sur des tuiles à 2 embranchements (Forme L/Angle)
        List<String> mobileTreasuresL = new ArrayList<>(Arrays.asList(
                "goal_butteryfly", "goal_hibou", "goal_insecte", "goal_lezard", "goal_mouse", "goal_spider"
        ));

        // Mélange aléatoire des trésors
        Collections.shuffle(mobileTreasuresT);
        Collections.shuffle(mobileTreasuresL);

        int indexT = 0;
        int indexL = 0;

        // --- 2. Création de la pioche de tuiles mobiles ---
        List<Tile> mobileTiles = new ArrayList<>();

        // A. 6 tuiles "T" avec un trésor
        for (int i = 0; i < 6; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.T, mobileTreasuresT.get(indexT++)));
        }

        // B. 6 tuiles "L" avec un trésor
        for (int i = 0; i < 6; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.L, mobileTreasuresL.get(indexL++)));
        }

        // C. 10 tuiles "L" sans trésor (tuiles couloirs simples)
        for (int i = 0; i < 10; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.L, null));
        }

        // D. 12 tuiles "I" sans trésor (tuiles droites)
        for (int i = 0; i < 12; i++) {
            mobileTiles.add(createRandomTile(Tile.Shape.I, null));
        }

        // Mélange final de toutes les tuiles mobiles pour la distribution
        Collections.shuffle(mobileTiles);
        int tilePileIndex = 0;

        // --- 3. Remplissage du Plateau ---
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                // Une case est fixe si sa ligne ET sa colonne sont paires (ex: 0,0 ; 2,4)
                boolean isFixed = (r % 2 == 0) && (c % 2 == 0);

                if (isFixed) {
                    // Création spécifique pour les tuiles inamovibles
                    grid[r][c] = createFixedTile(r, c);
                } else {
                    // Remplissage avec la pioche pour les cases mobiles
                    if (tilePileIndex < mobileTiles.size()) {
                        grid[r][c] = mobileTiles.get(tilePileIndex++);
                    } else {
                        // Sécurité : Cas impossible si le compte est bon
                        grid[r][c] = new Tile(Tile.Shape.I, 0, null, false);
                    }
                }
            }
        }

        // La dernière tuile restante devient la tuile supplémentaire (en main du joueur)
        if (tilePileIndex < mobileTiles.size()) {
            this.extraTile = mobileTiles.get(tilePileIndex);
        } else {
            this.extraTile = new Tile(Tile.Shape.I, 0, null, false);
        }
    }

    /**
     * Creates a fixed tile with the correct orientation and treasure based on its coordinates.
     *
     * @param r The row index.
     * @param c The column index.
     * @return The configured fixed Tile.
     */
    private Tile createFixedTile(int r, int c) {
        String key = r + "," + c;

        // --- COINS (Shape L) ---
        // Les rotations sont définies pour que les ouvertures pointent vers l'intérieur du plateau.
        switch (key) {
            case "0,0": return new Tile(Tile.Shape.L, 90, "fixed_tile_upleft_corner", true);
            case "0,6": return new Tile(Tile.Shape.L, 180, "fixed_tile_upright_corner", true);
            case "6,0": return new Tile(Tile.Shape.L, 0, "fixed_tile_downleft_corner", true);
            case "6,6": return new Tile(Tile.Shape.L, 270, "fixed_tile_downright_corner", true);
        }

        // --- AUTRES TUILES FIXES (Shape T) ---
        // Calcul de la rotation pour que le "T" pointe vers le centre ou la direction opposée au bord.
        int rotation = 0;

        if (r == 0) rotation = 0;        // Ligne du Haut : Pointe vers le Bas (0°)
        else if (r == 6) rotation = 180; // Ligne du Bas : Pointe vers le Haut (180°)
        else if (c == 0) rotation = 270; // Colonne Gauche : Pointe vers la Droite (270°)
        else if (c == 6) rotation = 90;  // Colonne Droite : Pointe vers la Gauche (90°)

            // Cas particuliers des tuiles centrales (2,2 ; 4,2 ; 4,4...)
        else {
            switch (key) {
                case "2,2": rotation = 270; break;
                case "4,2": rotation = 180; break;
                case "4,4": rotation = 90; break;
                case "2,4": rotation = 90; break;
                default: rotation = 0;
            }
        }

        String treasureName = getFixedTreasureName(key);
        if (treasureName != null) {
            return new Tile(Tile.Shape.T, rotation, treasureName, true);
        }

        return new Tile(Tile.Shape.T, 0, "goal_unknown", true);
    }

    /**
     * Maps coordinates to fixed treasure names.
     *
     * @param key The "row,col" key string.
     * @return The name of the treasure, or null if none.
     */
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

    /**
     * Helper to create a tile with a random rotation.
     *
     * @param shape The shape of the tile.
     * @param treasure The treasure on the tile (can be null).
     * @return A new Tile instance.
     */
    private Tile createRandomTile(Tile.Shape shape, String treasure) {
        // Génère une rotation aléatoire parmi 0, 90, 180, 270 degrés
        int randomRotation = new Random().nextInt(4) * 90;
        return new Tile(shape, randomRotation, treasure, false);
    }

    /**
     * Slides a row or a column in a specific direction.
     * The extra tile is inserted, and the ejected tile becomes the new extra tile.
     *
     * @param dir   The direction of the slide (UP, DOWN, LEFT, RIGHT).
     * @param index The index of the row or column to slide.
     * @throws IllegalArgumentException if the index corresponds to a fixed line.
     */
    public void slide(Direction dir, int index) {
        // Vérifie que l'index correspond bien à une ligne mobile (impaire)
        if (index % 2 == 0) throw new IllegalArgumentException("Cannot slide a fixed line!");

        Tile newExtra = null;

        // Logique de décalage selon la direction
        if (dir == Direction.RIGHT) {
            newExtra = grid[index][6]; // La dernière tuile sort
            System.arraycopy(grid[index], 0, grid[index], 1, 6); // Décalage
            grid[index][0] = extraTile; // Insertion au début
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

        // La tuile éjectée devient la nouvelle tuile en main
        extraTile = newExtra;
    }

    /**
     * Calculates all reachable positions from a starting point using Breadth-First Search (BFS).
     * Two tiles are connected if and only if they have mutually compatible connectors (Exit -> Entry).
     *
     * @param start The starting position.
     * @return A set of all reachable positions.
     */
    public Set<Position> getReachablePositions(Position start) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();

        // Initialisation du parcours BFS
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            Tile currentTile = grid[current.row()][current.col()];

            // Pour chaque direction ouverte sur la tuile actuelle
            for (Direction dir : currentTile.getConnectors()) {
                Position neighborPos = current.next(dir);

                // Si la case voisine existe (est dans la grille)
                if (isValid(neighborPos)) {
                    Tile neighborTile = grid[neighborPos.row()][neighborPos.col()];

                    // Vérification CRUCIALE : Connexion mutuelle
                    // Le voisin doit avoir une ouverture vers la case d'où l'on vient
                    if (neighborTile.getConnectors().contains(dir.opposite()) && !visited.contains(neighborPos)) {
                        visited.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }
        return visited;
    }

    /**
     * Checks if a position is within the grid boundaries.
     * @param p The position to check.
     * @return true if valid.
     */
    private boolean isValid(Position p) {
        return p.row() >= 0 && p.row() < 7 && p.col() >= 0 && p.col() < 7;
    }

    /**
     * Gets the tile at the specified coordinates.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The Tile object.
     */
    public Tile getTile(int row, int col) { return grid[row][col]; }

    /**
     * Gets the current extra tile (the one in hand).
     *
     * @return The extra Tile.
     */
    public Tile getExtraTile() { return extraTile; }
}