package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Payload;
import V4.Ingsoft.util.Payload.Status;

import java.util.ArrayList;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper<T> {
    private static final String CLASSNAME = DBAbstractPersonaHelper.class.getSimpleName(); // Added for logging

    @SuppressWarnings("unchecked")
    public DBAbstractPersonaHelper(PersonaType personaType) {
        super(personaType.getFilePath(), (Class<T>) personaType.getPersonaClass());
        // INIT DATABASE
        getJson().forEach(p -> cachedItems.put(p.getUsername(), p));
    }

    public static String securePsw(String user, String psw) {
        // Add null checks for robustness at the beginning
        if (user == null || psw == null) {
            // Log this? Should not happen if called from login which checks args.
            AssertionControl.logMessage("Attempted to secure password with null user or psw.", 1, CLASSNAME + ".securePsw");
            // Returning a default or throwing might be better depending on usage context
            return "invalid_input_hash"; // Return a default non-null string
        }
        // Original logic if inputs are valid
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    public ArrayList<T> getPersonList() {
        return super.getItems();
    }

    synchronized public boolean removePersona(String username) {
        final String SUB_CLASSNAME = CLASSNAME + ".removePersona<" + clazz.getSimpleName() + ">";
        if (username == null || username.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to remove persona with null or empty username.", 1, SUB_CLASSNAME);
            return false;
        }

        if (cachedItems.remove(username) == null) {
            AssertionControl.logMessage("Attempted to remove non-existent persona: " + username, 2, SUB_CLASSNAME);
            return false; // Persona not found
        }

        // Persona was removed from cache, now save
        boolean success = saveJson(getPersonList());
        if (success) {
            AssertionControl.logMessage("Removed persona: " + username, 4, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("Failed to save JSON after removing persona: " + username, 1, SUB_CLASSNAME);
            // Consider adding back to cache if save failed? Requires fetching removed item before remove() call.
        }
        return success;
    }

    public boolean changePassword(String username, String newPsw) {
        final String SUB_CLASSNAME = CLASSNAME + ".changePassword<" + clazz.getSimpleName() + ">";
        if (username == null || username.trim().isEmpty() || newPsw == null || newPsw.isEmpty()) {
            AssertionControl.logMessage("Attempted to change password with null/empty username or new password.", 1, SUB_CLASSNAME);
            return false;
        }

        T toChange = cachedItems.get(username);

        if (toChange == null) {
            AssertionControl.logMessage("Attempted to change password for non-existent user: " + username, 2, SUB_CLASSNAME);
            return false;
        }

        String oldPsw = toChange.getPsw();
        String toSet = DBAbstractPersonaHelper.securePsw(username, newPsw);
        toChange.setPsw(toSet);

        boolean success = saveJson(getPersonList());
        if (success) {
            toChange.setAsNotNew();
            AssertionControl.logMessage("Password changed successfully for user: " + username, 4, SUB_CLASSNAME);
            return true;
        } else {
            AssertionControl.logMessage("Failed to save JSON after changing password for user: " + username, 1, SUB_CLASSNAME);
            toChange.setPsw(oldPsw);
            return false;
        }
    }

    public T getPersona(String user) {
        final String SUB_CLASSNAME = CLASSNAME + ".getPersona<" + clazz.getSimpleName() + ">";
        if (user == null || user.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to get persona with null or empty username.", 1, SUB_CLASSNAME);
            return null;
        }
        return cachedItems.get(user);
    }

    // Login logic seems reasonable, but add null checks
    public Payload login(String user, String psw) {
        final String SUB_CLASSNAME = CLASSNAME + ".login<" + clazz.getSimpleName() + ">";
        if (user == null || user.trim().isEmpty() || psw == null || psw.isEmpty()) {
            AssertionControl.logMessage("Attempted login with null/empty username or password.", 2, SUB_CLASSNAME);
            return new Payload(Status.ERROR, null); // Return error payload
        }

        String securedPsw = DBAbstractPersonaHelper.securePsw(user, psw);

        // Iterate over cached items
        for (T p : cachedItems.values()) {
            if (p == null) {
                AssertionControl.logMessage("Encountered null Persona during login iteration.", 1, SUB_CLASSNAME);
                continue;
            }
            if (p.getUsername() == null || p.getPsw() == null) {
                AssertionControl.logMessage("Encountered Persona with null fields during login iteration.", 1, SUB_CLASSNAME);
                continue;
            }
            if (p.getUsername().equals(user)) {
                if (p.getPsw().equals(securedPsw)) {
                    AssertionControl.logMessage("Successful login for user: " + user, 4, SUB_CLASSNAME);
                    return new Payload(Status.OK, p); // Found user, password matches
                } else {
                    AssertionControl.logMessage("Incorrect password attempt for user: " + user, 3, SUB_CLASSNAME);
                    return new Payload(Status.ERROR, p); // Found user, but password incorrect
                }
            }
        }
        // If no matching user is found:
        AssertionControl.logMessage("Login attempt for non-existent user: " + user, 3, SUB_CLASSNAME);
        return new Payload(Status.ERROR, null);
    }

    public boolean isNew() {
        // Removed duplicated line
        return cachedItems.isEmpty(); // Use isEmpty() for clarity
    }

    public void close() {
        final String SUB_CLASSNAME = CLASSNAME + ".close<" + clazz.getSimpleName() + ">";
        // Consider adding logging
        AssertionControl.logMessage("Closing helper and saving data for " + clazz.getSimpleName(), 4, SUB_CLASSNAME);
        saveJson(getPersonList());
    }
} // End of class
