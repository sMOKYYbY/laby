package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Visual representation of a single tile.
 */
public class TileView extends StackPane {
    private final ImageView imageView;

    /**
     * Constructor.
     */
    public TileView() {
        this.imageView = new ImageView();
        this.imageView.setFitWidth(60);
        this.imageView.setFitHeight(60);
        this.getChildren().add(imageView);
    }

    /**
     * Updates the view based on the tile model.
     * @param tile the tile model.
     */
    public void update(Tile tile) {
        if (tile == null) return;
        imageView.setImage(ImageFactory.getImage(tile));
        imageView.setRotate(tile.getRotation());
    }
}