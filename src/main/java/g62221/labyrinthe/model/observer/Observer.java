package g62221.labyrinthe.model.observer;

/**
 * Interface defining the Observer component of the Observer Design Pattern.
 * <p>
 * Objects implementing this interface (like the View) can register themselves
 * to an {@link Observable} subject (like the Game or Facade). They will be
 * notified whenever the subject's state changes.
 * </p>
 */
public interface Observer {

    /**
     * Called automatically when the observed subject notifies a change.
     * <p>
     * This is the signal for the View to refresh its display based on the
     * new state of the Model.
     * </p>
     */
    void update(); // Méthode appelée par le Modèle pour dire : "J'ai changé, redessine-toi !"
}