package g62221.dev.ascii.model;
import g62221.dev.ascii.model.Drawing;
import g62221.dev.ascii.model.Shape;

/**
 * Commande ajoutant une forme au dessin dans AsciiPaint.
 *
 * Permet d’ajouter une forme et, via undo, de la retirer de l’état précédent.
 */
public class AddCommand implements Command {
    private final Drawing drawing;
    private final Shape shape;

    /**
     * @param drawing le dessin concerné
     * @param shape la forme à ajouter
     */
    public AddCommand(Drawing drawing, Shape shape) {
        this.drawing = drawing;
        this.shape = shape;
    }

    /**
     * Ajoute la forme au dessin.
     */
    @Override
    public void execute() {
        drawing.addShape(shape);
    }

    /**
     * Retire la forme précédemment ajoutée.
     */
    @Override
    public void unexecute() {
        drawing.removeShape(shape);
    }
}
