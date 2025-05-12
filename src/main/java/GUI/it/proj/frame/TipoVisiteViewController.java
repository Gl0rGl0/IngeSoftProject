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

        listVisita.setCellFactory(e -> new Cell<TipoVisita>(this, ID, true));
        refreshItems();
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(TipoVisita tipoVisita) {
        Payload res = Launcher.controller.interpreter("add -T \"" + tipoVisita.getTitle() + "\"");

        System.out.println("RIMUOVO " + tipoVisita.getTitle());
        //listVisita.getItems().removeIf(p -> p.getTitle().equals(tipoVisita.getTitle()));
        if(res != null && res.getStatus() == Status.OK)
            refreshItems();
    }

    // @Override
    public void addItem(TipoVisita tipoVisita) {
        Payload res = Launcher.controller.interpreter("add -T " + tipoVisita.toArray());

        System.out.println("AGGIUNGO " + tipoVisita.getTitle());
        if(res != null && res.getStatus() == Status.OK)
            //Se non è cambiato nulla è inutile refreshare
            //if(!((Payload<Collection<TipoVisita>>) res).getData().equals(this.listVisita.getItems()))
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

}
