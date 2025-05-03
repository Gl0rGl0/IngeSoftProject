package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.AssertionControl;

public class Guest extends Persona {

    public static volatile Guest instance = null;

    private Guest() throws Exception {
        super("Guest", "GuestPassword", PersonaType.GUEST, false, false);
    }

    public static Guest getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                if (instance == null) {
                    try {
                        instance = new Guest();
                    } catch (Exception e) {
                        AssertionControl.logMessage("Error while building Guest: " + e.getMessage(), 1, "Guest");
                    }
                }
            }
        }
        return instance;
    }
}