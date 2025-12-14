package g62221.labyrinthe.view.image;

import g62221.labyrinthe.model.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Factory class responsible for loading and caching game images.
 * <p>
 * This class implements a caching mechanism (Flyweight Pattern) to ensure that
 * each image file is loaded into memory only once, optimizing performance and memory usage.
 * It also handles the logic of mapping a {@link Tile} object to its corresponding file path.
 * </p>
 */
public class ImageFactory {

    // Cache memory: Key = File Path, Value = Loaded JavaFX Image
    // C'est le cœur du Pattern Flyweight : on stocke les images chargées pour ne pas les recharger à chaque fois.
    private static final Map<String, Image> cache = new HashMap<>();

    // Fallback image displayed when a file is missing
    private static final Image ERROR_IMAGE;

    // List of treasures specifically located in the "Fixed_tiles" directory
    // Sert à distinguer les trésors fixes des trésors mobiles pour trouver le bon dossier.
    private static final Set<String> FIXED_TREASURES_FILES = Set.of(
            "goal_book", "goal_candleholder", "goal_coffre", "goal_crown",
            "goal_helmet", "goal_keys", "goal_map", "goal_money",
            "goal_ring", "goal_saphir", "goal_skull", "goal_sword"
    );

    static {
        // Initialisation statique de l'image d'erreur (Carré Magenta)
        // Utile pour le débogage visuel : si on voit du rose, c'est qu'une image manque.
        WritableImage img = new WritableImage(100, 100);
        for (int i = 0; i < 100; i++)
            for (int j = 0; j < 100; j++)
                img.getPixelWriter().setColor(i, j, Color.MAGENTA);
        ERROR_IMAGE = img;
    }

    /**
     * Retrieves the image associated with a specific tile.
     * <p>
     * If the image is already in the cache, it is returned immediately.
     * Otherwise, it is loaded from the disk, cached, and then returned.
     * </p>
     *
     * @param tile The tile model to visualize.
     * @return The corresponding JavaFX Image, or a magenta square if loading fails.
     */
    public static Image getImage(Tile tile) {
        // 1. Détermination du chemin du fichier selon les propriétés de la tuile
        String path = determinePath(tile);

        // 2. Vérification du cache (Optimisation mémoire)
        if (!cache.containsKey(path)) {
            try (InputStream is = ImageFactory.class.getResourceAsStream(path)) {
                if (is != null) {
                    // Chargement réussi -> Ajout au cache
                    cache.put(path, new Image(is));
                } else {
                    // Fichier introuvable -> Log erreur et retour image rose
                    System.err.println("Image introuvable : " + path);
                    return ERROR_IMAGE;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_IMAGE;
            }
        }
        // Retourne l'image stockée en mémoire
        return cache.get(path);
    }

    /**
     * Determines the resource path for a given tile.
     * <p>
     * The logic routes the request to different subdirectories (/Corners, /Fixed_tiles, etc.)
     * based on the tile's content and shape.
     * </p>
     *
     * @param tile The tile to analyze.
     * @return The relative string path to the image resource.
     */
    private static String determinePath(Tile tile) {
        // CAS 1 : La tuile possède un dessin spécifique (Trésor ou départ)
        if (tile.getTreasure() != null) {
            String name = tile.getTreasure();

            // A. Tuiles de coins (Corners)
            if (name.startsWith("fixed_tile")) {
                return "/Corners/" + name + ".jpg";
            }

            // B. Trésors
            // On s'assure que le nom commence bien par "goal_" pour matcher les fichiers
            String filename = name.startsWith("goal_") ? name : "goal_" + name;

            // VÉRIFICATION : Est-ce un trésor fixe ou mobile ?
            // Cela change le dossier de destination.
            if (FIXED_TREASURES_FILES.contains(filename)) {
                return "/Fixed_tiles/" + filename + ".jpg";
            } else {
                // Trésor mobile -> Dossier racine
                return "/" + filename + ".jpg";
            }
        }

        // CAS 2 : Tuiles couloirs simples (I ou L sans trésor)
        return switch (tile.getShape()) {
            case I -> "/I_tiles/I_Shape.jpg";
            case L -> "/L_tiles/L_tile.jpg";
            // Par défaut on renvoie une tuile I (sécurité)
            default -> "/I_tiles/I_Shape.jpg";
        };
    }
}