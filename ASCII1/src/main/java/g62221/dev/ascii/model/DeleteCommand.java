package g62221.dev.ascii.model;


/**
 * Commande supprimant une forme du dessin par son index.
 *
 * Permet la suppression et la restauration (undo) d’une forme à une position précise.
 */
public class DeleteCommand implements Command {
    private final Drawing drawing;
    private final int index;
    private Shape removed;

    /**
     * @param drawing le dessin concerné
     * @param index l’index de la forme à supprimer
     */
    public DeleteCommand(Drawing drawing, int index) {
        this.drawing = drawing;
        this.index = index;
    }

    /**
     * Supprime la forme à l'index ; elle est stockée pour undo.
     */
    @Override
    public void execute() {
        removed = drawing.getShapeAt(index);
        drawing.remove(index);
    }

    /**
     * Restaure la forme supprimée à son emplacement d’origine.
     */
    @Override
    public void unexecute() {
        drawing.addShapeAt(index, removed);
    }
}
