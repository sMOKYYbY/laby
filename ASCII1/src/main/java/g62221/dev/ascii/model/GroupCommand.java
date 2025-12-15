package g62221.dev.ascii.model;

import java.util.List;

/**
 * Commande permettant de grouper plusieurs formes dans un Group composite.
 *
 * En undo, dissocie le Group et restaure les formes individuelles.
 */
public class GroupCommand implements Command {
    private final Drawing drawing;
    private final List<Integer> indices;
    private Group group;

    /**
     * @param drawing dessin concerné
     * @param indices indices des formes à regrouper
     */
    public GroupCommand(Drawing drawing, List<Integer> indices) {
        this.drawing = drawing;
        this.indices = indices;
    }

    /**
     * Crée le Group, retire les formes ciblées, ajoute le Group au dessin.
     */
    @Override
    public void execute() {
        group = new Group(drawing.getShapeAt(indices.get(0)).getColor());
        indices.sort((a,b) -> b-a); // décroissant
        for (int idx : indices)
            group.addShape(drawing.getShapeAt(idx));
        for (int idx : indices)
            drawing.remove(idx);
        drawing.addShape(group);
    }

    /**
     * Dissocie le Group et remet chaque forme à part.
     */
    @Override
    public void unexecute() {
        drawing.removeShape(group);
        for (Shape s : group.getShapes())
            drawing.addShape(s);
    }
}
