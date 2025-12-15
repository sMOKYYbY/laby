package g62221.dev.ascii.model;

/**
 * Classe abstraite fournissant la gestion de la couleur pour les formes.
 * <p>
 * Implémente partiellement {@link Shape} en gérant l'attribut {@code color}.
 * Les sous-classes doivent implémenter les méthodes géométriques {@code isInside}
 * et {@code move}.
 * </p>
 */
public abstract class ColoredShape implements Shape {
    private char color;

    /**
     * Construit une ColoredShape avec la couleur donnée.
     *
     * @param color caractère représentant la couleur
     */
    public ColoredShape(char color) {
        this.color = color;
    }

    @Override
    public char getColor() {
        return color;
    }

    @Override
    public void setColor(char color) {
        this.color = color;
    }
}
