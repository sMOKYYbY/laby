package g62221.meteo.model;

/**
 * Interface defining the contract for observable objects in the Observer pattern.
 * <p>
 * Classes implementing this interface allow observers ({@link Observer})
 * to register for automatic notifications when state changes occur.
 * </p>
 *
 * @author g62221
 * @version 3.0
 * @see Observer
 * @see WeatherApi
 */
public interface Observable {

    /**
     * Registers an observer to receive state change notifications.
     *
     * @param o the observer to register, must not be {@code null}
     * @throws NullPointerException if the observer is {@code null}
     */
    void registerObserver(Observer o);

    /**
     * Removes an observer from the list of registered observers.
     *
     * @param o the observer to remove
     */
    void removeObserver(Observer o);

    /**
     * Notifies all registered observers of a state change by transmitting
     * the updated weather data.
     *
     * @param data the updated weather data to transmit to observers
     */
    void notifyObservers(WeatherData data);
}
