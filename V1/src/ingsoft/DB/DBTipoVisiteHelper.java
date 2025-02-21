package ingsoft.DB;

import ingsoft.luoghi.TipoVisita;
import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DBTipoVisiteHelper extends DBAbstractHelper {
    private final String fileName = "tipiVisita.properties";
    private final ArrayList<TipoVisita> tipiVisite = new ArrayList<>();
    //private int lastID = 0; // eventualmente per generare ID univoci

    public ArrayList<TipoVisita> getTipiVisita() {
        refreshTipiVisita();
        return tipiVisite;
    }

    @SuppressWarnings("UseSpecificCatch")
    private void refreshTipiVisita() {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle visite: " + e.getMessage());
            return;
        }
        tipiVisite.clear();

        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".titolo")) {
                // Supponiamo chiave del tipo "tipo.<id>.titolo"
                String prefix = "tipo.";
                int startIndex = prefix.length();
                int endIndex = key.lastIndexOf(".titolo");
                if (endIndex <= startIndex) continue;
                String tipoID = key.substring(startIndex, endIndex);

                String titolo = properties.getProperty(prefix + tipoID + ".titolo");
                String descrizione = properties.getProperty(prefix + tipoID + ".descrizione");
                String latitudine = properties.getProperty(prefix + tipoID + ".gps.latitudine");
                String longitudine = properties.getProperty(prefix + tipoID + ".gps.longitudine");
                String dataInizioPeriodo = properties.getProperty(prefix + tipoID + ".dataInizioPeriodo");
                String dataFinePeriodo = properties.getProperty(prefix + tipoID + ".dataFinePeriodo");
                String oraInizio = properties.getProperty(prefix + tipoID + ".oraInizio");
                String durataVisita = properties.getProperty(prefix + tipoID + ".durataVisita");
                String free = properties.getProperty(prefix + tipoID + ".free");
                String numMinPartecipants = properties.getProperty(prefix + tipoID + ".numMinPartecipants");
                String numMaxPartecipants = properties.getProperty(prefix + tipoID + ".numMaxPartecipants");


                // Parsing delle informazioni
                GPS gps;
                try {
                    gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
                } catch (Exception ex) {
                    ViewSE.print("Errore nel parsing delle coordinate GPS per la visita " + tipoID + ": " + ex.getMessage());
                    continue;
                }
                Date inizioPeriodo, finePeriodo;
                try {
                    inizioPeriodo = new Date(dataInizioPeriodo);
                    finePeriodo = new Date(dataFinePeriodo);
                } catch (Exception e) {
                    ViewSE.print("Errore nel parsing delle date per la visita " + tipoID + ": " + e.getMessage());
                    continue;
                }
                Ora ora;
                try {
                    ora = new Ora(oraInizio);
                } catch (Exception e) {
                    ViewSE.print("Errore nel parsing dell'ora per la visita " + tipoID + ": " + e.getMessage());
                    continue;
                }
                try {
                    TipoVisita tipo = new TipoVisita(
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
                    tipiVisite.add(tipo);
                } catch (Exception ex) {
                    ViewSE.print("Errore nella creazione del tipo di visita " + tipoID + ": " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Aggiunge un nuovo tipo di visita al file delle proprietà.
     * Ritorna true se l'operazione va a buon fine, false altrimenti.
     */
    public boolean addTipoVisita(TipoVisita tipo) {
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }
        int index = 1;
        String keyPrefix = "tipo";
        while (true) {
            String existingTitolo = properties.getProperty(keyPrefix + "." + index + ".titolo");
            if (existingTitolo == null) {
                properties.setProperty(keyPrefix + "." + index + ".titolo", tipo.getTitolo());
                properties.setProperty(keyPrefix + "." + index + ".descrizione", tipo.getDescrizione());
                properties.setProperty(keyPrefix + "." + index + ".gps.latitudine", String.valueOf(tipo.getGps().getLatitudine()));
                properties.setProperty(keyPrefix + "." + index + ".gps.longitudine", String.valueOf(tipo.getGps().getLongitudine()));
                properties.setProperty(keyPrefix + "." + index + ".dataInizioPeriodo", tipo.getDataInizioPeriodo().toString());
                properties.setProperty(keyPrefix + "." + index + ".dataFinePeriodo", tipo.getDataFinePeriodo().toString());
                properties.setProperty(keyPrefix + "." + index + ".oraInizio", tipo.getOraInizio().toString());
                properties.setProperty(keyPrefix + "." + index + ".durataVisita", String.valueOf(tipo.getDurataVisita()));
                properties.setProperty(keyPrefix + "." + index + ".free", String.valueOf(tipo.isFree()));
                properties.setProperty(keyPrefix + "." + index + ".numMinPartecipants", String.valueOf(tipo.getNumMinPartecipants()));
                properties.setProperty(keyPrefix + "." + index + ".numMaxPartecipants", String.valueOf(tipo.getNumMaxPartecipants()));
                try {
                    storeProperties(fileName, properties);
                    // Invalida la cache o aggiorna l'elenco se necessario
                    return true;
                } catch (IOException e) {
                    ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                    return false;
                }
            }
            if (existingTitolo.equalsIgnoreCase(tipo.getTitolo())) {
                // Esiste già una visita con lo stesso titolo
                return false;
            }
            index++;
        }
    }

    /**
     * Rimuove un tipo di visita dal file delle proprietà, basandosi sul titolo.
     * Ritorna true se la rimozione va a buon fine, false altrimenti.
     */
    public boolean removeTipoVisita(String titolo) {
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
     * Cerca un tipo di visita nell'elenco dei tipi caricati in cache, basandosi sul titolo.
     */
    public TipoVisita findTipoVisita(String nomeVisita) {
        for (TipoVisita tipo : tipiVisite) {
            if (tipo.getTitolo().equalsIgnoreCase(nomeVisita))
                return tipo;
        }
        return null;
    }
}