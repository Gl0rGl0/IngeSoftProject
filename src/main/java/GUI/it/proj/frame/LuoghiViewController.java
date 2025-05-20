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

        listLuoghi.setCellFactory(e -> new Cell<Luogo>(this, ID));
        refreshItems();
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(String luogoTitle) {
        Payload res = Launcher.controller.interpreter("remove -L \"" + luogoTitle + "\"");

        if(res == null || res.getStatus() == Status.ERROR){
            Launcher.toast(res);
        }else{
            refreshItems();
        }
    }
    
    public void addItem(String... luogoDet) {
        if(luogoDet == null || luogoDet.length < 4){
            Launcher.toast(Payload.error(null, "Errore nell'aggiunta del luogo"));
            return;
        }

        StringBuilder out = new StringBuilder();
        for(String s : luogoDet){
            out.append("\"").append(s).append("\" ");
        }

        Payload res = Launcher.controller.interpreter("add -L " + out.toString());
        
        if(res != null && res.getStatus() == Status.INFO)
            refreshItems();
        else{
            Launcher.toast(Payload.error(null, "Errore nell'aggiunta del luogo"));
        }
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

        if(res != null && res.getStatus() == Status.INFO){
            this.listLuoghi.getItems().clear();
            this.listLuoghi.getItems().addAll(((Payload<Collection<Luogo>>) res).getData());
        }
    }
}