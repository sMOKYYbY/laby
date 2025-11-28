package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageFactory {
    private static final Map<String, Image> cache = new HashMap<>();
    private static final Image ERROR_IMAGE;

    // Liste des trésors qui sont rangés dans le dossier "Fixed_tiles"
    private static final Set<String> FIXED_TREASURES_FILES = Set.of(
            "goal_book", "goal_candleholder", "goal_coffre", "goal_crown",
            "goal_helmet", "goal_keys", "goal_map", "goal_money",
            "goal_ring", "goal_saphir", "goal_skull", "goal_sword"
    );

    static {
        // Carré rose (Magenta) pour signaler une image manquante
        WritableImage img = new WritableImage(100, 100);
        for (int i = 0; i < 100; i++)
            for (int j = 0; j < 100; j++)
                img.getPixelWriter().setColor(i, j, Color.MAGENTA);
        ERROR_IMAGE = img;
    }

    public static Image getImage(Tile tile) {
        String path = determinePath(tile);

        if (!cache.containsKey(path)) {
            try (InputStream is = ImageFactory.class.getResourceAsStream(path)) {
                if (is != null) {
                    cache.put(path, new Image(is));
                } else {
                    System.err.println("Image introuvable : " + path);
                    return ERROR_IMAGE;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_IMAGE;
            }
        }
        return cache.get(path);
    }

    private static String determinePath(Tile tile) {
        // 1. Si la tuile a un nom spécifique (Trésor ou Coin)
        if (tile.getTreasure() != null) {
            String name = tile.getTreasure();

            // A. COINS SPÉCIFIQUES
            if (name.startsWith("fixed_tile")) {
                return "/Corners/" + name + ".jpg";
            }

            // B. TRÉSORS
            // On s'assure d'avoir le préfixe goal_ (sécurité)
            String filename = name.startsWith("goal_") ? name : "goal_" + name;

            // VÉRIFICATION : Est-ce un trésor du dossier "Fixed_tiles" ?
            if (FIXED_TREASURES_FILES.contains(filename)) {
                return "/Fixed_tiles/" + filename + ".jpg";
            } else {
                // Sinon, c'est un trésor mobile (racine)
                return "/" + filename + ".jpg";
            }
        }

        // 2. TUILES VIDES (I, L sans trésor)
        return switch (tile.getShape()) {
            case I -> "/I_tiles/I_Shape.jpg";
            case L -> "/L_tiles/L_tile.jpg";
            default -> "/I_tiles/I_Shape.jpg";
        };
    }
}