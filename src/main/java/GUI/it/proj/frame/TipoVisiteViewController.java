package GUI.it.proj.frame;

import java.util.List;

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
        Payload<?> res = Launcher.controller.interpreter("remove -T \"" + tipoVisitaTitle + "\"");

        System.out.println("RIMUOVO " + tipoVisitaTitle);
        if(res != null && res.getStatus() == Status.INFO)
            refreshItems();
        
        Launcher.toast(res);
    }

    @FXML
    public void onAggiungiVisitaClick() {
        parent.addVisita();
    }

    @Override
    public void refreshItems() {
        Payload<?> res = Launcher.controller.interpreter("list -T");

        if(res != null && res.getStatus() == Status.INFO){
            this.listVisita.getItems().clear();
            List<TipoVisita> insert = ((Payload<List<TipoVisita>>) res).getData();
            insert.sort((v1, v2) -> v1.sort(v2) );
            listVisita.getItems().addAll(insert);
        }
    }

    public void closeDialog() {
        parent.closeDialog();
    }
}
