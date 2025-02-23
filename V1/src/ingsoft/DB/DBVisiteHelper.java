package ingsoft.DB;

import ingsoft.luoghi.TipoVisita;
import ingsoft.luoghi.Visita;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBVisiteHelper extends DBAbstractHelper {
    private final DBTipoVisiteHelper visualizzatoreTipoVisite = new DBTipoVisiteHelper();
    private final String fileName = "visite.properties";
    // Cache delle visite lette dal file
    private final HashMap<String, Visita> visiteRepository = new HashMap<>();
    private boolean isCacheValid = false;

    /**
     * Legge il file delle proprietà e restituisce la lista delle visite.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Visita.
     */
    public ArrayList<Visita> getVisite() {
        if (isCacheValid && visiteRepository != null) {
            return new ArrayList<>(visiteRepository.values());
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle visite: " + e.getMessage());
            return new ArrayList<>();
        }

        int index = 1;
        String keyPrefix = "visita.";
        while (true) {
            String nome = properties.getProperty(keyPrefix + index + ".titolo");
            String data = properties.getProperty(keyPrefix + index + ".data");
            String UID = properties.getProperty(keyPrefix + index + ".UID");

            // Se uno dei campi obbligatori manca, si interrompe la lettura
            if (nome == null || data == null || UID == null) {
                break;
            }

            TipoVisita tipo = visualizzatoreTipoVisite.findTipoVisita(nome);
            if (tipo != null) {
                Date d = new Date(data);
                Visita visita = new Visita(tipo, d, UID, tipo.getUID());
                visiteRepository.put(UID, visita);
            }
            index++;
        }

        isCacheValid = true;
        return new ArrayList<>(visiteRepository.values());
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
        String keyPrefix = "visita.";
        while (true) {
            properties.setProperty(keyPrefix + index + ".titolo", toAdd.getTitolo());
            properties.setProperty(keyPrefix + index + ".descrizione", toAdd.getData().toString());
            properties.setProperty(keyPrefix + index + ".UID", toAdd.getUID());
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
        if (findVisita(data, data) != null)
            return false;

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }

        int index = 1;
        boolean removed = false;
        String keyPrefix = "visita.";
        String existingTitolo = properties.getProperty(keyPrefix + index + ".nome");
        String existingData = properties.getProperty(keyPrefix + index + ".data");
        while (true) {
            if (existingTitolo == null || existingData == null)
                break;

            if (existingTitolo.equals(nome) && existingData.equals(data)) {
                properties.remove(keyPrefix + index + ".nome");
                properties.remove(keyPrefix + index + ".data");
                properties.remove(keyPrefix + index + ".UID");
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
        for (Visita v : visiteRepository.values()) {
            if (v.getTitolo().equalsIgnoreCase(titolo) && v.getData().toString().equals(data)) {
                return v;
            }
        }
        return null;
    }

    public ArrayList<Visita> getCompletate() {
        return (ArrayList<Visita>) this.visiteRepository.values(); // IN REALTÀ BISOGNA AGGIUNGERE LA LOGICA DEI 3
                                                                   // GIORNI PRIMA MA PER ORA LASCIAMO COSI FINCHE NON
                                                                   // SI SETUPPA
    }

    // IDEM A SOPRA...
    public ArrayList<Visita> getCancellate() {
        return (ArrayList<Visita>) this.visiteRepository.values(); // IN REALTÀ BISOGNA AGGIUNGERE LA LOGICA DEI 3
                                                                   // GIORNI PRIMA MA PER ORA LASCIAMO COSI FINCHE NON
                                                                   // SI SETUPPA
    }
}
