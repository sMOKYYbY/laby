package g62221.dev.ascii.model;

/**
 * Représente un cercle défini par un centre et un rayon.
 * <p>
 * Le test d'appartenance utilise la distance euclidienne : un point est à l'intérieur
 * si sa distance au centre est inférieure ou égale au rayon.
 * </p>
 */
public class Circle extends ColoredShape {
    private Point center;
    private double radius;

    /**
     * Construit un cercle.
     *
     * @param center centre du cercle
     * @param radius rayon (doit être >= 0)
     * @param color  caractère d'affichage
     */
    public Circle(Point center, double radius, char color) {
        super(color);
        this.center = new Point(center); // copie défensive
        this.radius = radius;
    }

    @Override
    public boolean isInside(Point p) {
        return center.distanceTo(p) <= radius;
    }

    @Override
    public void move(double dx, double dy) {
        center.move(dx, dy);
    }

    /**
     * Retourne le centre (copie) du cercle.
     *
     * @return copie du centre
     */
    public Point getCenter() {
        return new Point(center);
    }

    /**
     * Retourne le rayon du cercle.
     *
     * @return rayon
     */
    public double getRadius() {
        return radius;
    }
}
