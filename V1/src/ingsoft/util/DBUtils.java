package ingsoft.util;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Persona;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;

public class DBUtils {
    private final String basePath = "./V1/data/";

    private boolean hasToRefreshConfiguratori = true;
    private ArrayList<Configuratore> configuratori = new ArrayList<>();

    private boolean hasToRefreshFruitori = true;
    private ArrayList<Fruitore> fruitori = new ArrayList<>();

    private boolean hasToRefreshLuoghi = true;
    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private ArrayList<Visita> visite = new ArrayList<>();

    public ArrayList<Configuratore> getDBconfiguratori(){
        if(hasToRefreshConfiguratori){
            refreshConfiguratori();
        }
        return this.configuratori;
    }

    public ArrayList<Configuratore> getDBfruitori(){
        if(hasToRefreshFruitori){
            refreshFruitori();
        }
        return this.configuratori;
    }

    private void refreshConfiguratori(){
        String configuratoriFilePath = "configuratori";
        configuratori.clear();
        this.configuratori = getPersone(configuratoriFilePath, Configuratore.class);
    }

    private void refreshFruitori(){
        String fruitoriFilePath = "fruitori";
        fruitori.clear();
        this.fruitori = getPersone(fruitoriFilePath, Fruitore.class);
    }

    public boolean addConfiguratoreToDB(Configuratore toAdd) {
        String configuratoriFilePath = basePath + "configuratori.properties";
        Properties properties = loadProperties(configuratoriFilePath);

        // Verifica se l'username esiste già nel file
        int index = 1;
        while (true) {
            String existingUsername = properties.getProperty("configuratori." + index + ".username");
            if (existingUsername == null) {
                // Fine del file, aggiungiamo il nuovo configuratore
                properties.setProperty("configuratori." + index + ".username", toAdd.getUsername());
                properties.setProperty("configuratori." + index + ".psw", toAdd.getPsw());
                try {
                    // Scrive le proprietà nel file
                    storeProperties(basePath, properties);
                    //properties.store(new FileOutputStream(configuratoriFilePath), null);
                } catch (IOException e) {
                    return false; // Operazione fallita
                }

                // Forza il refresh della lista configuratori
                hasToRefreshConfiguratori = true;
                return true; // Operazione riuscita
            }

            // Se troviamo un username uguale, non aggiungiamo
            if (existingUsername.equals(toAdd.getUsername())) {
                return false; // Configuratore già presente
            }

            index++;
        }
    }

    public boolean addConfiguratoreToDB(String user, String psw){
        return addConfiguratoreToDB(new Configuratore(user, psw));
    }

    public boolean checkConfiguratore(String user, String psw) {
        for (Configuratore configuratore : configuratori) {
            if(configuratore.getUsername().equals(user)){
                return configuratore.getPsw().equals(securePsw(user, psw));
            }
        }
        return false;
    }

    public boolean checkFruitore(String user, String psw) {
        for (Fruitore fruitore : fruitori) {
            if(fruitore.getUsername().equals(user)){
                return fruitore.getPsw().equals(securePsw(user, psw));
            }
        }
        return false;
    }

    public static String securePsw(String user, String psw) {
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }

    public Configuratore getConfiguratoreFromDB(String user) {
        for (Configuratore configuratore : configuratori) {
            if(configuratore.getUsername().equals(user)){
                return configuratore;
            }
        }
        return null;
    }

    public ArrayList<Luogo> getDBLuoghi(){
        if(hasToRefreshLuoghi){
            refreshLuoghi();
        }
        return this.luoghi;
    }

    // Funzione per caricare il file .properties
    private static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return properties;
    }

    private static void storeProperties(String filePath, Properties properties) throws IOException {
        try {
            // Scrive le proprietà nel file
            properties.store(new FileOutputStream(filePath), null);
        } catch (IOException e) {
            throw e;
        }
    }

    private <T extends Persona> ArrayList<T> getPersone(String filePath, Class<T> personaClass) {
        Properties properties = loadProperties(basePath + filePath + ".properties");
        ArrayList<T> persone = new ArrayList<>();
        int index = 1;
        while (true) {
            // Leggiamo username e psw per ogni persona
            String username = properties.getProperty(filePath+ "." + index + ".username");
            String psw = properties.getProperty(filePath + "." + index + ".psw");

            // Se non ci sono più dati, usciamo dal ciclo
            if (username == null || psw == null) break;

            try {
                Constructor<T> constructor = personaClass.getConstructor(String.class, String.class);
                T persona = constructor.newInstance(username, psw);
                persone.add(persona);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            index++;
        }
        return persone;
    }

    private void refreshLuoghi() {
        refreshVisite();
        luoghi.clear();
        Properties properties = loadProperties(basePath + "luoghi" + ".properties");
        int index = 1;
        while (true) {
            // Leggiamo il nome e la descrizione di ogni luogo
            String nomeLuogo = properties.getProperty("luogo." + index + ".nome");
            String descrizioneLuogo = properties.getProperty("luogo." + index + ".descrizione");
            String latitudine = properties.getProperty("luogo." + index + ".gps.latitudine");
            String longitudine = properties.getProperty("luogo." + index + ".gps.longitudine");
    
            // Se non ci sono più dati, usciamo dal ciclo
            if (nomeLuogo == null || descrizioneLuogo == null) break;
    
            // Creiamo un nuovo oggetto Luogo e lo aggiungiamo alla lista
            GPS gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
            Luogo luogo = new Luogo(nomeLuogo, descrizioneLuogo, gps, getlistaVisiteFromLuogo(nomeLuogo));
            luoghi.add(luogo);
    
            index++;
        }
    }
    

    private void refreshVisite() {
        Properties properties = loadProperties(basePath + "visite" + ".properties");
        visite.clear(); // Pulisce la lista delle visite prima di aggiornarla
    
        // Itera su tutte le chiavi nel file
        for (String key : properties.stringPropertyNames()) {
            // Verifica se la chiave corrisponde a una visita (contiene ".titolo")
            if (key.endsWith(".titolo")) {
                // Estrae l'ID della visita rimuovendo ".titolo"
                String visitaID = key.substring(7, key.lastIndexOf(".titolo"));
    
                // Legge tutte le proprietà associate all'ID
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
    
                // Crea il GPS
                GPS gps = new GPS(
                    Double.parseDouble(latitudine),
                    Double.parseDouble(longitudine)
                );
    
                // Crea Date e Ora
                String[] dataInizioParts = dataInizioPeriodo.split("-");
                Date inizioPeriodo = new Date(
                    Integer.parseInt(dataInizioParts[2]),
                    Integer.parseInt(dataInizioParts[1]),
                    Integer.parseInt(dataInizioParts[0])
                );
    
                String[] dataFineParts = dataFinePeriodo.split("-");
                Date finePeriodo = new Date(
                    Integer.parseInt(dataFineParts[2]),
                    Integer.parseInt(dataFineParts[1]),
                    Integer.parseInt(dataFineParts[0])
                );
    
                String[] oraParts = oraInizio.split(":");
                Ora ora = new Ora(
                    Integer.parseInt(oraParts[0]),
                    Integer.parseInt(oraParts[1])
                );
    
                // Crea l'oggetto Visita
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
    
                // Aggiunge la visita alla lista
                visite.add(visita);
            }
        }
    }

    public ArrayList<Visita> getlistaVisiteFromLuogo(String luogo){
        String cerca = luogo.toLowerCase().strip().replaceAll(" ", "");
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita visita : visite) {
            if(visita.getIDVisita().contains(cerca))
                out.add(visita);
        }
        return out;
    }
    
}