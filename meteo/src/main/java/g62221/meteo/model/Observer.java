package g62221.meteo.model;

/**
 * Interface defining the contract for observers in the Observer pattern.
 * <p>
 * Classes implementing this interface can register with an {@link Observable}
 * object to receive automatic notifications when weather data changes.
 * </p>
 *
 * @author g62221
 * @version 3.0
 * @see Observable
 * @see WeatherView
 */
public interface Observer {

    /**
     * Callback method automatically invoked when the observable object
     * notifies a state change with new weather data.
     * <p>
     * This method may be called from a thread different from the JavaFX thread,
     * so implementations must use {@code Platform.runLater()} for any
     * user interface updates.
     * </p>
     *
     * @param data the new weather data received from the observable
     */
    void updateWeatherObject(WeatherData data);
}
