package V4.Ingsoft.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Payload;
import V4.Ingsoft.util.Payload.Status;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper<T> {

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
            constructor = clazz.getConstructor(String.class, String.class, boolean.class, boolean.class);
            T newPersona = constructor.newInstance(username, newPsw, false, true);
            cachedItems.put(username, newPersona); // INSTEAD OF REMOVING/ADDING, OVERWRITE
            return saveJson(getPersonList());
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage() + "AAAA", 1, this.getClass().getSimpleName());
        }

        return false;
    }

    public T getPersona(String user) {
        return cachedItems.get(user);
    }

    // IMPLEMENTED IN SUBCLASSES TO RESPECT VERSIONS
    public Payload login(String user, String psw) {
        for (T p : getPersonList()) {
            if (p.getUsername().equals(user)) {
                if (p.getPsw().equals(DBAbstractPersonaHelper.securePsw(user, psw))){
                    return new Payload(Status.OK, p);
                }else{
                    return new Payload(Status.ERROR, p);
                }
            }
        }
        return new Payload(Status.ERROR, null);
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
