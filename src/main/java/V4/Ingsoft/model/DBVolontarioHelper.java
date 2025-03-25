package V4.Ingsoft.model;

import java.util.ArrayList;

import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public Volontario getVolontarioByUID(String volontarioUID) {
        return super.cachedPersons.get(volontarioUID);
    }

    public ArrayList<Volontario> getVolontari() {
        return new ArrayList<>(cachedPersons.values());
    }
}