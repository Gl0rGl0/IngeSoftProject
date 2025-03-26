package V4.Ingsoft.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper<T> {
    protected final HashMap<String, T> cachedItems = new HashMap<>();

    @SuppressWarnings("unchecked")
    public DBAbstractPersonaHelper(PersonaType personaType) {
        super(personaType.getFilePath(), (Class<T>) personaType.getPersonaClass());
        // INIT DATABASE
        getJson().forEach(p -> cachedItems.put(p.getUsername(), p));
    }

    public ArrayList<T> getPersonList() {
        return super.getItems();
    }

    synchronized public boolean removePersona(String username) {
        if (cachedItems.remove(username) == null)
            return false;

        return saveJson(getPersonList());
    }

    public boolean changePassword(String username, String newPsw) {
        T toChange = cachedItems.get(username);

        if (toChange == null)
            return false;

        try {
            Constructor<T> constructor;
            constructor = clazz.getConstructor(String.class, String.class, boolean.class);
            T newPersona = constructor.newInstance(username, DBAbstractPersonaHelper.securePsw(username, newPsw),
                    false);
            cachedItems.put(username, newPersona); // AL POSTO DI RIMUOVERE/AGGIUNGERE, SOVRASCRIVO
            return saveJson(getPersonList());
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage(), 1, this.getClass().getSimpleName());
        }

        return false;
    }

    public T getPersona(String user) {
        return cachedItems.get(user);
    }

    // IMPLEMENTATO NELLE SOTTOCLASSI COSI DA RISPETTARE LE VERSIONI
    public T login(String user, String psw) {
        for (T p : getPersonList()) {
            if (p.getUsername().equals(user)) {
                if (p.getPsw().equals(DBAbstractPersonaHelper.securePsw(user, psw)))
                    return p;
            }
        }
        return null;
    }

    public static String securePsw(String user, String psw) {
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    public boolean isNew() {
        return cachedItems.size() == 0;
    }

    public void close() {
        saveJson(getPersonList());
    }
}
