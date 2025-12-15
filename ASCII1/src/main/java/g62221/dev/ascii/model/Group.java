package g62221.dev.ascii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite de formes (Composite pattern).
 *
 * Permet de manipuler un ensemble de formes comme une entité unique,
 * avec déplacements/couleur/appartenance déléguée aux sous-formes.
 */
public class Group extends ColoredShape {
    private final List<Shape> shapes = new ArrayList<>();

    /**
     * @param color couleur du groupe, par convention couleur de la première forme
     */
    public Group(char color) {
        super(color);
    }

    /**
     * Ajoute une sous-forme au groupe.
     * @param s sous-forme
     */
    public void addShape(Shape s) { shapes.add(s); }

    /**
     * Retire une sous-forme du groupe.
     * @param s sous-forme
     */
    public void removeShape(Shape s) { shapes.remove(s); }

    /**
     * Retourne la liste des sous-formes.
     * @return sous-formes du groupe
     */
    public List<Shape> getShapes() { return shapes; }

    /**
     * Teste si le point est à l’intérieur d’au moins une sous-forme.
     */
    @Override
    public boolean isInside(Point p) {
        for (Shape s : shapes) if (s.isInside(p)) return true;
        return false;
    }

    /**
     * Déplace toutes les sous-formes du groupe.
     */
    @Override
    public void move(double dx, double dy) {
        for (Shape s : shapes) s.move(dx, dy);
    }

    /**
     * Affecte la même couleur à toutes les sous-formes.
     */
    @Override
    public void setColor(char c) {
        for (Shape s : shapes) s.setColor(c);
        super.setColor(c);
    }

    /**
     * Couleur du groupe = couleur de la première sous-forme (par convention).
     */
    @Override
    public char getColor() {
        return shapes.isEmpty() ? super.getColor() : shapes.get(0).getColor();
    }
}
