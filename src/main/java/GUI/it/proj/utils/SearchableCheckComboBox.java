package GUI.it.proj.utils;

import org.controlsfx.control.CheckComboBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SearchableCheckComboBox<T> extends VBox {
    private final TextField searchField = new TextField();
    private CheckComboBox<T> combo;
    private FilteredList<T> filteredItems;

    // costruttore no-arg richiesto da FXML
    public SearchableCheckComboBox() {
        // inizializzazione minima, lista vuota
        this(FXCollections.observableArrayList());
    }

    public SearchableCheckComboBox(ObservableList<T> items) {
        this.filteredItems = new FilteredList<>(items, t -> true);
        this.combo = new CheckComboBox<>(filteredItems);
        searchField.setPromptText("Cerca…");
        searchField.textProperty().addListener((obs, old, nw) -> {
            String text = (nw == null) ? "" : nw.toLowerCase();
            filteredItems.setPredicate(item -> item.toString().toLowerCase().contains(text));
        });
        getChildren().setAll(searchField, combo);
    }

    /** Setter usabile da FXML con `<SearchableCheckComboBox items="..."/>` */
    public void setItems(ObservableList<T> items) {
        filteredItems = new FilteredList<>(items, t -> true);
        combo.getItems().setAll(filteredItems);
    }

    public CheckComboBox<T> getCheckComboBox() {
        return combo;
    }
}
