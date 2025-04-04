package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.Configuratore;
import V4.Ingsoft.controller.item.persone.PersonaType;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {
    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE);
        if (cachedItems.isEmpty()) {
            Configuratore admin;
            try {
                admin = new Configuratore(new String[]{"ADMIN, PASSWORD"});
            } catch (Exception e) {
                return;
            }
            admin.setAsNotNew();
            cachedItems.put("ADMIN", admin);
        }
    }

    public boolean addConfiguratore(Configuratore c) {
        if (cachedItems.get(c.getUsername()) != null)
            return false;

        cachedItems.put(c.getUsername(), c);
        return saveJson(getPersonList());
    }
}
