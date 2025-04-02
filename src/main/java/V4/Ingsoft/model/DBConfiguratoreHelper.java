package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.Configuratore;
import V4.Ingsoft.controller.item.persone.PersonaType;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {
    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE);
        if (cachedItems.isEmpty()) {
            cachedItems.put("ADMIN", new Configuratore("ADMIN", "PASSWORD", false, true));
        }
    }

    public boolean addConfiguratore(String username, String password) {
        if (cachedItems.get(username) != null)
            return false;

        cachedItems.put(username, new Configuratore(username, password, true, true));
        return saveJson(getPersonList());
    }

    // the psw is saved with a very simple hash encryption + salting with user given
    // that it is unique...
    // @Override
    // public Persona login(String user, String psw) {
    // for (Persona p : getPersonList()) {
    // if (p.getUsername().equals(user)) {
    // if (p.getPsw().equals(DBAbstractPersonaHelper.securePsw(user, psw)))
    // return p;
    // }
    // }
    // return null;
    // }
}
