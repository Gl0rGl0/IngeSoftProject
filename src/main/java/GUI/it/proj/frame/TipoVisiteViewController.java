package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
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
        listVisita.getItems().addAll(Launcher.controller.getDB().dbTipoVisiteHelper.getTipiVisita());
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(TipoVisita tipoVisita) {
        System.out.println("RIMUOVO " + tipoVisita.getTitle());
        listVisita.getItems().removeIf(p -> p.getTitle().equals(tipoVisita.getTitle()));
    }

    // @Override
    public void addItem(TipoVisita tipoVisita) {
        System.out.println("AGGIUNGO " + tipoVisita.getTitle());
        listVisita.getItems().add(tipoVisita);
    }

    @Override
    public void modifyItem(TipoVisita tipoVisita, Object luogo) {
        System.out.println("Assign " + tipoVisita.getTitle() + " to " + ((Luogo) luogo).getName());
    }

    @FXML
    public void onAggiungiVisitaClick() {
        System.out.println("AGGIUNGO");
        parent.addVisita();
    }

}
