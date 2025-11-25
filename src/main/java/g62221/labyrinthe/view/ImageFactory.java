package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to load images based on tile state.
 */
public class ImageFactory {
    private static final Map<String, Image> cache = new HashMap<>();

    /**
     * Retrieves the image for a given tile.
     * @param tile the tile to render.
     * @return the corresponding JavaFX Image.
     */
    public static Image getImage(Tile tile) {
        String path = determinePath(tile);
        if (!cache.containsKey(path)) {
            InputStream is = ImageFactory.class.getResourceAsStream(path);
            if (is == null) {
                // Fallback or error handling
                return null;
            }
            cache.put(path, new Image(is));
        }
        return cache.get(path);
    }

    private static String determinePath(Tile tile) {
        // 1. Corners (Fixed) - Specific naming required in resources
        if (tile.isFixed() && tile.getShape() == Tile.Shape.L && !tile.hasTreasure()) {
            // Example path, adjust if your corners have specific names
            return "/Corners/fixed_tile_upleft_corner.jpg";
        }

        // 2. Treasures (Goal)
        if (tile.hasTreasure()) {
            String filename = "goal_" + tile.getTreasure() + ".jpg";
            // Check if it is a fixed T tile
            if (tile.isFixed()) {
                return "/Fixed_tiles/" + filename;
            } else {
                return "/" + filename; // Root of resources
            }
        }

        // 3. Empty Mobile Tiles
        return switch (tile.getShape()) {
            case I -> "/I_tiles/I_Shape.jpg";
            case L -> "/L_tiles/L_tile.jpg";
            default -> "/I_tiles/I_Shape.jpg"; // Fallback
        };
    }
}