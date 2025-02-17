package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.util.GPS;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DBLuoghiHelper extends DBAbstractHelper {
    private final String fileName = "luoghi.properties";
    // Cache dei luoghi letti dal file
    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private boolean isCacheValid = false;

    public boolean addLuogo(String nome, String descrizione, GPS gps, ArrayList<Visita> visiteCollegate) {
        return addLuogo(new Luogo(nome, descrizione, gps, visiteCollegate));
    }

    /**
     * Legge il file delle proprietà e restituisce la lista dei luoghi.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Luogo.
     */
    public ArrayList<Luogo> getLuoghi() {
        if (isCacheValid && luoghi != null) {
            return luoghi;
        }
    
        ArrayList<Luogo> result = new ArrayList<>();
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento dei luoghi: " + e.getMessage());
            return result;
        }
    
        int index = 1;
        String keyPrefix = "luogo";
        while (true) {
            String nomeLuogo = properties.getProperty(keyPrefix + "." + index + ".nome");
            String descrizioneLuogo = properties.getProperty(keyPrefix + "." + index + ".descrizione");
            String latitudine = properties.getProperty(keyPrefix + "." + index + ".gps.latitudine");
            String longitudine = properties.getProperty(keyPrefix + "." + index + ".gps.longitudine");
    
            // Se uno dei campi obbligatori manca, si interrompe la lettura
            if (nomeLuogo == null || descrizioneLuogo == null || latitudine == null || longitudine == null) {
                break;
            }
    
            GPS gps;
            try {
                gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
            } catch (NumberFormatException ex) {
                ViewSE.print("Errore nel parsing delle coordinate GPS per il luogo " + nomeLuogo + ": " + ex.getMessage());
                index++;
                continue;
            }
    
            // Per semplicità, si crea il luogo con una lista vuota di visite
            // (eventualmente si possono caricare le visite correlate tramite visiteHelper)
            ArrayList<Visita> visiteLuogo = new ArrayList<>();
            Luogo luogo = new Luogo(nomeLuogo, descrizioneLuogo, gps, visiteLuogo);
            result.add(luogo);
            index++;
        }
    
        luoghi = result;
        isCacheValid = true;
        return luoghi;
    }

    /**
     * Aggiunge un nuovo Luogo nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Luogo.
     *
     * @param luogo il luogo da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public boolean addLuogo(Luogo luogo) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
    
        int index = 1;
        String keyPrefix = "luogo";
        while (true) {
            String existingNome = properties.getProperty(keyPrefix + "." + index + ".nome");
            if (existingNome == null) {
                properties.setProperty(keyPrefix + "." + index + ".nome", luogo.getNome());
                properties.setProperty(keyPrefix + "." + index + ".descrizione", luogo.getDescrizione());
                properties.setProperty(keyPrefix + "." + index + ".gps.latitudine", String.valueOf(luogo.getGps().getLatitudine()));
                properties.setProperty(keyPrefix + "." + index + ".gps.longitudine", String.valueOf(luogo.getGps().getLongitudine()));
                try {
                    storeProperties(fileName, properties);
                    isCacheValid = false; // Invalida la cache
                    return true;
                } catch (IOException e) {
                    ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
            }
            // Se il nome esiste già, si interrompe l'aggiunta
            if (luogo.isEqualNome(existingNome)) {
                return false;
            }
            index++;
        }
    }

    /**
     * Rimuove un Luogo dal file delle proprietà, basandosi sul nome.
     * Simile a removePersona(), ma adattato per Luogo.
     *
     * @param nome il nome del luogo da rimuovere
     * @return true se il luogo è stato rimosso, false in caso di errori.
     */
    public boolean removeLuogo(String nome) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
    
        int index = 1;
        boolean removed = false;
        String keyPrefix = "luogo";
        while (true) {
            String existingNome = properties.getProperty(keyPrefix + "." + index + ".nome");
            if (existingNome == null) {
                break;
            }
            if (existingNome.equals(nome)) {
                properties.remove(keyPrefix + "." + index + ".nome");
                properties.remove(keyPrefix + "." + index + ".descrizione");
                properties.remove(keyPrefix + "." + index + ".gps.latitudine");
                properties.remove(keyPrefix + "." + index + ".gps.longitudine");
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
    
    /**
     * Cerca e restituisce un Luogo in cache in base al nome.
     *
     * @param nome il nome del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public Luogo findLuogo(String nome) {
        for (Luogo l : luoghi) {
            if (l.isEqualNome(nome)) {
                return l;
            }
        }
        return null;
    }
}