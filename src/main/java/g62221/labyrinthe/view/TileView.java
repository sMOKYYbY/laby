package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import g62221.labyrinthe.view.image.ImageFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

/**
 * Visual representation of a single tile on the JavaFX board.
 * <p>
 * This class extends {@link StackPane} to allow layering elements (like the tile image
 * and potential debug overlays or particle effects). It handles the complex logic
 * of mapping the logical rotation (Model) to the visual rotation (View), correcting
 * discrepancies in the source image files.
 * </p>
 */
public class TileView extends StackPane {

    private final ImageView imageView;

    // --- VISUAL CORRECTION MAP ---
    // Why is this needed?
    // The source images were drawn with arbitrary orientations (e.g., the Lizard points Left by default).
    // The Game Logic assumes 0 degrees = Up.
    // This map stores the offset needed to align the image with the logical "Up".
    private static final Map<String, Double> CORRECTIONS = new HashMap<>();

    static {
        // Table de correction manuelle :
        // Pour chaque trésor, on définit de combien de degrés il faut tourner l'image
        // pour qu'elle corresponde à l'orientation "0" du code.
        CORRECTIONS.put("goal_hibou", 270.0);
        CORRECTIONS.put("goal_helmet", 180.0);
        CORRECTIONS.put("goal_insecte", 270.0);
        CORRECTIONS.put("goal_crown", 90.0);
        CORRECTIONS.put("goal_coffre", 180.0);
        CORRECTIONS.put("goal_lezard", 90.0);
        CORRECTIONS.put("goal_candleholder", 180.0);
        CORRECTIONS.put("goal_butteryfly", 90.0);
        CORRECTIONS.put("goal_map", 90.0);
        CORRECTIONS.put("goal_ring", 90.0);
        CORRECTIONS.put("goal_skull", 270.0);
        CORRECTIONS.put("goal_sword", 270.0);
        CORRECTIONS.put("goal_money", 0.0);
        CORRECTIONS.put("goal_keys", 0.0);
        CORRECTIONS.put("goal_saphir", 270.0);
        CORRECTIONS.put("goal_book", 0.0);
        CORRECTIONS.put("goal_mouse", 180.0);
        CORRECTIONS.put("goal_spider", 90.0);

        // Correction par défaut pour les tuiles mobiles en T
        double mobileTDefault = 180.0;
        CORRECTIONS.put("goal_pig", mobileTDefault);
        CORRECTIONS.put("goal_dragon", mobileTDefault);
        CORRECTIONS.put("goal_ghost", mobileTDefault);
        CORRECTIONS.put("goal_ghost2", mobileTDefault);
        CORRECTIONS.put("goal_witch", mobileTDefault);
        CORRECTIONS.put("goal_bat", mobileTDefault);
    }

    /**
     * Constructs a new TileView.
     * Initializes the image container with fixed dimensions.
     */
    public TileView() {
        this.imageView = new ImageView();

        // --- TAILLE FIXE ---
        // On force la taille à 100x100 pixels pour garantir l'alignement dans la grille
        this.imageView.setFitWidth(100);
        this.imageView.setFitHeight(100);
        // -------------------

        this.getChildren().add(imageView);
    }

    /**
     * Updates the visual state of the tile based on the model data.
     *
     * @param tile The tile data object from the model.
     */
    public void update(Tile tile) {
        if (tile == null) return;

        // 1. Récupération de l'image optimisée (via le Pattern Flyweight / Factory)
        imageView.setImage(ImageFactory.getImage(tile));

        // 2. Calcul de la rotation
        // Rotation Logique (Model) : L'état du jeu (0, 90, 180, 270)
        double rotationLogique = tile.getRotation();
        double correction = 0;

        String name = tile.getTreasure();
        // Nettoyage du nom pour trouver la clé dans la map
        String keyName = (name != null) ? name.replace("fixed_tile_", "") : null;

        // --- Application de la correction visuelle ---
        if (keyName != null && CORRECTIONS.containsKey(keyName)) {
            // Cas A : C'est un trésor connu dans la map de correction
            correction = CORRECTIONS.get(keyName);
        } else if (tile.isFixed() && name != null && name.startsWith("fixed_tile_") && !name.contains("goal")) {
            // Cas B : C'est un coin fixe (déjà dessiné correctement, pas de rotation)
            imageView.setRotate(0);
            return;
        } else if (!tile.hasTreasure()) {
            // Cas C : Tuile couloir vide
            correction = 0;
        } else {
            // Cas D : Fallback pour les formes T et L génériques
            if (tile.getShape() == Tile.Shape.T) correction = tile.isFixed() ? 270.0 : 180.0;
            if (tile.getShape() == Tile.Shape.L) correction = 180.0;
        }

        // Application finale de la rotation sur le composant JavaFX
        imageView.setRotate(rotationLogique + correction);

        // --- DEBUG VISUEL (Outil de développement) ---
        // Ce bloc de code m'a servi à vérifier que les connexions logiques (isReachable)
        // correspondaient bien à ce que je voyais à l'écran.
        // Il dessine des petits carrés verts sur les sorties ouvertes.

        /*
        // 1. Nettoyage des anciens marqueurs
        this.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Rectangle);

        // 2. Dessin des indicateurs de connexion
        for (g62221.labyrinthe.model.Direction dir : tile.getConnectors()) {
            javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(15, 15, javafx.scene.paint.Color.LIME);
            r.setOpacity(0.8);
            r.setStroke(javafx.scene.paint.Color.BLACK);
            r.setStrokeWidth(1);

            // Placement sur les bords correspondants
            switch (dir) {
                case UP -> r.setTranslateY(-35);
                case DOWN -> r.setTranslateY(35);
                case LEFT -> r.setTranslateX(-35);
                case RIGHT -> r.setTranslateX(35);
            }
            this.getChildren().add(r);
        }
        */
    }
}