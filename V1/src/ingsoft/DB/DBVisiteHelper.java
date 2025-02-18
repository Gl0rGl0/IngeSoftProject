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
                    ViewSE.print("Errore nel parsing delle date per la visita " + visitaID + ": " + e.getMessage());
                    continue;
                }
                Ora ora;
                try {
                    String[] oraParts = oraInizio.split(":");
                    ora = new Ora(Integer.parseInt(oraParts[0]), Integer.parseInt(oraParts[1]));
                } catch (Exception e) {
                    ViewSE.print("Errore nel parsing dell'ora per la visita " + visitaID + ": " + e.getMessage());
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
                    visite_disponibili.add(visita);
                } catch (Exception ex) {
                    ViewSE.print("Errore nella creazione della visita " + visitaID + ": " + ex.getMessage());
                }
            }
        }
    }

    public Visita findVisita(String nomeVisita) {
        for (Visita visita : visite_disponibili) {
            if(visita.getTitolo().equalsIgnoreCase(nomeVisita))
                return visita;
        }
        return null;
    }
}