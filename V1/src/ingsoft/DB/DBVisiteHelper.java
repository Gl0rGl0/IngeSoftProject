package ingsoft.DB;

import ingsoft.ViewSE;
import ingsoft.luoghi.StatusVisita;
import ingsoft.luoghi.Visita;
import ingsoft.util.Date;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBVisiteHelper extends DBAbstractHelper {
    private final String fileName = "archivio.properties";
    private final HashMap<String, Visita> visiteRepository = new HashMap<>();

    /**
     * Legge il file delle proprietà e restituisce la lista delle visite.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Visita.
     */
    public ArrayList<Visita> getVisite() {
        return new ArrayList<>(visiteRepository.values());
    }

    /**
     * Aggiunge una nuova Visita nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Visita.
     *
     * @param visita la visita da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public void addVisita(Visita toAdd) {
        visiteRepository.put(toAdd.getUID(), toAdd);
    }

    /**
     * Rimuove una Visita dal file delle proprietà, basandosi sul nome.
     * Simile a removePersona(), ma adattato per Visita.
     *
     * @param nome il nome della visita da rimuovere
     * @return true se la visita è stata rimossa, false in caso di errori.
     */
    public void removeVisita(String nome, String data) {
        Visita toRemove = findVisita(nome, data);
        if (toRemove == null)
            return;
        visiteRepository.remove(toRemove.getUID());
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
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.COMPLETA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getConfermate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CONFERMATA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getCancellate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CANCELLATA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getVisiteProposte() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.PROPOSTA)
                out.add(v);
        }

        return out;
    }

    public void checkVisiteInTerminazione(Date d) {
        for (Visita v : getVisite()) {
            v.mancano3Giorni(d);
            if (v.getStatus() == StatusVisita.CANCELLATA) {
                scriviVisiteCancellate(v);
                visiteRepository.remove(v.getUID());
            }
        }
    }

    private boolean scriviVisiteCancellate(Visita toAdd) {
        if (!visiteRepository.containsKey(toAdd.getUID())) {
            return false;
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.println("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
        int index = 1;
        String keyPrefix = "archivio.";
        while (true) {
            String existing = properties.getProperty(keyPrefix + index + ".UID");
            if (existing == null) {
                properties.setProperty(keyPrefix + index + ".titolo", toAdd.getTitolo());
                properties.setProperty(keyPrefix + index + ".stato", toAdd.getStatus().toString());
                properties.setProperty(keyPrefix + index + ".data", toAdd.getData().toString());
                properties.setProperty(keyPrefix + index + ".partecipanti",
                        toAdd.getAttualeCapienza() + "/" + toAdd.tipo.getNumMaxPartecipants());
                properties.setProperty(keyPrefix + index + ".UID", toAdd.getUID());

                try {
                    storeProperties(fileName, properties);
                    return true;
                } catch (IOException e) {
                    ViewSE.println("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
            }
            index++;
        }
    }

    public ArrayList<String> getVisiteEffettuate() {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.println("Errore durante il caricamento delle visite: " + e.getMessage());
            return new ArrayList<>();
        }

        int index = 1;
        String keyPrefix = "archivio.";

        ArrayList<String> outA = new ArrayList<>();

        while (true) {
            String titolo = properties.getProperty(keyPrefix + index + ".titolo");
            String status = properties.getProperty(keyPrefix + index + ".stato");
            String data = properties.getProperty(keyPrefix + index + ".data");
            String capienza = properties.getProperty(keyPrefix + index + ".partecipanti");
            String uid = properties.getProperty(keyPrefix + index + ".UID");

            if (uid == null)
                break;

            String out = titolo + " " + status + " " + data + " " + capienza + " " + uid;
            index++;
            outA.add(out);
        }
        return outA;
    }

    public Visita getVisiteByUID(String uid) {
        return visiteRepository.get(uid);
    }
}
