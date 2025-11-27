package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageFactory {
    private static final Map<String, Image> cache = new HashMap<>();
    private static final Image ERROR_IMAGE;

    static {
        // CarrÃ© rose en cas d'erreur
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
                    // System.err.println("Image manquante: " + path); // DÃ©commentez pour debug
                    return ERROR_IMAGE;
                }
            } catch (Exception e) {
                return ERROR_IMAGE;
            }
        }
        return cache.get(path);
    }

    private static String determinePath(Tile tile) {
        // Si la tuile a un nom spÃ©cifique (TrÃ©sor ou Coin nommÃ©)
        if (tile.getTreasure() != null) {
            String name = tile.getTreasure();

            // 1. COINS SPÃ‰CIFIQUES (commencent par "fixed_tile")
            if (name.startsWith("fixed_tile")) {
                return "/Corners/" + name + ".jpg";
            }

            // 2. TRÃ‰SORS (commencent par "goal_")
            // Note: on s'assure d'avoir le prÃ©fixe goal_
            String filename = name.startsWith("goal_") ? name : "goal_" + name;

            if (tile.isFixed()) {
                return "/Fixed_tiles/" + filename + ".jpg";
            } else {
                return "/" + filename + ".jpg"; // Racine (TrÃ©sors mobiles)
            }
        }

        // 3. TUILES MOBILES VIDE (Pas de nom/trÃ©sor)
        return switch (tile.getShape()) {
            case I -> "/I_tiles/I_Shape.jpg";
            case L -> "/L_tiles/L_tile.jpg";
            // Cas fallback pour un T mobile sans trÃ©sor (rare mais possible dans le code)
            default -> "/Fixed_tiles/goal_money.jpg"; // Temporaire, ou une image T vide si vous en avez une
        };
    }
}