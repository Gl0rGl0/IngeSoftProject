package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FruitoriViewController implements ListBase<Visita> {
    public static final String ID = "fruitori";

    @FXML
    private ListView<Visita> listVisite;

    @FXML
    private void initialize() {
        VBox.setVgrow(listVisite, Priority.ALWAYS);

        listVisite.setCellFactory(e -> new Cell<Visita>(this, TipoVisiteViewController.ID, false));
        Payload res = Launcher.controller.interpreter("visit");
        if(res != null && res.getStatus() == Status.OK)
            listVisite.getItems().addAll((Visita[]) res.getData());
    }

    @Override
    public void removeItem(Visita visita) {
    }

    @Override
    public void addItem(Visita visita) {
    }
}