package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HomeVisiteViewController implements ListBase<Visita> {
    public static final String ID = "home";

    @FXML
    private ListView<Visita> listVisite;

    VisitaIscrizioneDialogController vicontroller;

    @FXML
    private void initialize() {
        VBox.setVgrow(listVisite, Priority.ALWAYS);
        vicontroller = new VisitaIscrizioneDialogController();

        listVisite.setCellFactory(e -> new Cell<Visita>(this, TipoVisiteViewController.ID));
        refreshItems();
    }

    @Override
    public void refreshItems() {
        listVisite.getItems().clear();
        listVisite.getItems().addAll(Model.getInstance().dbVisiteHelper.getVisiteProposte());
    }

    public void closeDialog() {
        throw new UnsupportedOperationException("Unimplemented method 'closeDialog'");
    }


}