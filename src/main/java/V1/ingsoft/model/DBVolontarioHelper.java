package V1.ingsoft.model;

import V1.ingsoft.controller.item.persone.PersonaType;
import V1.ingsoft.controller.item.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO);
    }

    public Volontario getVolontarioByUID(String volontarioUID) {
        return super.cachedPersons.get(volontarioUID);
    }
}