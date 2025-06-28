package GUI.it.proj.frame;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Calendar;
import GUI.it.proj.utils.Calendarable;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VolontariViewController implements ListBase<TipoVisita>, Calendarable {
    public static final String ID = "volontario";

    @FXML private ListView<TipoVisita> listTipoVisiteAssign;
    @FXML private StackPane calendar;

    private Calendar calendarComponent;

    @FXML
    private void initialize() {
        VBox.setVgrow(listTipoVisiteAssign, Priority.ALWAYS);

        listTipoVisiteAssign.setCellFactory(e -> new Cell<TipoVisita>(this, TipoVisiteViewController.ID));
        
        Persona current = Launcher.controller.getCurrentUser();
        if(current.getType().equals(PersonaType.VOLONTARIO))
            listTipoVisiteAssign.getItems().addAll(Model.getInstance().trovaTipoVisiteByVolontario((Volontario)current));

        calendarComponent = new Calendar(Launcher.controller.date.clone().addMonth(1), dates -> {
            updateCalendar(dates);
        });
        calendar.getChildren().add(calendarComponent.getView());
    }

    @Override
    public void refreshItems() {
        Payload<?> res = Launcher.controller.interpreter("myvisit");

        if(res != null && res.getStatus() == Status.INFO){
            this.listTipoVisiteAssign.getItems().clear();
            this.listTipoVisiteAssign.getItems().addAll(((Payload<Collection<TipoVisita>>) res).getData());
        }
    }

    @Override
    public void updateCalendar(List<LocalDate> dates) {
        StringBuilder out = new StringBuilder();
        for (LocalDate localDate : dates) {
            out.append(String.format(" %s/%s/%s", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()));
        }
        Payload<?> res = Launcher.controller.interpreter("setav -b " + out);
        Launcher.toast(res);
    }
}