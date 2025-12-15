package g62221.dev.ascii.model;

/**
 * Interface du patron de conception Command pour AsciiPaint.
 *
 * Une commande représente une opération réversible sur le modèle (dessin, forme, etc.).
 * Chaque commande doit pouvoir être exécutée (effectuer l’action) et annulée (inverser l’action).
 */
public interface Command {
    /**
     * Exécute la commande (action principale).
     */
    void execute();

    /**
     * Annule la commande (revient à l’état précédent).
     */
    void unexecute();
}
