package g62221.dev.ascii.model;

/**
 * Commande permettant de dégrouper un Group composite.
 *
 * En undo, recompose le Group.
 */
public class UngroupCommand implements Command {
    private final Drawing drawing;
    private final int groupIndex;
    private Group group;

    /**
     * @param drawing dessin concerné
     * @param groupIndex index de la forme composite à dissocier
     */
    public UngroupCommand(Drawing drawing, int groupIndex) {
        this.drawing = drawing;
        this.groupIndex = groupIndex;
    }

    /**
     * Remplace le Group par ses sous-formes.
     */
    @Override
    public void execute() {
        Shape s = drawing.getShapeAt(groupIndex);
        if (s instanceof Group) {
            group = (Group) s;
            drawing.remove(groupIndex);
            for (Shape shape : group.getShapes())
                drawing.addShape(shape);
        }
    }

    /**
     * Restaure le Group, retire ses sous-formes.
     */
    @Override
    public void unexecute() {
        for (Shape shape : group.getShapes())
            drawing.removeShape(shape);
        drawing.addShape(group);
    }
}
