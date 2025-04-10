package V4.Ingsoft.controller.item.persone;

import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;

public class Guest extends Persona {

    private Guest() throws Exception {
        super("Guest", "GuestPassword", PersonaType.GUEST, false, false);
    }

    public static volatile Guest instance = null;

    public static Guest getInstance() {
        if (instance == null) {
            synchronized(Model.class) {
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