package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.Date;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public boolean addVolontario(Volontario v) {
        if (cachedItems.get(v.getUsername()) != null)
            return false;

        cachedItems.put(v.getUsername(), v);
        return saveJson(getPersonList());
    }

    public void checkVolunteers(Date d) {
        getPersonList().forEach(v -> v.checkStatus(d));
    }

    public void resetAllAvailability() {
        getPersonList().forEach(Volontario::clearAvailability);
    }
}