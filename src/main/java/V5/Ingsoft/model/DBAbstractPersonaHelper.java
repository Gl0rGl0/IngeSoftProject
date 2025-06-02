package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper<T> {

    public DBAbstractPersonaHelper(PersonaType personaType) {
        super(personaType.getFilePath(), (Class<T>) personaType.getPersonaClass());
    }

    public static String securePsw(String user, String psw) {
        // Add null checks for robustness at the beginning
        if (user == null || psw == null) {
            // Log this? Should not happen if called from login which checks args.
            AssertionControl.logMessage("Attempted to secure password with null user or psw.", Payload.Status.ERROR, "SecurePsw");
            // Returning a default or throwing might be better depending on usage context
            return "invalid_input_hash"; // Return a default non-null string
        }
        // Original logic if inputs are valid
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    @Override
    synchronized public boolean removeItem(String username) {
        final String SUB_CLASSNAME = getClassName() + ".removePersona<" + clazz.getSimpleName() + ">";
        if (username == null || username.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to remove persona with null or empty username.", Payload.Status.ERROR, SUB_CLASSNAME);
            return false;
        }

        // Remove Persona from cache, now save
        boolean success = super.removeItem(username);
        if (success) {
            AssertionControl.logMessage("Removed persona: " + username, Payload.Status.INFO, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("Failed to save JSON after removing persona: " + username, Payload.Status.ERROR, SUB_CLASSNAME);
            // Consider adding back to cache if save failed? Requires fetching removed item before remove() call.
        }
        return success;
    }

    public boolean changePassword(String username, String newPsw) {
        final String SUB_CLASSNAME = getClassName() + ".changePassword<" + clazz.getSimpleName() + ">";
        if (username == null || username.trim().isEmpty() || newPsw == null || newPsw.isEmpty()) {
            AssertionControl.logMessage("Attempted to change password with null/empty username or new password.", Payload.Status.ERROR, SUB_CLASSNAME);
            return false;
        }

        T toChange = cachedItems.get(username);

        if (toChange == null) {
            AssertionControl.logMessage("Attempted to change password for non-existent user: " + username, Payload.Status.WARN, SUB_CLASSNAME);
            return false;
        }

        String oldPsw = toChange.getPsw();
        String toSet = DBAbstractPersonaHelper.securePsw(username, newPsw);

        if (oldPsw.equals(toSet)) {
            AssertionControl.logMessage("Attempted to change password with the same psw.", Payload.Status.WARN, SUB_CLASSNAME);
            return false;
        }

        toChange.setPsw(toSet);

        boolean success = saveDB();
        if (success) {
            if (!toChange.isNew())
                toChange.setAsNotNew();
            AssertionControl.logMessage("Password changed successfully for user: " + username, Payload.Status.INFO, SUB_CLASSNAME);
            return true;
        } else {
            AssertionControl.logMessage("Failed to save JSON after changing password for user: " + username, Payload.Status.ERROR, SUB_CLASSNAME);
            toChange.setPsw(oldPsw);
            return false;
        }
    }

    public T getPersona(String user) {
        final String SUB_CLASSNAME = getClassName() + ".getPersona<" + clazz.getSimpleName() + ">";
        if (user == null || user.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to get persona with null or empty username.", Payload.Status.ERROR, SUB_CLASSNAME);
            return null;
        }
        return cachedItems.get(user);
    }

    // Login logic seems reasonable, but add null checks
    public Payload<Persona> login(String user, String psw) {
        final String SUB_CLASSNAME = getClassName() + ".login<" + clazz.getSimpleName() + ">";
        if (user == null || user.trim().isEmpty() || psw == null || psw.isEmpty()) {
            return Payload.warn(null, "Attempted login with null/empty username or password.");
        }

        String securedPsw = DBAbstractPersonaHelper.securePsw(user, psw);

        // Iterate over cached items
        for (T p : cachedItems.values()) {
            if (p == null) {
                AssertionControl.logMessage("Encountered null Persona during login iteration.", Payload.Status.ERROR, SUB_CLASSNAME);
                continue;
            }
            if (p.getUsername() == null || p.getPsw() == null) {
                AssertionControl.logMessage("Encountered Persona with null fields during login iteration.", Payload.Status.ERROR, SUB_CLASSNAME);
                continue;
            }
            if (p.getUsername().equals(user)) {
                if (p.getPsw().equals(securedPsw)) {
                    return Payload.info(p, "Successful login for user: " + user); // Found user, password matches
                } else {
                    return Payload.error(p, "Incorrect password attempt for user: " + user); // Found user, but password incorrect
                }
            }
        }
        // If no matching user is found:
        return Payload.error(null, "Login attempt for non-existent user: " + user);
    }

    public boolean isNew() {
        // Removed duplicated line
        return cachedItems.isEmpty(); // Use isEmpty() for clarity
    }

    public void close() {
        final String SUB_CLASSNAME = getClassName() + ".close<" + clazz.getSimpleName() + ">";
        // Consider adding logging
        AssertionControl.logMessage("Closing helper and saving data for " + clazz.getSimpleName(), Payload.Status.INFO, SUB_CLASSNAME);
        saveDB();
    }
}