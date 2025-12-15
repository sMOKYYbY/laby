package g62221.meteo.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Weather data retrieval service implementing the Observable pattern.
 * <p>
 * This class performs two main operations:
 * <ul>
 *   <li>Geocoding a city via OpenStreetMap's Nominatim API</li>
 *   <li>Retrieving min/max temperatures via the Open-Meteo API</li>
 * </ul>
 * </p>
 * <p>
 * All registered observers are automatically notified when new weather
 * data is successfully retrieved.
 * </p>
 *
 * @author g62221
 * @version 3.0
 * @see Observable
 * @see WeatherData
 */
public class WeatherApi implements Observable {

    /** HTTP client for network requests */
    private final HttpClient http = HttpClient.newHttpClient();

    /** JSON mapper for parsing API responses */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Set of registered observers to receive notifications */
    private final Set<Observer> observers = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyObservers(WeatherData data) {
        for (Observer o : observers) {
            o.updateWeatherObject(data);
        }
    }

    /**
     * Fetches weather data for a given city and date.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Validates input parameters</li>
     *   <li>Geocodes the city to obtain latitude and longitude</li>
     *   <li>Queries the Open-Meteo API for temperatures</li>
     *   <li>Parses the JSON response</li>
     *   <li>Automatically notifies all registered observers</li>
     * </ol>
     * </p>
     *
     * @param city the name of the city (non-empty)
     * @param date the target date for weather forecast
     * @return a {@link WeatherData} object containing min/max temperatures
     * @throws WeatherException if parameters are invalid, if the city is not found,
     *                          on network errors, or JSON parsing errors
     */
    public WeatherData fetch(String city, LocalDate date) {
        if (city == null || city.isBlank() || date == null) {
            throw new WeatherException("City and date are required");
        }

        double[] latLon = geocode(city);
        double lat = latLon[0];
        double lon = latLon[1];

        try {
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
                    "&longitude=" + lon +
                    "&daily=temperature_2m_min,temperature_2m_max" +
                    "&timezone=Europe/Brussels" +
                    "&start_date=" + date +
                    "&end_date=" + date;

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(weatherUrl)).build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(resp.body());

            JsonNode daily = root.get("daily");
            if (daily == null) {
                throw new WeatherException("Malformed weather response");
            }

            double tmax = daily.get("temperature_2m_max").get(0).asDouble();
            double tmin = daily.get("temperature_2m_min").get(0).asDouble();
            WeatherData data = new WeatherData(city, date, tmin, tmax);

            notifyObservers(data);
            return data;
        } catch (Exception e) {
            throw new WeatherException("Failed to fetch weather", e);
        }
    }

    /**
     * Performs geocoding of a city to obtain its geographic coordinates.
     * <p>
     * Uses OpenStreetMap's Nominatim API to convert a city name
     * into latitude/longitude coordinates.
     * </p>
     *
     * @param city the name of the city to geocode
     * @return a two-element array {@code [latitude, longitude]}
     * @throws WeatherException if the city is not found or on network errors
     */
    private double[] geocode(String city) {
        try {
            String geoUrl = "https://nominatim.openstreetmap.org/search.php?q=" +
                    encode(city) + "&format=jsonv2";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(geoUrl))
                    .header("User-Agent", "HE2B-WeatherApp-Student")
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode arr = mapper.readTree(resp.body());

            if (!arr.isArray() || arr.isEmpty()) {
                throw new WeatherException("City not found: " + city);
            }

            JsonNode first = arr.get(0);
            double lat = first.get("lat").asDouble();
            double lon = first.get("lon").asDouble();
            return new double[]{lat, lon};
        } catch (WeatherException we) {
            throw we;
        } catch (Exception e) {
            throw new WeatherException("Failed to geocode city", e);
        }
    }

    /**
     * Encodes a string for use in a URL.
     * <p>
     * Performs minimal encoding by replacing spaces with '+'.
     * For complete encoding, use {@code URLEncoder.encode()}.
     * </p>
     *
     * @param s the string to encode
     * @return the encoded string
     */
    private String encode(String s) {
        return s.trim().replace(" ", "+");
    }
}
