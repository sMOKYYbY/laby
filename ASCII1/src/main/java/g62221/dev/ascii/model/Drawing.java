package g62221.dev.ascii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une illustration (un canvas) contenant plusieurs formes.
 * <p>
 * Le dessin connaît sa largeur et sa hauteur (en "cellules" / pixels entiers)
 * et maintient une liste ordonnée de {@link Shape}.
 * </p>
 */
public class Drawing {
    private int width;
    private int height;
    private final List<Shape> shapes = new ArrayList<>();

    /**
     * Construit un dessin de taille {@code width} x {@code height}.
     *
     * @param width  largeur en cellules (entier positif)
     * @param height hauteur en cellules (entier positif)
     */
    public Drawing(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Ajoute une forme à la fin de la liste.
     *
     * @param shape forme à ajouter
     */
    public void addShape(Shape s) {
        shapes.add(s);
    }

    public boolean removeShape(Shape s) {
        return shapes.remove(s);
    }

    // Pour supprimer à un index précis
    public boolean remove(int index) {
        if (index >= 0 && index < shapes.size()) {
            shapes.remove(index);
            return true;
        }
        return false;
    }

    // Pour insérer ou restaurer à une position
    public void addShapeAt(int index, Shape shape) {
        shapes.add(index, shape);
    }

    // Pour récupérer une forme à un index
    public Shape getShapeAt(int index) {
        return shapes.get(index);
    }

    /**
     * Retourne la première forme trouvée (dans l'ordre d'ajout) qui contient le point {@code p}.
     * <p>
     * Si plusieurs formes se recouvrent, la première trouvée est renvoyée. Selon le besoin,
     * on peut inverser l'ordre pour rendre la dernière ajoutée prioritaire.
     * </p>
     *
     * @param p point testé
     * @return forme contenant p ou null si aucune
     */
    public Shape getShapeAt(Point p) {
        for (Shape s : shapes) {
            if (s.isInside(p)) return s;
        }
        return null;
    }

    /**
     * Retourne la forme à l'index donné.
     *
     * @param index index
     * @return forme (peut lancer IndexOutOfBoundsException si index invalide)
     */


    /**
     * Retourne la largeur du dessin.
     *
     * @return largeur
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retourne la hauteur du dessin.
     *
     * @return hauteur
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retourne la liste modifiable des formes (utilisé pour inspection ou tests).
     *
     * @return liste de formes
     */
    public List<Shape> getShapes() {
        return shapes;
    }
}
