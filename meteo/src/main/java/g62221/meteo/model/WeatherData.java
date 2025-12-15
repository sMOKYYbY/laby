package g62221.meteo.model;

import java.time.LocalDate;

/**
 * Immutable Data Transfer Object (DTO) representing weather information
 * for a given city on a specific date.
 * <p>
 * This class is a Java record, guaranteeing immutability and automatically
 * providing {@code equals()}, {@code hashCode()}, {@code toString()} methods
 * and accessors for each component.
 * </p>
 *
 * @param city the name of the city
 * @param date the date for which the weather data is valid
 * @param tempMin the minimum temperature in degrees Celsius
 * @param tempMax the maximum temperature in degrees Celsius
 *
 * @author g62221
 * @version 3.0
 */
public record WeatherData(
        String city,
        LocalDate date,
        double tempMin,
        double tempMax
) { }
