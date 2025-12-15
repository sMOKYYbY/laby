package g62221.dev.ascii.model;

/**
 * Représente un rectangle axis-aligned défini par son coin supérieur gauche,
 * une largeur et une hauteur.
 * <p>
 * On considère qu'un point est à l'intérieur si ses coordonnées sont comprises
 * entre les bornes définies par le coin supérieur gauche et le coin inférieur droit.
 * </p>
 */
public class Rectangle extends ColoredShape {
    private Point upperLeft;
    private double width;
    private double height;

    /**
     * Construit un rectangle.
     *
     * @param upperLeft coin supérieur gauche
     * @param width     largeur (>= 0)
     * @param height    hauteur (>= 0)
     * @param color     caractère d'affichage
     */
    public Rectangle(Point upperLeft, double width, double height, char color) {
        super(color);
        this.upperLeft = new Point(upperLeft);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isInside(Point p) {
        double px = p.getX();
        double py = p.getY();
        return px >= upperLeft.getX()
                && px <= upperLeft.getX() + width
                && py >= upperLeft.getY()
                && py <= upperLeft.getY() + height;
    }

    @Override
    public void move(double dx, double dy) {
        upperLeft.move(dx, dy);
    }

    /**
     * Retourne le coin supérieur gauche (copie).
     *
     * @return copie du coin supérieur gauche
     */
    public Point getUpperLeft() {
        return new Point(upperLeft);
    }

    /**
     * Retourne la largeur.
     *
     * @return largeur
     */
    public double getWidthValue() {
        return width;
    }

    /**
     * Retourne la hauteur.
     *
     * @return hauteur
     */
    public double getHeightValue() {
        return height;
    }
}
