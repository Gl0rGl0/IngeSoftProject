package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.Product;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.util.Date;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> implements Product{
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