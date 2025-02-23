package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.util.GPS;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBLuoghiHelper extends DBAbstractHelper {
    private final String fileName = "luoghi.properties";
    private final HashMap<String, Luogo> luoghiRepository = new HashMap<>();
    private boolean isCacheValid = false;

    private ArrayList<String> tipoVisitaUIDs;

    public void aggiungiTipoVisita(String tipoVisitaUID) {
        if (!tipoVisitaUIDs.contains(tipoVisitaUID))
            tipoVisitaUIDs.add(tipoVisitaUID);
    }

    public ArrayList<String> getTipoVisitaUIDs() {
        return this.tipoVisitaUIDs;
    }

    public Luogo getLuogoByUID(String uid) {
        return luoghiRepository.get(uid);
    }

    public boolean addLuogo(String nome, String descrizione, GPS gps) {
        return addLuogo(new Luogo(nome, descrizione, gps));
    }

    public ArrayList<Luogo> getLuoghi() {
        if (isCacheValid && luoghiRepository != null) {
            return new ArrayList<>(luoghiRepository.values());
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento dei luoghi: " + e.getMessage());
            return new ArrayList<>();
        }

        luoghiRepository.clear();

        int index = 1;
        String keyPrefix = "luogo.";
        while (true) {
            String nomeLuogo = properties.getProperty(keyPrefix + index + ".nome");
            String descrizioneLuogo = properties.getProperty(keyPrefix + index + ".descrizione");
            String posizione = properties.getProperty(keyPrefix + index + ".gps");
            String UID = properties.getProperty(keyPrefix + index + ".UID");

            // Se uno dei campi obbligatori manca, si interrompe la lettura
            if (nomeLuogo == null || descrizioneLuogo == null || posizione == null) {
                break;
            }

            GPS gps = new GPS(posizione);
            Luogo luogo = new Luogo(nomeLuogo, descrizioneLuogo, gps, UID);
            luoghiRepository.put(UID, luogo);
            index++;
        }

        isCacheValid = true;
        return new ArrayList<>(luoghiRepository.values());
    }

    /**
     * Aggiunge un nuovo Luogo nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Luogo.
     *
     * @param luogo il luogo da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public boolean addLuogo(Luogo toAdd) {
        if (findLuogo(toAdd.getNome()) != null)
            return false;

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }

        int index = 1;
        String keyPrefix = "luogo.";
        while (true) {
            String existing = properties.getProperty(keyPrefix + index + ".UID");
            if (existing == null) {
                properties.setProperty(keyPrefix + index + ".nome", toAdd.getNome());
                properties.setProperty(keyPrefix + index + ".descrizione", toAdd.getDescrizione());
                properties.setProperty(keyPrefix + index + ".gps", toAdd.getGps().toString());
                properties.setProperty(keyPrefix + index + ".UID", toAdd.getUID());
                try {
                    storeProperties(fileName, properties);
                    isCacheValid = false; // Invalida la cache
                    return true;
                } catch (IOException e) {
                    ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
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
        if (findLuogo(nome) != null)
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
        String keyPrefix = "luogo.";
        while (true) {
            String existingNome = properties.getProperty(keyPrefix + index + ".UID");
            if (existingNome == null) {
                break;
            }
            if (existingNome.equals(nome)) {
                properties.remove(keyPrefix + index + ".nome");
                properties.remove(keyPrefix + index + ".descrizione");
                properties.remove(keyPrefix + index + ".gps");
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
     * Cerca e restituisce un Luogo in cache in base al nome.
     *
     * @param nome il nome del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public Luogo findLuogo(String nome) {
        for (Luogo l : getLuoghi()) {
            if (l.getNome().equalsIgnoreCase(nome)) {
                return l;
            }
        }
        return null;
    }
}