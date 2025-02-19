package ingsoft.DB;

import ingsoft.luoghi.Visita;
import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DBVisiteHelper extends DBAbstractHelper {
    private final String fileName = "visite.properties";
    private final ArrayList<Visita> visite_disponibili = new ArrayList<>();
    private int lastID = 0; // eventualmente per generare ID univoci

    public ArrayList<Visita> getVisite() {
        refreshVisite();
        return visite_disponibili;
    }

    @SuppressWarnings("UseSpecificCatch")
    private void refreshVisite() {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle visite: " + e.getMessage());
            return;
        }
        visite_disponibili.clear();

        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".titolo")) {
                // Supponiamo chiave del tipo "visita.<id>.titolo"
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

                // Parsing delle informazioni
                GPS gps;
                try {
                    gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
                } catch (NumberFormatException ex) {
                    ViewSE.print("Errore nel parsing delle coordinate GPS per la visita " + visitaID + ": " + ex.getMessage());
                    continue;
                }
                Date inizioPeriodo, finePeriodo;
                try {
                    inizioPeriodo = new Date(dataInizioPeriodo);
                    finePeriodo = new Date(dataFinePeriodo);
                } catch (Exception e) {
                    ViewSE.print("Errore nel parsing delle date per la visita " + visitaID + ": " + e.getMessage());
                    continue;
                }
                Ora ora;
                try {
                    ora = new Ora(oraInizio);
                } catch (Exception e) {
                    ViewSE.print("Errore nel parsing dell'ora per la visita " + visitaID + ": " + e.getMessage());
                    continue;
                }
                try {
                    Visita visita = new Visita(
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
                    visite_disponibili.add(visita);
                } catch (Exception ex) {
                    ViewSE.print("Errore nella creazione della visita " + visitaID + ": " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Aggiunge una nuova visita al file delle proprietà.
     * Ritorna true se l'operazione va a buon fine, false altrimenti.
     */
    public boolean addVisita(Visita visita) {
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
            String existingTitolo = properties.getProperty(keyPrefix + "." + index + ".titolo");
            if (existingTitolo == null) {
                properties.setProperty(keyPrefix + "." + index + ".titolo", visita.getTitolo());
                properties.setProperty(keyPrefix + "." + index + ".descrizione", visita.getDescrizione());
                properties.setProperty(keyPrefix + "." + index + ".gps.latitudine", String.valueOf(visita.getGps().getLatitudine()));
                properties.setProperty(keyPrefix + "." + index + ".gps.longitudine", String.valueOf(visita.getGps().getLongitudine()));
                properties.setProperty(keyPrefix + "." + index + ".dataInizioPeriodo", visita.getDataInizioPeriodo().toString());
                properties.setProperty(keyPrefix + "." + index + ".dataFinePeriodo", visita.getDataFinePeriodo().toString());
                properties.setProperty(keyPrefix + "." + index + ".oraInizio", visita.getOraInizio().toString());
                properties.setProperty(keyPrefix + "." + index + ".durataVisita", String.valueOf(visita.getDurataVisita()));
                properties.setProperty(keyPrefix + "." + index + ".free", String.valueOf(visita.isFree()));
                properties.setProperty(keyPrefix + "." + index + ".numMinPartecipants", String.valueOf(visita.getNumMinPartecipants()));
                properties.setProperty(keyPrefix + "." + index + ".numMaxPartecipants", String.valueOf(visita.getNumMaxPartecipants()));
                System.out.println(visita.getOraInizio().toString());
                try {
                    storeProperties(fileName, properties);
                    // Invalida la cache o aggiorna l'elenco se necessario
                    return true;
                } catch (IOException e) {
                    ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
            }
            if (existingTitolo.equalsIgnoreCase(visita.getTitolo())) {
                // Esiste già una visita con lo stesso titolo
                return false;
            }
            index++;
        }
    }

    /**
     * Rimuove una visita dal file delle proprietà, basandosi sul titolo.
     * Ritorna true se la rimozione va a buon fine, false altrimenti.
     */
    public boolean removeVisita(String titolo) {
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
        while (true) {
            String existingTitolo = properties.getProperty(keyPrefix + "." + index + ".titolo");
            if (existingTitolo == null)
                break;
            if (existingTitolo.equalsIgnoreCase(titolo)) {
                properties.remove(keyPrefix + "." + index + ".titolo");
                properties.remove(keyPrefix + "." + index + ".descrizione");
                properties.remove(keyPrefix + "." + index + ".gps.latitudine");
                properties.remove(keyPrefix + "." + index + ".gps.longitudine");
                properties.remove(keyPrefix + "." + index + ".dataInizioPeriodo");
                properties.remove(keyPrefix + "." + index + ".dataFinePeriodo");
                properties.remove(keyPrefix + "." + index + ".oraInizio");
                properties.remove(keyPrefix + "." + index + ".durataVisita");
                properties.remove(keyPrefix + "." + index + ".free");
                properties.remove(keyPrefix + "." + index + ".numMinPartecipants");
                properties.remove(keyPrefix + "." + index + ".numMaxPartecipants");
                removed = true;
            }
            index++;
        }
    
        if (removed) {
            try {
                storeProperties(fileName, properties);
                return true;
            } catch (IOException e) {
                ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Cerca una visita nell'elenco delle visite caricate in cache, basandosi sul titolo.
     */
    public Visita findVisita(String nomeVisita) {
        for (Visita visita : visite_disponibili) {
            if (visita.getTitolo().equalsIgnoreCase(nomeVisita))
                return visita;
        }
        return null;
    }
}