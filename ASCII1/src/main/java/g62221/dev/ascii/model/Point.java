package g62221.dev.ascii.model;

/**
 * Représente un point (coordonnées x,y) en coordonnées réelles.
 * <p>
 * Fournit des utilitaires simples : accès aux coordonnées, déplacement et
 * calcul de distance euclidienne vers un autre point.
 * </p>
 */
public class Point {
    private double x;
    private double y;

    /**
     * Construit un point aux coordonnées (x,y).
     *
     * @param x coordonnée x
     * @param y coordonnée y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construit une copie du point fourni.
     *
     * @param p point à copier
     */
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Retourne la coordonnée x.
     *
     * @return coordonnée x
     */
    public double getX() {
        return x;
    }

    /**
     * Retourne la coordonnée y.
     *
     * @return coordonnée y
     */
    public double getY() {
        return y;
    }

    /**
     * Déplace le point de (dx, dy).
     *
     * @param dx décalage en x
     * @param dy décalage en y
     */
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * Calcule la distance euclidienne entre ce point et un autre.
     *
     * @param other l'autre point
     * @return distance euclidienne
     */
    public double distanceTo(Point other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "Point(" + x + "," + y + ")";
    }
}
