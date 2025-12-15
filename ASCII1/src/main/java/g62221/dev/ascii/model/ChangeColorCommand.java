package g62221.dev.ascii.model;


/**
 * Commande modifiant la couleur d’une forme au sein du dessin AsciiPaint.
 *
 * Au undo, la couleur initiale est restaurée.
 */
public class ChangeColorCommand implements Command {
    private final Drawing drawing;
    private final int index;
    private final char newColor;
    private char oldColor;

    /**
     * @param drawing dessin contenant la forme
     * @param index index de la forme à modifier
     * @param color nouvelle couleur à affecter
     */
    public ChangeColorCommand(Drawing drawing, int index, char color) {
        this.drawing = drawing;
        this.index = index;
        this.newColor = color;
    }

    /**
     * Affecte la nouvelle couleur à la forme cible.
     */
    @Override
    public void execute() {
        oldColor = drawing.getShapeAt(index).getColor();
        drawing.getShapeAt(index).setColor(newColor);
    }

    /**
     * Restaure l’ancienne couleur.
     */
    @Override
    public void unexecute() {
        drawing.getShapeAt(index).setColor(oldColor);
    }
}
