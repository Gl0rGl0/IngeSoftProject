package ingsoft.DB;

import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;

public abstract class DBAbstractPersonaHelper<T extends Persona> extends DBAbstractHelper {
    private final String fileName;
    private final PersonaType personaType;
    private final Class<T> personaClass;
    protected ArrayList<T> cachedPersons = null;
    private boolean isCacheValid = false;


    public DBAbstractPersonaHelper(PersonaType personaType, Class<T> personaClass) {
        this.personaType = personaType;
        this.personaClass = personaClass;
        this.fileName = personaType.getFilePath() + ".properties";
    }

    @SuppressWarnings("UseSpecificCatch")
    public ArrayList<T> getPersonList() {
        if (isCacheValid && cachedPersons != null) {
            return cachedPersons;
        }
    
        ArrayList<T> persons = new ArrayList<>();
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle proprietà: " + e.getMessage());
            return persons;
        }
    
        int index = 1;
        String keyPrefix = personaType.getFilePath();
        while (true) {
            String username = properties.getProperty(keyPrefix + "." + index + ".username");
            String psw = properties.getProperty(keyPrefix + "." + index + ".psw");
            String newFlag = properties.getProperty(keyPrefix + "." + index + ".new", "0");
    
            if (username == null || psw == null)
                break;
    
            try {
                Constructor<T> constructor = personaClass.getConstructor(String.class, String.class, String.class);
                T persona = constructor.newInstance(username, psw, newFlag);
                persons.add(persona);
            } catch (Exception e) {
                ViewSE.print("Errore durante l'istanziazione della classe " + personaClass.getSimpleName() + ": " + e.getMessage());
            }
            index++;
        }
    
        cachedPersons = persons;
        isCacheValid = true;
        return persons;
    }    

    public boolean addPersona(T persona) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
        int index = 1;
        String keyPrefix = personaType.getFilePath();
        while (true) {
            String existingUsername = properties.getProperty(keyPrefix + "." + index + ".username");
            if (existingUsername == null) {
                properties.setProperty(keyPrefix + "." + index + ".username", persona.getUsername());
                properties.setProperty(keyPrefix + "." + index + ".psw", securePsw(persona.getUsername(), persona.getPsw()));
                properties.setProperty(keyPrefix + "." + index + ".new", persona.getNew());
                try {
                    storeProperties(fileName, properties);
                    isCacheValid = false; // Invalida la cache
                    return true;
                } catch (IOException e) {
                    ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
            }
            if (existingUsername.equals(persona.getUsername())) {
                return false;
            }
            index++;
        }
    }
    
    public boolean removePersona(String username) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle proprietà: " + e.getMessage());
            return false;
        }
    
        int index = 1;
        boolean removed = false;
        String keyPrefix = personaType.getFilePath();
        while (true) {
            String existingUsername = properties.getProperty(keyPrefix + "." + index + ".username");
            if (existingUsername == null)
                break;
            if (existingUsername.equals(username)) {
                properties.remove(keyPrefix + "." + index + ".username");
                properties.remove(keyPrefix + "." + index + ".psw");
                properties.remove(keyPrefix + "." + index + ".new");
                removed = true;
            }
            index++;
        }
    
        if (removed) {
            try {
                storeProperties(fileName, properties);
                isCacheValid = false; // Invalida la cache
                return true;
            } catch (IOException e) {
                ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("UseSpecificCatch")
    public boolean changePassword(String username, String newPsw) {
        ArrayList<T> persons = getPersonList();
        boolean found = false;
        for (T persona : persons) {
            if (persona.getUsername().equals(username)) {
                found = true;
                break;
            }
        }
        
        if (found == false) {
            return false;
        }

        if (!removePersona(username))
            return false;
    
        try {
            Constructor<T> constructor = personaClass.getConstructor(String.class, String.class, String.class);
            T newPersona = constructor.newInstance(username, newPsw, "0");
            boolean result = addPersona(newPersona);
            isCacheValid = false; // Invalida la cache dopo la modifica della password
            return result;
        } catch (Exception e) {
            ViewSE.print("Errore nella modifica della password: " + e.getMessage());
            return false;
        }
    }
    
    public Persona findPersona(String user){
        for (Persona p : cachedPersons) {
            if(p.getUsername().equals(user))
                return p;
        }
        return null;
    }

    public Persona login(String user, String psw){
        //IMPLEMENTATO NELLE SOTTOCLASSI COSI DA RISPETTARE LE VERSIONI
        return null;
    }
}
