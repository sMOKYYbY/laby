package g62221.meteo.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.time.LocalDate;

/**
 * JavaFX view for entering weather search parameters.
 * <p>
 * This view includes:
 * <ul>
 *   <li>A text field to enter the city name</li>
 *   <li>A date picker to select the forecast date</li>
 *   <li>A search button with icon</li>
 * </ul>
 * </p>
 *
 * @author g62221
 * @version 3.0
 */
public class InputView {

    /** Text field for city name input */
    private final TextField cityField = new TextField();

    /** Date picker */
    private final DatePicker datePicker = new DatePicker();

    /** Search button */
    private final Button searchButton = new Button();

    /** Main horizontal container */
    private final HBox root = new HBox(10);

    /**
     * Constructs a new input view with initialized components.
     * <p>
     * Configures prompt texts, default date (today), search icon,
     * and centered horizontal layout.
     * </p>
     */
    public InputView() {
        cityField.setPromptText("Enter a city");
        datePicker.setPromptText("Date");
        datePicker.setValue(LocalDate.now());

        ImageView icon = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/search.png")));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        searchButton.setGraphic(icon);

        root.getChildren().addAll(cityField, datePicker, searchButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
    }

    /**
     * Returns the root JavaFX node of this view for inclusion in a scene.
     *
     * @return the root container of type {@link Node}
     */
    public Node getNode() {
        return root;
    }

    /**
     * Returns the search button to allow the controller to attach actions to it.
     *
     * @return the search button
     */
    public Button getSearchButton() {
        return searchButton;
    }

    /**
     * Gets the text entered in the city field.
     *
     * @return the city name entered by the user
     */
    public String getCity() {
        return cityField.getText();
    }

    /**
     * Gets the date selected in the date picker.
     *
     * @return the selected date, or {@code null} if no date is selected
     */
    public LocalDate getDate() {
        return datePicker.getValue();
    }
}
