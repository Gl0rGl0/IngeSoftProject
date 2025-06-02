package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.DBWithStatus;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> implements DBWithStatus {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public void resetAllAvailability() {
        getItems().forEach(Volontario::clearAvailability);
    }
}