package g62221.dev.ascii.model;

/**
 * Commande déplaçant une forme d’un certain vecteur
 * (dx, dy) au sein du dessin.
 *
 * Undo inverse le déplacement.
 */
public class MoveCommand implements Command {
    private final Drawing drawing;
    private final int index;
    private final double dx, dy;

    /**
     * @param drawing dessin concerné
     * @param index index de la forme à déplacer
     * @param dx déplacement horizontal
     * @param dy déplacement vertical
     */
    public MoveCommand(Drawing drawing, int index, double dx, double dy) {
        this.drawing = drawing;
        this.index = index;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Déplace la forme selon (dx, dy).
     */
    @Override
    public void execute() {
        drawing.getShapeAt(index).move(dx, dy);
    }

    /**
     * Déplace la forme selon (-dx, -dy).
     */
    @Override
    public void unexecute() {
        drawing.getShapeAt(index).move(-dx, -dy);
    }
}
