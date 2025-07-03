package GUI.it.proj.frame;

import java.util.List;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListDeleter;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LuoghiViewController implements ListDeleter<Luogo> {
    public static final String ID = "luoghi";

    @FXML
    private ListView<Luogo> listLuoghi;

    private LuoghiTipoVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listLuoghi, Priority.ALWAYS);

        listLuoghi.setCellFactory(e -> new Cell<Luogo>(this, ID));
        refreshItems();
    }

    public void setParentController(LuoghiTipoVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(String luogoTitle) {
        Payload<?> res = Launcher.getInstance().controller.interpreter("remove -L \"" + luogoTitle + "\"");

        if(res == null || res.getStatus() == Status.ERROR){
            Launcher.getInstance().toast(res);
        }else{
            refreshItems();
        }
    }
    
    public void addItem(String... luogoDet) {
        if(luogoDet == null || luogoDet.length < 4){
            Launcher.getInstance().toast(Payload.error(null, "Errore nell'aggiunta del luogo"));
            return;
        }

        StringBuilder out = new StringBuilder();
        for(String s : luogoDet){
            out.append("\"").append(s).append("\" ");
        }

        Payload<?> res = Launcher.getInstance().controller.interpreter("add -L " + out.toString());
        
        if(res != null && res.getStatus() == Status.INFO)
            refreshItems();
        else{
            Launcher.getInstance().toast(Payload.error(null, "Errore nell'aggiunta del luogo"));
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
        Payload<?> res = Launcher.getInstance().controller.interpreter("list -L");
        
        if(res != null && res.getStatus() == Status.INFO){
            this.listLuoghi.getItems().clear();
            List<Luogo> insert = ((Payload<List<Luogo>>) res).getData();
            insert.sort((v1, v2) -> ((StatusItem) v1.getStatus()).ordinal() - ((StatusItem) v2.getStatus()).ordinal());
            listLuoghi.getItems().addAll(insert);
        }
    }
}