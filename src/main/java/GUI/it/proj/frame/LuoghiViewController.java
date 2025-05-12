package GUI.it.proj.frame;

import java.util.Collection;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LuoghiViewController implements ListEditer<Luogo> {
    public static final String ID = "luoghi";

    @FXML
    private ListView<Luogo> listLuoghi;

    private LuoghiVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listLuoghi, Priority.ALWAYS);

        listLuoghi.setCellFactory(e -> new Cell<Luogo>(this, ID, true));
        refreshItems();
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(Luogo luogo) {
        Payload res = Launcher.controller.interpreter("remove -L \"" + luogo.getName() + "\"");

        if(res == null)
            return;

        System.out.println("RIMUOVO " + luogo.getName());

        if(res.getStatus() == Status.OK)
            refreshItems();
        else
            Launcher.toast(res);
    }
    
    public void addItem(Luogo luogo) {
        Payload res = Launcher.controller.interpreter("add -L " + luogo.toArray());
        System.out.println("AGGIUNGO " + luogo.getName());

        if(res != null && res.getStatus() == Status.OK)
            refreshItems();
    }

    @FXML
    public void onAggiungiLuogoClick() {
        parent.addLuogo();
    }

    public void closeDialog(){
        parent.closeDialog();
    }

    public void refreshItems() {
        Payload res = Launcher.controller.interpreter("list -L");

        if(res != null && res.getStatus() == Status.OK){
            this.listLuoghi.getItems().clear();
            this.listLuoghi.getItems().addAll(((Payload<Collection<Luogo>>) res).getData());
        }
    }

}