package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class TileView extends StackPane {
    private final ImageView imageView;

    // --- TABLE DE CORRECTION MAÎTRE ---
    private static final Map<String, Double> CORRECTIONS = new HashMap<>();

    static {
        // --- MISE À JOUR : LE HIBOU PASSÉ À 270.0 ---
        CORRECTIONS.put("goal_hibou", 270.0); // CORRIGÉ !

        // --- Les autres cas validés précédemment ---
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

        // L Mobiles
        CORRECTIONS.put("goal_mouse", 180.0);
        CORRECTIONS.put("goal_spider", 90.0);

        // T Mobiles (Défaut 180°)
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
        this.imageView.setFitWidth(95);
        this.imageView.setFitHeight(95);
        this.getChildren().add(imageView);
    }

    public void update(Tile tile) {
        if (tile == null) return;

        imageView.setImage(ImageFactory.getImage(tile));

        double rotationLogique = tile.getRotation();
        double correction = 0;

        String name = tile.getTreasure();
        String keyName = (name != null) ? name.replace("fixed_tile_", "") : null;

        // 1. Correction par Nom
        if (keyName != null && CORRECTIONS.containsKey(keyName)) {
            correction = CORRECTIONS.get(keyName);
        }
        // 2. Correction Coins Fixes
        else if (tile.isFixed() && name != null && name.startsWith("fixed_tile_") && !name.contains("goal")) {
            imageView.setRotate(0);
            return;
        }
        // 3. Correction Tuiles Vides
        else if (!tile.hasTreasure()) {
            switch (tile.getShape()) {
                case I -> correction = 0;
                case L -> correction = 0.0;
                case T -> correction = 0;
            }
        }
        // 4. Fallback
        else {
            if (tile.getShape() == Tile.Shape.T) correction = tile.isFixed() ? 270.0 : 180.0;
            if (tile.getShape() == Tile.Shape.L) correction = 180.0;
        }

        imageView.setRotate(rotationLogique + correction);


        /**

         // --- DEBUG VISUEL (Carrés Verts) ---

         // 1. On nettoie les anciens carrés de debug
         this.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Rectangle);

         // 2. On parcourt les connecteurs logiques de la tuile
         for (g62221.labyrinthe.model.Direction dir : tile.getConnectors()) {
         // Création d'un petit carré vert semi-transparent
         javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(10, 10, javafx.scene.paint.Color.LIMEGREEN);
         r.setOpacity(0.7);

         // Positionnement sur les bords selon la direction
         switch (dir) {
         case UP -> r.setTranslateY(-25);    // Haut
         case DOWN -> r.setTranslateY(25);   // Bas
         case LEFT -> r.setTranslateX(-25);  // Gauche
         case RIGHT -> r.setTranslateX(25);  // Droite
         }

         // Ajout à la vue
         this.getChildren().add(r);
         }*/
    }
}