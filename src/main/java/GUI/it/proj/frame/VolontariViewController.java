package GUI.it.proj.frame;

import java.time.LocalDate;
import java.time.Month;
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
import V5.Ingsoft.util.Date;
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
    private Volontario currentVol;

    @FXML
    private void initialize() {
        VBox.setVgrow(listTipoVisiteAssign, Priority.ALWAYS);

        listTipoVisiteAssign.setCellFactory(e -> new Cell<TipoVisita>(this, TipoVisiteViewController.ID));
        
        Date controllerD = Launcher.getInstance().controller.date.clone();
        controllerD.addDay(16).addMonth(1);

        calendarComponent = new Calendar(controllerD, dates -> {
            updateCalendar(dates);
        });

        calendar.getChildren().add(calendarComponent.getView());

        Persona current = Launcher.getInstance().controller.getCurrentUser();
        if(current.getType().equals(PersonaType.VOLONTARIO)){
            currentVol = (Volontario) current;
            refreshDisponibilita();
        }
        
    }
    
    @Override
    public void refreshItems() {
        Persona current = Launcher.getInstance().controller.getCurrentUser();
        if(currentVol == null || !currentVol.getType().equals(PersonaType.VOLONTARIO))
            currentVol = (Volontario) current;

        Payload<?> res = Launcher.getInstance().controller.interpreter("myvisit");
        
        if(res != null && res.getStatus() == Status.INFO){
            this.listTipoVisiteAssign.getItems().clear();
            this.listTipoVisiteAssign.getItems().addAll(((Payload<Collection<TipoVisita>>) res).getData());
        }

        refreshDisponibilita();
    }
    
    private void refreshDisponibilita(){
        if(currentVol == null) return;
        Date d = Launcher.getInstance().controller.date;
        Month m = d.getMonth();
        
        if(d.getDay() > 16)
            m = m.plus(1);
        
        int monthLenght = m.maxLength();
        boolean[] sel = currentVol.getAvailability();
        calendarComponent.setSelected(null);

        Date set = null;
        try {
            set = new Date(1, m.getValue(), d.getYear());
        } catch (Exception e) { e.printStackTrace(); }
        
        if(set == null) return;
        
        for(int i = 0; i < monthLenght; i++){
            if(sel[i]){
                System.out.println(i+1);
                calendarComponent.setSingleSelected(set);
            }
            set.addDay(1);
        }
            
    }

    @Override
    public void updateCalendar(List<LocalDate> dates) {
        StringBuilder out = new StringBuilder();
        for (LocalDate localDate : dates) {
            out.append(String.format(" %s/%s/%s", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()));
        }
        Payload<?> res = Launcher.getInstance().controller.interpreter("setav -b " + out);
        Launcher.getInstance().toast(res);
    }
}