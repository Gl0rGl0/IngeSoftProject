package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LuoghiViewController implements ListBase<Luogo> {
    public static final String ID = "luoghi";

    @FXML
    private ListView<Luogo> listLuoghi;

    private LuoghiVisiteViewController parent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listLuoghi, Priority.ALWAYS);

        listLuoghi.setCellFactory(e -> new Cell<Luogo>(this, ID, true));
        listLuoghi.getItems().addAll(Launcher.controller.getDB().dbLuoghiHelper.getLuoghi());
    }

    public void setParentController(LuoghiVisiteViewController parent) {
        this.parent = parent;
    }

    @Override
    public void removeItem(Luogo luogo) {
        Payload res = Launcher.controller.interpreter("remove -L " + luogo.getName());
        
        if(res != null && res.getStatus() == Status.OK)
            listLuoghi.getItems().removeIf(p -> p.getName().equals(luogo.getName()));
    }

    public void addItem(Luogo luogo) {
        System.out.println("AGGIUNGO " + luogo.getName());
        listLuoghi.getItems().add(luogo);
    }

    @FXML
    public void onAggiungiLuogoClick() {
        System.out.println("AGGIUNGO");
        parent.addLuogo();
    }

    public void closeDialog(){
        parent.closeDialog();
    }

}