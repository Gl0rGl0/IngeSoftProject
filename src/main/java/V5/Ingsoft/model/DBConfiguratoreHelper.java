package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.Product;
import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> implements Product {

    public DBConfiguratoreHelper() {
        super(PersonaType.CONFIGURATORE);
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

        if (cachedItems.containsKey(username)) { // Use containsKey for clarity
            AssertionControl.logMessage("Attempted to add duplicate Configuratore: " + username, Payload.Status.WARN, SUB_CLASSNAME);
            return false; // Do not overwrite existing configurator
        }

        boolean success = saveJson(getPersonList());
        if (success) {
            cachedItems.put(username, c);
            AssertionControl.logMessage("Added Configuratore: " + username, Payload.Status.INFO, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("Failed to save JSON after adding Configuratore: " + username, Payload.Status.ERROR, SUB_CLASSNAME);
        }
        return success;
    }
}
