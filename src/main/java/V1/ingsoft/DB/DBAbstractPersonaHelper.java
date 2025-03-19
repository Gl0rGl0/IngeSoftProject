package V1.ingsoft.DB;

import V1.ingsoft.persone.Persona;
import V1.ingsoft.persone.PersonaType;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper<T> {
    protected final HashMap<String, T> cachedPersons = new HashMap<>();

    @SuppressWarnings("unchecked")
    public DBAbstractPersonaHelper(PersonaType personaType) {
        super(personaType.getFilePath(), (Class<T>) personaType.getPersonaClass());
        // INIT DATABASE
        getJson().forEach(p -> cachedPersons.put(p.getUsername(), p));
    }

    public ArrayList<T> getPersonList() {
        return new ArrayList<>(cachedPersons.values());
    }

    public boolean addPersona(T persona) {
        if (cachedPersons.get(persona.getUsername()) != null)
            return false;

        cachedPersons.put(persona.getUsername(), persona);
        return saveJson(getPersonList());
    }

    synchronized public boolean removePersona(String username) {
        if (cachedPersons.remove(username) == null)
            return false;

        return saveJson(getPersonList());
    }

    public boolean changePassword(String username, String newPsw) {
        T toChange = cachedPersons.get(username);

        if (toChange == null)
            return false;

        try {
            Constructor<T> constructor;
            constructor = clazz.getConstructor(String.class, String.class, boolean.class);
            T newPersona = constructor.newInstance(username, DBAbstractPersonaHelper.securePsw(username, newPsw),
                    false);
            cachedPersons.put(username, newPersona); // AL POSTO DI RIMUOVERE/AGGIUNGERE, SOVRASCRIVO
            return saveJson(getPersonList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public T findPersona(String user) {
        return cachedPersons.get(user);
    }

    // IMPLEMENTATO NELLE SOTTOCLASSI COSI DA RISPETTARE LE VERSIONI
    public Persona login(String user, String psw) {
        return null;
    }

    public static String securePsw(String user, String psw) {
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    public boolean isNew() {
        return cachedPersons.size() == 0;
    }

    public void close() {
        saveJson(getPersonList());
    }
}
