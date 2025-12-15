package g62221.dev.ascii.model;

import java.util.*;

public class AsciiPaint {

    private final Drawing drawing;
    private final CommandManager commandManager;

    /**
     * Construit la façade avec un dessin de taille width x height.
     * @param width largeur du dessin (entier)
     * @param height hauteur du dessin (entier)
     */
    public AsciiPaint(int width, int height) {
        this.drawing = new Drawing(width, height);
        this.commandManager = new CommandManager();
    }

    /**
     * Ajoute un cercle au dessin.
     */
    public void addCircle(double x, double y, double radius, char color) {
        Shape s = new Circle(new Point(x, y), radius, color);
        commandManager.doCommand(new AddCommand(drawing, s));
    }

    /**
     * Ajoute un rectangle au dessin.
     */
    public void addRectangle(double x, double y, double w, double h, char color) {
        Shape s = new Rectangle(new Point(x, y), w, h, color);
        commandManager.doCommand(new AddCommand(drawing, s));
    }

    /**
     * Ajoute un carré au dessin.
     */
    public void addSquare(double x, double y, double side, char color) {
        Shape s = new Square(new Point(x, y), side, color);
        commandManager.doCommand(new AddCommand(drawing, s));
    }

    /**
     * Supprime la forme à l'index donné.
     */
    public void removeShape(int index) {
        commandManager.doCommand(new DeleteCommand(drawing, index));
    }

    /**
     * Change la couleur de la forme à l'index donné.
     */
    public void setColor(int index, char color) {
        commandManager.doCommand(new ChangeColorCommand(drawing, index, color));
    }

    /**
     * Déplace la forme à l'index donné de (dx, dy).
     */
    public void moveShape(int index, double dx, double dy) {
        commandManager.doCommand(new MoveCommand(drawing, index, dx, dy));
    }

    /**
     * Groupe les formes selon une liste d'indices.
     */
    public void group(List<Integer> indices) {
        commandManager.doCommand(new GroupCommand(drawing, indices));
    }

    /**
     * Dégroupe la forme à l'index (doit être un groupe !)
     */
    public void ungroup(int index) {
        commandManager.doCommand(new UngroupCommand(drawing, index));
    }

    /**
     * Annule la dernière commande exéctuée (undo).
     */
    public void undo() {
        commandManager.undo();
    }

    /**
     * Refait la dernière commande annulée (redo).
     */
    public void redo() {
        commandManager.redo();
    }

    /**
     * Retourne le caractère à afficher à la position (x,y). ' ' si rien.
     */
    public char getColorAt(int x, int y) {
        Point p = new Point(x, y);
        Shape s = drawing.getShapeAt(p);
        return (s == null) ? ' ' : s.getColor();
    }

    /**
     * Retourne la largeur du dessin.
     */
    public int getWidth() { return drawing.getWidth(); }

    /**
     * Retourne la hauteur du dessin.
     */
    public int getHeight() { return drawing.getHeight(); }

    /**
     * Accès à la liste des formes (pour le contrôleur).
     */
    public Drawing getDrawing() { return drawing; }
}
