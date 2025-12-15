package g62221.meteo.model;

/**
 * Business exception thrown when errors occur during weather data
 * retrieval or processing.
 * <p>
 * This exception can be thrown in the following cases:
 * <ul>
 *   <li>Invalid input parameters (empty city, null date)</li>
 *   <li>Geocoding failure (city not found)</li>
 *   <li>Network or HTTP errors</li>
 *   <li>JSON response parsing errors</li>
 * </ul>
 * </p>
 *
 * @author g62221
 * @version 3.0
 */
public class WeatherException extends RuntimeException {

    /**
     * Constructs a new exception with the specified message.
     *
     * @param message the message describing the error
     */
    public WeatherException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param message the message describing the error
     * @param cause the underlying cause of the exception
     */
    public WeatherException(String message, Throwable cause) {
        super(message, cause);
    }
}
