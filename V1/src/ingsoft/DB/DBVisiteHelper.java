package ingsoft.DB;

import ingsoft.luoghi.Visita;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DBVisiteHelper extends DBAbstractHelper {
    private final String fileName = "visite.properties";
    // Cache delle visite lette dal file
    private ArrayList<Visita> visite = getVisite();
    private boolean isCacheValid = false;

    /**
     * Legge il file delle proprietà e restituisce la lista delle visite.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Visita.
     */
    public ArrayList<Visita> getVisite() {
        if (isCacheValid && visite != null) {
            return visite;
        }
    
        ArrayList<Visita> result = new ArrayList<>();
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle visite: " + e.getMessage());
            return result;
        }
    
        int index = 1;
        String keyPrefix = "visita";
        while (true) {
            String nome = properties.getProperty(keyPrefix + "." + index + ".titolo");
            String data = properties.getProperty(keyPrefix + "." + index + ".data");
    
            // Se uno dei campi obbligatori manca, si interrompe la lettura
            if (nome == null || data == null) {
                break;
            }
    
            //bisogna fare add visita qui, problema è che per controllare che effettivamente esista un tipo di visita con quel nome bisogna controllare in db
            index++;
        }
    
        visite = result;
        isCacheValid = true;
        return visite;
    }

    /**
     * Aggiunge una nuova Visita nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Visita.
     *
     * @param visita la visita da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public boolean addVisita(Visita toAdd) {
        
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
    
        int index = 1;
        String keyPrefix = "visita";
        while (true) {
            properties.setProperty(keyPrefix + "." + index + ".titolo", toAdd.getTitolo());
            properties.setProperty(keyPrefix + "." + index + ".descrizione", toAdd.getData().toString());
            try {
                storeProperties(fileName, properties);
                isCacheValid = false; // Invalida la cache
                index++;
                return true;
            } catch (IOException e) {
                ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Rimuove una Visita dal file delle proprietà, basandosi sul nome.
     * Simile a removePersona(), ma adattato per Visita.
     *
     * @param nome il nome della visita da rimuovere
     * @return true se la visita è stata rimossa, false in caso di errori.
     */
    public boolean removeVisita(String nome, String data) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
    
        int index = 1;
        boolean removed = false;
        String keyPrefix = "visita";
        String existingTitolo = properties.getProperty(keyPrefix + "." + index + ".nome");
        String existingData = properties.getProperty(keyPrefix + "." + index + ".data");
        while (true) {
            if(existingTitolo == null || existingData == null) break;
            
            if (existingTitolo.equals(nome) && existingData.equals(data)){
                properties.remove(keyPrefix + "." + index + ".nome");
                properties.remove(keyPrefix + "." + index + ".data");
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
     * Cerca e restituisce una Visita in cache in base al nome.
     *
     * @param nome il nome della visita da cercare
     * @return la Visita trovata, oppure null se non esiste.
     */
    public Visita findVisita(String titolo, String data) {
        for (Visita v : visite) {
            if (v.getTitolo().equalsIgnoreCase(titolo) && v.getData().toString().equals(data)) {
                return v;
            }
        }
        return null;
    }   
}
