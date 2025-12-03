package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class TileView extends StackPane {
    private final ImageView imageView;

    // Table de correction (inchangée)
    private static final Map<String, Double> CORRECTIONS = new HashMap<>();
    static {
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
        double mobileTDefault = 180.0;
        CORRECTIONS.put("goal_pig", mobileTDefault);
        CORRECTIONS.put("goal_dragon", mobileTDefault);
        CORRECTIONS.put("goal_ghost", mobileTDefault);
        CORRECTIONS.put("goal_ghost2", mobileTDefault);
        CORRECTIONS.put("goal_witch", mobileTDefault);
        CORRECTIONS.put("goal_bat", mobileTDefault);
    }

    public TileView() {
        this.imageView = new ImageView();

        // --- MODIFICATION TAILLE : 100x100 ---
        this.imageView.setFitWidth(100);
        this.imageView.setFitHeight(100);
        // -------------------------------------

        this.getChildren().add(imageView);
    }

    public void update(Tile tile) {
        if (tile == null) return;

        imageView.setImage(ImageFactory.getImage(tile));

        double rotationLogique = tile.getRotation();
        double correction = 0;

        String name = tile.getTreasure();
        String keyName = (name != null) ? name.replace("fixed_tile_", "") : null;

        // --- Logique de correction d'image (inchangée) ---
        if (keyName != null && CORRECTIONS.containsKey(keyName)) {
            correction = CORRECTIONS.get(keyName);
        } else if (tile.isFixed() && name != null && name.startsWith("fixed_tile_") && !name.contains("goal")) {
            imageView.setRotate(0);
            return; // Pas de debug sur les coins fixes car ils sont "hors logique" de rotation standard
        } else if (!tile.hasTreasure()) {
            correction = 0;
        } else {
            if (tile.getShape() == Tile.Shape.T) correction = tile.isFixed() ? 270.0 : 180.0;
            if (tile.getShape() == Tile.Shape.L) correction = 180.0;
        }

        imageView.setRotate(rotationLogique + correction);

        // --- DEBUG VISUEL (CARRÉS VERTS) ---
        /**
        // 1. On nettoie les anciens carrés pour ne pas les empiler
        this.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Rectangle);

        // 2. On dessine un carré vert pour chaque connecteur logique "ouvert"
        for (g62221.labyrinthe.model.Direction dir : tile.getConnectors()) {
            javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(15, 15, javafx.scene.paint.Color.LIME);
            r.setOpacity(0.8); // Bien visible
            r.setStroke(javafx.scene.paint.Color.BLACK);
            r.setStrokeWidth(1);

            // Positionnement sur les bords
            switch (dir) {
                case UP -> r.setTranslateY(-35);    // Bord Haut
                case DOWN -> r.setTranslateY(35);   // Bord Bas
                case LEFT -> r.setTranslateX(-35);  // Bord Gauche
                case RIGHT -> r.setTranslateX(35);  // Bord Droit
            }

            this.getChildren().add(r);
        }**/
    }
}