package GUI.it.proj.frame;

import java.util.Collection;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FruitoriViewController implements ListEditer<Iscrizione> {
    public static final String ID = "fruitori";

    @FXML
    private ListView<Iscrizione> listIscrizioni;

    @FXML
    private void initialize() {
        VBox.setVgrow(listIscrizioni, Priority.ALWAYS);
        
        listIscrizioni.setCellFactory(e -> new Cell<Iscrizione>(this, FruitoriViewController.ID));
        refreshItems();
    }

    @Override
    public void refreshItems() {
        listIscrizioni.getItems().clear();

        Payload<?> res = Launcher.controller.interpreter("myvisit");
        if(res != null && res.getStatus() == Status.INFO)
            listIscrizioni.getItems().addAll((Collection<Iscrizione>) res.getData());
    }

    @Override
    public void removeItem(String item) {
        Payload<?> res = Launcher.controller.interpreter("visit -i " + item);
        if(res != null && res.getStatus() == Status.INFO)
            refreshItems();

        Launcher.toast(res);
    }
}