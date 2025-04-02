package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public boolean addVolontario(String username, String password) {
        if (cachedItems.get(username) != null)
            return false;

        cachedItems.put(username, new Volontario(username, password, true, true));
        return saveJson(getPersonList());
    }

}