package g62221.labyrinthe.model.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the Subject component of the Observer Design Pattern.
 * <p>
 * This class maintains a registry of {@link Observer} objects and provides
 * methods to attach, detach, and notify them. Any class that wants to be
 * watched by the View (like {@code Game} or {@code LabyrinthFacade}) should extend this class.
 * </p>
 */
public class Observable {

    // Liste de tous les observateurs abonnés (ex: La Vue, le Contrôleur)
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer to receive notifications.
     *
     * @param observer The observer to add.
     */
    public void addObserver(Observer observer) {
        // Ajoute un observateur à la liste (Abonnement).
        // On vérifie s'il n'est pas déjà présent pour éviter les doublons de notification.
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer so it no longer receives notifications.
     *
     * @param observer The observer to remove.
     */
    public void removeObserver(Observer observer) {
        // Retire un observateur de la liste (Désabonnement).
        observers.remove(observer);
    }

    /**
     * Triggers the update method on all registered observers.
     * <p>
     * This method should be called whenever the state of the observable object changes
     * (e.g., after a player moves or a tile is inserted).
     * </p>
     */
    protected void notifyObservers() {
        // Notifie tous les observateurs enregistrés.
        // C'est ici qu'on dit à la Vue : "Hé, le modèle a changé, mets-toi à jour !".
        for (Observer observer : observers) {
            observer.update();
        }
    }
}