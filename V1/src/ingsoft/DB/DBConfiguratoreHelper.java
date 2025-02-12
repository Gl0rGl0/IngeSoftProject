package ingsoft.DB;

import ingsoft.persone.Configuratore;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {
    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE, Configuratore.class);
    }
    
    @Override
    public Persona login(String user, String psw){
        for (Persona p : cachedPersons) {
            if(p.getUsername().equals(user)){
                if(p.getPsw().equals(DBAbstractPersonaHelper.securePsw(user, psw)))
                    return p;
            }
        }
        return null;
    }
}
