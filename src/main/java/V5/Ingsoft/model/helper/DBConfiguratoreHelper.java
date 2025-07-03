package V5.Ingsoft.model.helper;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {

    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE);
        initConfig();
    }

    public DBConfiguratoreHelper(ArrayList<Configuratore> list) {
        super(PersonaType.CONFIGURATORE, list);
        initConfig();
    }

    private void initConfig() {
        if (cachedItems.isEmpty()) {
            Configuratore admin;
            try {
                admin = new Configuratore(new String[]{"ADMIN", "PASSWORD"});
            } catch (Exception e) {
                return;
            }
            admin.setAsNotNew();
            cachedItems.put("ADMIN", admin);
        }
    }

    public boolean addConfiguratore(Configuratore c) {
        final String SUB_CLASSNAME = getClassName() + ".addConfiguratore";
        if (c == null || c.getUsername() == null || c.getUsername().trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to add null Configuratore or Configuratore with null/empty username.", Payload.Status.ERROR, SUB_CLASSNAME);
            return false;
        }

        String username = c.getUsername();
        boolean success = super.addItem(c);

        if (success) {
            AssertionControl.logMessage("Added Configuratore: " + username, Payload.Status.INFO, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("Failed to save JSON after adding Configuratore: " + username, Payload.Status.ERROR, SUB_CLASSNAME);
        }
        return success;
    }
}
