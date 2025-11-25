package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Observable objects.
 */
public class Observable {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer.
     * @param observer the observer to add.
     */
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes an observer.
     * @param observer the observer to remove.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers.
     */
    protected void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}