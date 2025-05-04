package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class DBConfiguratoreHelper extends DBAbstractPersonaHelper<Configuratore> {
    private static final String CLASSNAME = DBConfiguratoreHelper.class.getSimpleName(); // Added for logging

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
        final String SUB_CLASSNAME = CLASSNAME + ".addConfiguratore";
        if (c == null || c.getUsername() == null || c.getUsername().trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to add null Configuratore or Configuratore with null/empty username.", Payload.Level.ERROR, SUB_CLASSNAME);
            return false;
        }
        String username = c.getUsername();

        if (cachedItems.containsKey(username)) { // Use containsKey for clarity
            AssertionControl.logMessage("Attempted to add duplicate Configuratore: " + username, Payload.Level.WARN, SUB_CLASSNAME);
            return false; // Do not overwrite existing configurator
        }

        cachedItems.put(username, c);
        boolean success = saveJson(getPersonList());
        if (success) {
            AssertionControl.logMessage("Added Configuratore: " + username, Payload.Level.INFO, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("Failed to save JSON after adding Configuratore: " + username, Payload.Level.ERROR, SUB_CLASSNAME);
            // Consider removing from cache if save failed?
            // cachedItems.remove(username);
        }
        return success;
    }
}
