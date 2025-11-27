package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Tile;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileView extends StackPane {
    private final ImageView imageView;

    public TileView() {
        this.imageView = new ImageView();
        this.imageView.setFitWidth(60);
        this.imageView.setFitHeight(60);
        this.getChildren().add(imageView);
    }

    public void update(Tile tile) {
        if (tile == null) return;

        imageView.setImage(ImageFactory.getImage(tile));

        // 1. TUILES FIXES (Coins et TrÃ©sors fixÃ©s)
        if (tile.isFixed()) {
            if (tile.getTreasure() != null && tile.getTreasure().startsWith("fixed_tile_")) {
                imageView.setRotate(0); // Les images de coins sont dÃ©jÃ  prÃ©-orientÃ©es
            } else {
                // Les trÃ©sors fixes (T) sont Ã  l'envers de base, on ajoute 180
                imageView.setRotate(tile.getRotation() + 180);
            }
        }
        // 2. TUILES MOBILES
        else {
            double rotation = tile.getRotation();

            // CORRECTION DES DÃ‰CALAGES D'IMAGES
            if (tile.getShape() == Tile.Shape.L) {
                // L'image L est correcte de base. PAS de correction.
                rotation += 0;
            } else if (tile.getShape() == Tile.Shape.T) {
                // L'image T (trÃ©sor) est inversÃ©e (Haut vs Bas). Correction +180.
                rotation += 180;
            }

            imageView.setRotate(rotation);
        }

        // --- DEBUG VISUEL : Pour vÃ©rifier les murs invisibles ---
        // (DÃ©commentez ces lignes si vous avez encore un doute)

        this.getChildren().removeIf(node -> node instanceof Rectangle);
        for (Direction dir : tile.getConnectors()) {
            addDebugConnector(dir);
        }

    }

    private void addDebugConnector(Direction dir) {
        Rectangle r = new Rectangle(10, 10, Color.LIMEGREEN);
        r.setOpacity(0.7);
        switch (dir) {
            case UP -> r.setTranslateY(-25);
            case DOWN -> r.setTranslateY(25);
            case LEFT -> r.setTranslateX(-25);
            case RIGHT -> r.setTranslateX(25);
        }
        this.getChildren().add(r);
    }
}