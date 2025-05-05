package GUI.it.proj.utils;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos; // Potrebbe essere utile per allineamento

/**
 * A custom JavaFX control that combines a TextField for searching
 * and a CheckComboBox for selecting multiple items from a filtered list.
 * The search field filters the items displayed in the CheckComboBox.
 * The items are filtered based on their toString() representation.
 *
 * @param <T> The type of the items in the CheckComboBox.
 */
public class SearchableCheckComboBox<T> extends VBox {

    private final TextField searchField = new TextField();
    private final CheckComboBox<T> checkComboBox;

    // Holds the original, unfiltered list of items
    private ObservableList<T> sourceItems;

    // A filtered view of the sourceItems, used by the CheckComboBox
    private FilteredList<T> filteredItems;

    /**
     * No-argument constructor required for FXML loading.
     * Initializes with an empty observable list.
     */
    public SearchableCheckComboBox() {
        // Call the main constructor with an empty list initially
        this(FXCollections.observableArrayList());
    }

    /**
     * Main constructor to create a SearchableCheckComboBox with initial items.
     * @param items The initial list of items to display and filter.
     */
    public SearchableCheckComboBox(ObservableList<T> items) {
        // Setup the search field
        searchField.setPromptText("Cerca...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterItems(newText);
        });

        // Store the original source items
        this.sourceItems = (items == null) ? FXCollections.observableArrayList() : items;

        // Create a FilteredList wrapping the source items
        // Initially, the predicate is always true, showing all items
        this.filteredItems = new FilteredList<>(this.sourceItems, item -> true);

        // Create the CheckComboBox using the filtered list
        this.checkComboBox = new CheckComboBox<>(this.filteredItems);
        checkComboBox.setMaxWidth(Double.MAX_VALUE); // Allow combo box to grow horizontally

        // Add the search field and the check combo box to this VBox
        getChildren().addAll(searchField, checkComboBox);

        // Set some default VBox properties (optional, can be done in FXML)
        // setSpacing(5);
        // setAlignment(Pos.TOP_LEFT); // Or other alignment if needed
    }

    /**
     * Sets the items for the CheckComboBox. This replaces the entire list
     * of items that the SearchableCheckComboBox will operate on.
     * This method is typically called by FXML when the 'items' property is set,
     * or programmatically to change the data source.
     *
     * @param items The new list of items.
     */
    public void setItems(ObservableList<T> items) {
        // Store the new list as the source
        this.sourceItems = (items == null) ? FXCollections.observableArrayList() : items;

        // Create a new FilteredList wrapping the new source items
        // Initialize with a predicate that shows all items
        FilteredList<T> newFilteredList = new FilteredList<>(this.sourceItems, item -> true);

        // Update the instance field to point to the new filtered list
        this.filteredItems = newFilteredList;

        // Update the CheckComboBox to use the new filtered list
        this.checkComboBox.getItems().clear();
        this.checkComboBox.getItems().addAll(this.filteredItems);

        // Re-apply the current search filter in case there's text in the search field
        filterItems(searchField.getText());
    }

    /**
     * Gets the current list of items displayed in the CheckComboBox
     * (which is the filtered list).
     *
     * @return The observable list of items currently displayed.
     */
    public ObservableList<T> getItems() {
        // Returns the currently active filtered list
        return this.filteredItems;
    }

    /**
     * Gets the underlying CheckComboBox instance.
     * Use this to access CheckComboBox specific methods like getCheckModel().
     *
     * @return The internal CheckComboBox.
     */
    public CheckComboBox<T> getCheckComboBox() {
        return this.checkComboBox;
    }

    /**
     * Gets the CheckModel of the underlying CheckComboBox.
     * This allows you to check/uncheck items and get the list of checked items.
     *
     * @return The CheckModel of the internal CheckComboBox.
     */
    public CheckModel<T> getCheckModel() {
         return this.checkComboBox.getCheckModel();
    }

     /**
     * Gets the list of items that are currently checked in the CheckComboBox.
     * This is a convenience method equivalent to getCheckModel().getCheckedItems().
     *
     * @return An ObservableList of the currently checked items.
     */
    public ObservableList<T> getCheckItems() {
        return getCheckModel().getCheckedItems();
    }


    /**
     * Applies the filtering predicate based on the search text.
     * This method is called by the search field's text listener
     * and by the setItems method.
     *
     * @param searchText The text entered in the search field.
     */
    private void filterItems(String searchText) {
        // Handle null or empty search text - show all items
        if (searchText == null || searchText.trim().isEmpty()) {
            this.filteredItems.setPredicate(item -> true);
        } else {
            // Case-insensitive search based on the item's toString()
            String lowerCaseFilter = searchText.trim().toLowerCase();
            this.filteredItems.setPredicate(item -> {
                // Handle null items in the list
                if (item == null) {
                    return false;
                }
                // Filter logic: check if the item's string representation contains the search text
                return item.toString().toLowerCase().contains(lowerCaseFilter);
            });
        }
    }
}
