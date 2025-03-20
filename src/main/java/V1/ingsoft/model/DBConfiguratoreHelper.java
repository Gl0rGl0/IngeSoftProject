package V1.ingsoft.model;

import V1.ingsoft.controller.item.persone.Configuratore;
import V1.ingsoft.controller.item.persone.Persona;
import V1.ingsoft.controller.item.persone.PersonaType;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {
    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE);
        if (cachedPersons.isEmpty()) {
            cachedPersons.put("ADMIN", new Configuratore("ADMIN", "PASSWORD", false, true));
        }
    }

    // la psw è salvata con una cifratura hash semplicissima + salting con user dato
    // che è univoco...
    @Override
    public Persona login(String user, String psw) {
        for (Persona p : getPersonList()) {
            if (p.getUsername().equals(user)) {
                if (p.getPsw().equals(DBAbstractPersonaHelper.securePsw(user, psw)))
                    return p;
            }
        }
        return null;
    }
}
