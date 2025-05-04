package GUI.it.proj.frame;

import java.util.ArrayList;
import java.util.List;

import GUI.it.proj.utils.Calendar;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.Visita;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VolontariViewController implements ListViewController<Visita> {
    public static final String ID = "volontario";

    @FXML
    private ListView<Visita> listVisite;
    @FXML
    private StackPane calendar;

    @FXML
    private void initialize() {
        VBox.setVgrow(listVisite, Priority.ALWAYS);

        listVisite.setCellFactory(e -> new Cell<Visita>(this, VisiteViewController.ID, false));
        listVisite.getItems().addAll(generateVisite(20));

        Calendar calendarComponent = new Calendar();
        calendar.getChildren().add(calendarComponent.getView());
    }

    @Override
    public void removeItem(Visita visita) {
    }

    @Override
    public void addItem(Visita visita) {
    }

    private List<Visita> generateVisite(int count) {
        List<Visita> visite = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            visite.add(new Visita("visita" + i, "descr" + i));
        }

        visite.add(new Visita("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        return visite;
    }

    @Override
    public void modifyItem(Visita visita) {
    }

}