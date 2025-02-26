package ingsoft.DB;

import ingsoft.luoghi.StatusVisita;
import ingsoft.luoghi.TipoVisita;
import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBTipoVisiteHelper extends DBAbstractHelper {

    private final String fileName = "tipiVisita.properties";
    private final HashMap<String, TipoVisita> tipiVisitaRepository = new HashMap<>();

    private boolean isCacheValid = false;

    public TipoVisita getTipiVisitaByUID(String uid) {
        if (isCacheValid) {
            return tipiVisitaRepository.get(uid);
        }
        getTipiVisita();
        return tipiVisitaRepository.get(uid);
    }

    public ArrayList<TipoVisita> getTipiVisita() {
        if (isCacheValid && tipiVisitaRepository != null) {
            return new ArrayList<>(tipiVisitaRepository.values());
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento delle visite: " + e.getMessage());
            return new ArrayList<>();
        }

        tipiVisitaRepository.clear();

        int index = 1;
        String prefix = "tipo.";

        while (true) {
            String titolo = properties.getProperty(prefix + index + ".titolo");
            String descrizione = properties.getProperty(prefix + index + ".descrizione");
            String posizione = properties.getProperty(prefix + index + ".gps");
            String dataInizioPeriodo = properties.getProperty(prefix + index + ".dataInizioPeriodo");
            String dataFinePeriodo = properties.getProperty(prefix + index + ".dataFinePeriodo");
            String oraInizio = properties.getProperty(prefix + index + ".oraInizio");
            String durataVisita = properties.getProperty(prefix + index + ".durataVisita");
            String free = properties.getProperty(prefix + index + ".free");
            String numMinPartecipants = properties.getProperty(prefix + index + ".numMinPartecipants");
            String numMaxPartecipants = properties.getProperty(prefix + index + ".numMaxPartecipants");
            String giorniSettimana = properties.getProperty(prefix + index + ".days");
            String UID = properties.getProperty(prefix + index + ".UID");

            String dataInserimento = properties.getProperty(prefix + index + ".dataInserimento");
            if (titolo == null || descrizione == null || posizione == null || dataInizioPeriodo == null
                    || dataFinePeriodo == null || oraInizio == null || durataVisita == null || free == null
                    || numMinPartecipants == null || numMaxPartecipants == null || UID == null) {
                break;
            }

            // Parsing delle informazioni
            GPS gps = new GPS(posizione);
            Date inizioPeriodo = new Date(dataInizioPeriodo);
            Date finePeriodo = new Date(dataFinePeriodo);
            Date inserimento = new Date(dataInserimento);
            Ora ora = new Ora(oraInizio);
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
                        Integer.parseInt(numMaxPartecipants),
                        giorniSettimana,
                        UID,
                        inserimento);
                tipiVisitaRepository.put(UID, tipo);
            } catch (NumberFormatException ex) {
                ViewSE.print("Errore nella creazione del tipo di visita " + index + ": " + ex.getMessage());
            }
            index++;
        }

        isCacheValid = true;
        return new ArrayList<>(tipiVisitaRepository.values());
    }

    /**
     * Aggiunge un nuovo tipo di visita al file delle proprietà. Ritorna true se
     * l'operazione va a buon fine, false altrimenti.
     */
    public boolean addTipoVisita(TipoVisita toAdd) {
        if (findTipoVisita(toAdd.getTitolo()) != null) {
            return false;
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.log("Errore durante il caricamento del file: " + e.getMessage(), 1, this.getClass().toString());
            return false;
        }
        int index = 1;
        String keyPrefix = "tipo.";
        while (true) {
            String existing = properties.getProperty(keyPrefix + index + ".UID");
            if (existing == null) {
                properties.setProperty(keyPrefix + index + ".titolo", toAdd.getTitolo());
                properties.setProperty(keyPrefix + index + ".descrizione", toAdd.getDescrizione());
                properties.setProperty(keyPrefix + index + ".gps", toAdd.getGps().toString());
                properties.setProperty(keyPrefix + index + ".dataInizioPeriodo",
                        toAdd.getDataInizioPeriodo().toString());
                properties.setProperty(keyPrefix + index + ".dataFinePeriodo",
                        toAdd.getDataFinePeriodo().toString());
                properties.setProperty(keyPrefix + index + ".oraInizio", toAdd.getOraInizio().toString());
                properties.setProperty(keyPrefix + index + ".durataVisita",
                        String.valueOf(toAdd.getDurataVisita()));
                properties.setProperty(keyPrefix + index + ".free", String.valueOf(toAdd.isFree()));
                properties.setProperty(keyPrefix + index + ".numMinPartecipants",
                        String.valueOf(toAdd.getNumMinPartecipants()));
                properties.setProperty(keyPrefix + index + ".numMaxPartecipants",
                        String.valueOf(toAdd.getNumMaxPartecipants()));
                properties.setProperty(keyPrefix + index + ".days",
                        String.valueOf(toAdd.getGiorniString()));
                properties.setProperty(keyPrefix + index + ".UID", toAdd.getUID());

                properties.setProperty(keyPrefix + index + ".dataInserimento", toAdd.getDataInserimento().toString());
                try {
                    storeProperties(fileName, properties);
                    isCacheValid = false;
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
     * Rimuove un tipo di visita dal file delle proprietà, basandosi sul titolo.
     * Ritorna true se la rimozione va a buon fine, false altrimenti.
     */
    public boolean removeTipoVisita(String titolo) {
        if (findTipoVisita(titolo) != null) {
            return false;
        }

        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento del file: " + e.getMessage());
            return false;
        }

        int index = 1;
        boolean removed = false;
        String keyPrefix = "tvisita.";
        while (true) {
            String existing = properties.getProperty(keyPrefix + index + ".titolo");
            if (existing == null) {
                break;
            }
            if (existing.equals(titolo)) {
                properties.remove(keyPrefix + index + ".titolo");
                properties.remove(keyPrefix + index + ".descrizione");
                properties.remove(keyPrefix + index + ".gps");
                properties.remove(keyPrefix + index + ".dataInizioPeriodo");
                properties.remove(keyPrefix + index + ".dataFinePeriodo");
                properties.remove(keyPrefix + index + ".oraInizio");
                properties.remove(keyPrefix + index + ".durataVisita");
                properties.remove(keyPrefix + index + ".free");
                properties.remove(keyPrefix + index + ".numMinPartecipants");
                properties.remove(keyPrefix + index + ".numMaxPartecipants");
                properties.remove(keyPrefix + index + ".UID");

                properties.remove(keyPrefix + index + ".dataInserimento");
                removed = true;
            }
            index++;
        }

        if (removed) {
            try {
                storeProperties(fileName, properties);
                isCacheValid = false;
                return true;
            } catch (IOException e) {
                ViewSE.print("Errore durante il salvataggio delle proprietà: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Cerca un tipo di visita nell'elenco dei tipi caricati in cache, basandosi
     * sul titolo.
     */
    public TipoVisita findTipoVisita(String nomeVisita) {
        for (TipoVisita tipo : getTipiVisita()) {
            if (tipo.getTitolo().equalsIgnoreCase(nomeVisita)) {
                return tipo;
            }
        }
        return null;
    }

    public void checkTipoVisiteAttese(Date d) {
        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStato() == StatusVisita.ATTESA)
                tv.isMeseScaduto(d);
        }
    }

    public ArrayList<TipoVisita> getTipoVisiteIstanziabili() {
        ArrayList<TipoVisita> out = new ArrayList<>();

        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStato() != StatusVisita.ATTESA)
                out.add(tv);
        }
       return out;
    }
}
