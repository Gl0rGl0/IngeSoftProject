package GUI.it.proj.frame;

import java.util.Collection;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TipoVisiteViewController implements ListEditer<TipoVisita> {
    public static final String ID = "visite";

    @FXML
    private ListView<TipoVisita> listVisita;

    private LuoghiVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listVisita, Priority.ALWAYS);

        listVisita.setCellFactory(e -> new Cell<TipoVisita>(this, ID));
        refreshItems();
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(String tipoVisitaTitle) {
        Payload res = Launcher.controller.interpreter("remove -T \"" + tipoVisitaTitle + "\"");

        System.out.println("RIMUOVO " + tipoVisitaTitle);
        if(res != null && res.getStatus() == Status.OK)
            refreshItems();
    }

    @FXML
    public void onAggiungiVisitaClick() {
        System.out.println("AGGIUNGO VISITA");
        parent.addVisita();
    }

    @Override
    public void refreshItems() {
        Payload res = Launcher.controller.interpreter("list -T");

        if(res != null && res.getStatus() == Status.OK){
            this.listVisita.getItems().clear();
            this.listVisita.getItems().addAll(((Payload<Collection<TipoVisita>>) res).getData());
        }
    }

    public void closeDialog() {
        parent.closeDialog();
    }
}
