package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.persone.Volontario;
import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;
import ingsoft.util.ViewSE;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBUtils {

    private final String basePath = "./V1/data/";

    private boolean needsRefreshConfiguratori = true;
    private ArrayList<Configuratore> configuratori = new ArrayList<>();

    private boolean needsRefreshFruitori = true;
    private ArrayList<Fruitore> fruitori = new ArrayList<>();

    private boolean needsRefreshVolontari = true;
    private ArrayList<Volontario> volontari = new ArrayList<>();

    private boolean needsRefreshLuoghi = true;
    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private ArrayList<Visita> visite = new ArrayList<>();

    public DBUtils() {
        refreshConfiguratori();
        refreshFruitori();
        refreshVolontari();
        refreshLuoghi();
    }

    // --- Metodi di accesso alle liste ---

    public ArrayList<Configuratore> getConfiguratori() {
        if (needsRefreshConfiguratori) {
            refreshConfiguratori();
            needsRefreshConfiguratori = false;
        }
        return this.configuratori;
    }

    public ArrayList<Fruitore> getFruitori() {
        if (needsRefreshFruitori) {
            refreshFruitori();
            needsRefreshFruitori = false;
        }
        return this.fruitori;
    }

    public ArrayList<Volontario> getVolontari() {
        if (needsRefreshVolontari) {
            refreshVolontari();
            needsRefreshVolontari = false;
        }
        return this.volontari;
    }

    public ArrayList<Luogo> getLuoghi() {
        if (needsRefreshLuoghi) {
            refreshLuoghi();
            needsRefreshLuoghi = false;
        }
        return this.luoghi;
    }

    // --- Metodi per il refresh dei dati ---

    private void refreshConfiguratori() {
        configuratori.clear();
        this.configuratori = getPersoneDB(PersonaType.CONFIGURATORE);
    }

    private void refreshFruitori() {
        fruitori.clear();
        this.fruitori = getPersoneDB(PersonaType.FRUITORE);
    }

    private void refreshVolontari() {
        volontari.clear();
        this.volontari = getPersoneDB(PersonaType.VOLONTARIO);
    }

    private boolean refreshLuoghi() {
        refreshVisite();
        luoghi.clear();
        Properties properties;
        try {
            properties = loadProperties(basePath + "luoghi.properties");
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento dei luoghi: " + e.getMessage());
            return false;
        }
        int index = 1;
        while (true) {
            String nomeLuogo = properties.getProperty("luogo." + index + ".nome");
            String descrizioneLuogo = properties.getProperty("luogo." + index + ".descrizione");
            String latitudine = properties.getProperty("luogo." + index + ".gps.latitudine");
            String longitudine = properties.getProperty("luogo." + index + ".gps.longitudine");

            if (nomeLuogo == null || descrizioneLuogo == null) {
                break;
            }

            GPS gps;
            try {
                gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
            } catch (NumberFormatException ex) {
                ViewSE.log("Errore nel parsing delle coordinate GPS per il luogo " + nomeLuogo + ": " + ex.getMessage());
                index++;
                continue;
            }

            Luogo luogo = new Luogo(nomeLuogo, descrizioneLuogo, gps, getlistaVisiteFromLuogo(nomeLuogo));
            luoghi.add(luogo);
            index++;
        }
        return true;
    }

    private boolean refreshVisite() {
        Properties properties;
        try {
            properties = loadProperties(basePath + "visite.properties");
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento delle visite: " + e.getMessage());
            return false;
        }
        visite.clear();

        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".titolo")) {
                // Supponiamo che la chiave sia del tipo "visita.<id>.titolo"
                String prefix = "visita.";
                int startIndex = prefix.length();
                int endIndex = key.lastIndexOf(".titolo");
                if (endIndex <= startIndex) continue;
                String visitaID = key.substring(startIndex, endIndex);

                String titolo = properties.getProperty("visita." + visitaID + ".titolo");
                String descrizione = properties.getProperty("visita." + visitaID + ".descrizione");
                String latitudine = properties.getProperty("visita." + visitaID + ".gps.latitudine");
                String longitudine = properties.getProperty("visita." + visitaID + ".gps.longitudine");
                String dataInizioPeriodo = properties.getProperty("visita." + visitaID + ".dataInizioPeriodo");
                String dataFinePeriodo = properties.getProperty("visita." + visitaID + ".dataFinePeriodo");
                String oraInizio = properties.getProperty("visita." + visitaID + ".oraInizio");
                String durataVisita = properties.getProperty("visita." + visitaID + ".durataVisita");
                String free = properties.getProperty("visita." + visitaID + ".free");
                String numMinPartecipants = properties.getProperty("visita." + visitaID + ".numMinPartecipants");
                String numMaxPartecipants = properties.getProperty("visita." + visitaID + ".numMaxPartecipants");

                // Parsing di GPS, Date e Ora
                GPS gps;
                try {
                    gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
                } catch (NumberFormatException ex) {
                    ViewSE.log("Errore nel parsing delle coordinate GPS per la visita " + visitaID + ": " + ex.getMessage());
                    continue;
                }
                Date inizioPeriodo, finePeriodo;
                try {
                    String[] startParts = dataInizioPeriodo.split("-");
                    inizioPeriodo = new Date(
                            Integer.parseInt(startParts[2]),
                            Integer.parseInt(startParts[1]),
                            Integer.parseInt(startParts[0])
                    );
                    String[] endParts = dataFinePeriodo.split("-");
                    finePeriodo = new Date(
                            Integer.parseInt(endParts[2]),
                            Integer.parseInt(endParts[1]),
                            Integer.parseInt(endParts[0])
                    );
                } catch (Exception e) {
                    ViewSE.log("Errore nel parsing delle date per la visita " + visitaID + ": " + e.getMessage());
                    continue;
                }
                Ora ora;
                try {
                    String[] oraParts = oraInizio.split(":");
                    ora = new Ora(Integer.parseInt(oraParts[0]), Integer.parseInt(oraParts[1]));
                } catch (Exception e) {
                    ViewSE.log("Errore nel parsing dell'ora per la visita " + visitaID + ": " + e.getMessage());
                    continue;
                }
                try {
                    Visita visita = new Visita(
                            visitaID,
                            titolo,
                            descrizione,
                            gps,
                            inizioPeriodo,
                            finePeriodo,
                            ora,
                            Integer.parseInt(durataVisita),
                            Boolean.parseBoolean(free),
                            Integer.parseInt(numMinPartecipants),
                            Integer.parseInt(numMaxPartecipants)
                    );
                    visite.add(visita);
                } catch (Exception ex) {
                    ViewSE.log("Errore nella creazione della visita " + visitaID + ": " + ex.getMessage());
                }
            }
        }
        return true;
    }

    public ArrayList<Visita> getlistaVisiteFromLuogo(String luogo) {
        String cerca = luogo.toLowerCase().strip().replaceAll(" ", "");
        return visite.stream()
                .filter(v -> v.getIDVisita().contains(cerca))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // --- Metodi per l'aggiunta/rimozione/aggiornamento delle Persone ---

    public boolean addConfiguratoreToDB(Configuratore toAdd) {
        return addPersonaToDB(toAdd, PersonaType.CONFIGURATORE);
    }

    public boolean addConfiguratoreToDB(String user, String psw) {
        return addConfiguratoreToDB(new Configuratore(user, psw, "1"));
    }

    public boolean addFruitoreToDB(Fruitore toAdd) {
        return addPersonaToDB(toAdd, PersonaType.FRUITORE);
    }

    public boolean addFruitoreToDB(String user, String psw) {
        return addFruitoreToDB(new Fruitore(user, psw, "1"));
    }

    public boolean addVolontarioToDB(Volontario toAdd) {
        return addPersonaToDB(toAdd, PersonaType.VOLONTARIO);
    }

    public boolean addVolontarioToDB(String user, String psw) {
        return addVolontarioToDB(new Volontario(user, psw, "1"));
    }

    public boolean removeConfiguratoreFromDB(String username) {
        return removePersonaFromDB(username, PersonaType.CONFIGURATORE);
    }

    public boolean removeFruitoreFromDB(String username) {
        return removePersonaFromDB(username, PersonaType.FRUITORE);
    }

    public boolean removeVolontarioFromDB(String username) {
        return removePersonaFromDB(username, PersonaType.VOLONTARIO);
    }

    private boolean addPersonaToDB(Persona persona, PersonaType type) {
        if (existsPersona(persona.getUsername(), type)) {
            return false;
        }
        String filePath = basePath + type.getFilePath() + ".properties";
        Properties properties = new Properties();
        File file = new File(filePath);

        // Verifica che la directory esista
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            if (!directory.mkdirs()) {
                ViewSE.log("Errore: impossibile creare la directory " + directory.getAbsolutePath());
                return false;
            }
        }

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    ViewSE.log("Errore: impossibile creare il file " + filePath);
                    return false;
                }
            }
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            }
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }

        int index = 1;
        String keyPrefix = type.getFilePath();
        while (true) {
            String existingUsername = properties.getProperty(keyPrefix + "." + index + ".username");
            if (existingUsername == null) {
                // Aggiunge la nuova persona
                properties.setProperty(keyPrefix + "." + index + ".username", persona.getUsername());
                properties.setProperty(keyPrefix + "." + index + ".psw", securePsw(persona.getUsername(), persona.getPsw()));
                properties.setProperty(keyPrefix + "." + index + ".new", persona.getNew());
                try {
                    storeProperties(filePath, properties);
                } catch (IOException e) {
                    ViewSE.log("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
                // Forza il refresh della lista appropriata
                switch (type) {
                    case CONFIGURATORE -> needsRefreshConfiguratori = true;
                    case FRUITORE -> needsRefreshFruitori = true;
                    case VOLONTARIO -> needsRefreshVolontari = true;
                }
                return true;
            }
            if (existingUsername.equals(persona.getUsername())) {
                return false; // Persona già presente
            }
            index++;
        }
    }

    private boolean removePersonaFromDB(String username, PersonaType type) {
        String filePath = basePath + type.getFilePath() + ".properties";
        Properties properties;
        try {
            properties = loadProperties(filePath);
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento delle proprietà: " + e.getMessage());
            return false;
        }

        int index = 1;
        boolean removed = false;
        String keyPrefix = type.getFilePath();
        while (true) {
            String existingUsername = properties.getProperty(keyPrefix + "." + index + ".username");
            if (existingUsername == null) {
                break;
            }
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
                storeProperties(filePath, properties);
                // Forza il refresh della lista appropriata
                switch (type) {
                    case CONFIGURATORE -> needsRefreshConfiguratori = true;
                    case FRUITORE -> needsRefreshFruitori = true;
                    case VOLONTARIO -> needsRefreshVolontari = true;
                }
                return true;
            } catch (IOException e) {
                ViewSE.log("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean changePsw(Persona persona, String newPsw) {
        if(persona == null)
            return false;
        // Rimuove la persona con la vecchia password
        if (!removePersonaFromDB(persona.getUsername(), persona.type())) {
            return false;
        }
        Persona newPersona;
        switch (persona.type()) {
            case CONFIGURATORE -> newPersona = new Configuratore(persona.getUsername(), newPsw, "0");
            case FRUITORE -> newPersona = new Fruitore(persona.getUsername(), newPsw, "0");
            case VOLONTARIO -> newPersona = new Volontario(persona.getUsername(), newPsw, "0");
            default -> {
                return false;
            }
        }
        return addPersonaToDB(newPersona, persona.type());
    }

    public boolean loginCheckConfiguratore(String user, String psw) {
        return configuratori.stream()
                .filter(c -> c.getUsername().equals(user))
                .anyMatch(c -> c.getPsw().equals(securePsw(user, psw)));
    }

    public boolean existsPersona(String username, PersonaType type) {
        ArrayList<? extends Persona> lista;
        switch (type) {
            case CONFIGURATORE -> lista = configuratori;
            case FRUITORE -> lista = fruitori;
            case VOLONTARIO -> lista = volontari;
            default -> lista = new ArrayList<>();
        }
        return lista.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    public static String securePsw(String user, String psw) {
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    public Configuratore getConfiguratoreFromDB(String user) {
        return configuratori.stream()
                .filter(c -> c.getUsername().equals(user))
                .findFirst().orElse(null);
    }

    public Volontario getVolontarioFromDB(String user) {
        return volontari.stream()
                .filter(v -> v.getUsername().equals(user))
                .findFirst().orElse(null);
    }

    public Fruitore getFruitoreFromDB(String user) {
        return fruitori.stream()
                .filter(f -> f.getUsername().equals(user))
                .findFirst().orElse(null);
    }

    public Persona cercaInDB(String user) {
        Persona persona = getConfiguratoreFromDB(user);
        if (persona != null) {
            return persona;
        }
        persona = getVolontarioFromDB(user);
        if (persona != null) {
            return persona;
        }
        return getFruitoreFromDB(user);
    }

    // --- Metodi di utilità per la gestione dei file .properties ---

    private static Properties loadProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
        return properties;
    }

    private static void storeProperties(String filePath, Properties properties) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            properties.store(fos, null);
        }
    }

    // --- Metodo generico per caricare le Persone dal DB ---

    private <T extends Persona> ArrayList<T> getPersoneDB(PersonaType type) {
        String fileName = type.getFilePath();
        Class<T> personaClass = (Class<T>) type.getPersonaClass();
        Properties properties;
        try {
            properties = loadProperties(basePath + fileName + ".properties");
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento delle proprietà: " + e.getMessage());
            return new ArrayList<>();
        }

        ArrayList<T> persone = new ArrayList<>();
        int index = 1;
        String keyPrefix = fileName;
        while (true) {
            String username = properties.getProperty(keyPrefix + "." + index + ".username");
            String psw = properties.getProperty(keyPrefix + "." + index + ".psw");
            String firstAccess = properties.getProperty(keyPrefix + "." + index + ".new", "0");

            if (username == null || psw == null) {
                break;
            }

            try {
                Constructor<T> constructor = personaClass.getConstructor(String.class, String.class, String.class);
                T persona = constructor.newInstance(username, psw, firstAccess);
                persone.add(persona);
            } catch (Exception e) {
                ViewSE.log("Errore durante l'istanziazione della classe " + personaClass.getSimpleName() + ": " + e.getMessage());
                return new ArrayList<>();
            }
            index++;
        }
        return persone;
    }
}