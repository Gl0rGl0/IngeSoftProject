package GUI.it.proj.frame;

import java.util.ArrayList;
import java.util.List;

import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.Visita;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VisiteViewController implements ListViewController<Visita> {
    public static final String ID = "visite";

    @FXML
    private ListView<Visita> listVisita;

    private LuoghiVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listVisita, Priority.ALWAYS);

        listVisita.setCellFactory(e -> new Cell<Visita>(this, ID, true));
        listVisita.getItems().addAll(generateVisite(20));
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(Visita visita) {
        System.out.println("RIMUOVO " + visita.getTitolo());
        listVisita.getItems().removeIf(p -> p.getTitolo().equals(visita.getTitolo()));
    }

    // @Override
    public void addItem(Visita visita) {
        // AGGIUNGI A DB parent.addVisita...
        System.out.println("AGGIUNGO " + visita.getTitolo());
        listVisita.getItems().add(visita);
    }

    private List<Visita> generateVisite(int count) {
        List<Visita> visite = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            visite.add(new Visita("visita" + i, "descr" + i));
        }
        return visite;
    }

    @Override
    public void modifyItem(Visita visita) {
        System.out.println("MODIFICO " + visita.getTitolo());
        parent.editItem(visita);
    }

    @FXML
    public void onAggiungiVisitaClick() {
        System.out.println("AGGIUNGO");
        parent.addVisita();
    }

}
