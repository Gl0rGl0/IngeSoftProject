package V5.Ingsoft.model.helper;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.interfaces.DBWithStatus;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> implements DBWithStatus {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public DBVolontarioHelper(ArrayList<Volontario> list) {
        super(PersonaType.VOLONTARIO, list);
    }

    public void resetAllAvailability() {
        getItems().forEach(Volontario::clearAvailability);
    }
}