package GUI.it.proj.frame;

import java.util.Collection;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Calendar;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VolontariViewController implements ListBase<TipoVisita> {
    public static final String ID = "volontario";

    @FXML
    private ListView<TipoVisita> listTipoVisiteAssign;
    @FXML
    private StackPane calendar;

    @FXML
    private void initialize() {
        VBox.setVgrow(listTipoVisiteAssign, Priority.ALWAYS);

        listTipoVisiteAssign.setCellFactory(e -> new Cell<TipoVisita>(this, TipoVisiteViewController.ID, false));
        
        Persona current = Launcher.controller.getCurrentUser();
        if(current.getType().equals(PersonaType.VOLONTARIO))
            listTipoVisiteAssign.getItems().addAll(Launcher.controller.getDB().trovaTipoVisiteByVolontario((Volontario)current));

        Calendar calendarComponent = new Calendar();
        calendar.getChildren().add(calendarComponent.getView());
    }

    @Override
    public void refreshItems() {
        Payload res = Launcher.controller.interpreter("myvisit");

        if(res != null && res.getStatus() == Status.OK){
            this.listTipoVisiteAssign.getItems().clear();
            this.listTipoVisiteAssign.getItems().addAll(((Payload<Collection<TipoVisita>>) res).getData());
        }
    }
}