package g62221.dev.ascii.model;

/**
 * Représente un carré. Hérite de {@link Rectangle} en imposant width == height.
 */
public class Square extends Rectangle {

    /**
     * Construit un carré.
     *
     * @param upperLeft coin supérieur gauche
     * @param side      longueur du côté (>= 0)
     * @param color     caractère d'affichage
     */
    public Square(Point upperLeft, double side, char color) {
        super(upperLeft, side, side, color);
    }
}
