package g62221.dev.ascii.model;

/**
 * Interface décrivant le contrat pour une forme géométrique.
 * <p>
 * Une forme doit être capable de :
 * <ul>
 *   <li>indiquer si un point est à l'intérieur {@link #isInside(Point)}</li>
 *   <li>être déplacée {@link #move(double,double)}</li>
 *   <li>fournir et modifier une "couleur" représentée par un caractère</li>
 * </ul>
 *
 */
public interface Shape {
    /**
     * Indique si le point {@code p} est à l'intérieur (ou sur la bordure) de la forme.
     *
     * @param p point testé
     * @return true si le point est à l'intérieur, false sinon
     */
    boolean isInside(Point p);

    /**
     * Déplace la forme de (dx, dy).
     *
     * @param dx déplacement en x
     * @param dy déplacement en y
     */
    void move(double dx, double dy);

    /**
     * Retourne le caractère représentant la "couleur" de la forme.
     *
     * @return caractère d'affichage
     */
    char getColor();

    /**
     * Définit le caractère représentant la "couleur" de la forme.
     *
     * @param color caractère d'affichage
     */
    void setColor(char color);
}
