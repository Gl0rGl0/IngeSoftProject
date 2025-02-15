package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.util.GPS;
import ingsoft.util.ViewSE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBLuoghiHelper extends DBAbstractHelper {
    private final String fileName = "luoghi.properties";
    private ArrayList<Luogo> luoghi = new ArrayList<>();
    public boolean finalized = false;
    private final DBVisiteHelper visiteHelper;

    public DBLuoghiHelper(DBVisiteHelper visiteHelper) {
        this.visiteHelper = visiteHelper;
    }

    public ArrayList<Luogo> getLuoghi() {
        refreshLuoghi();
        return luoghi;
    }

    public void finalizeLuoghi() {
        finalized = true;
    }

    public void addLuogo() {
        
    }

    private void refreshLuoghi() {
        // Aggiorna prima le visite
        ArrayList<Visita> visite = visiteHelper.getVisite();
        luoghi.clear();
        Properties properties;
        try {
            properties = loadProperties(fileName);
        } catch (IOException e) {
            ViewSE.print("Errore durante il caricamento dei luoghi: " + e.getMessage());
            return;
        }
        int index = 1;
        while (true) {
            String nomeLuogo = properties.getProperty("luogo." + index + ".nome");
            String descrizioneLuogo = properties.getProperty("luogo." + index + ".descrizione");
            String latitudine = properties.getProperty("luogo." + index + ".gps.latitudine");
            String longitudine = properties.getProperty("luogo." + index + ".gps.longitudine");

            if (nomeLuogo == null || descrizioneLuogo == null)
                break;

            GPS gps;
            try {
                gps = new GPS(Double.parseDouble(latitudine), Double.parseDouble(longitudine));
            } catch (NumberFormatException ex) {
                ViewSE.print("Errore nel parsing delle coordinate GPS per il luogo " + nomeLuogo + ": " + ex.getMessage());
                index++;
                continue;
            }
            // Filtra le visite relative a questo luogo (esempio di matching)
            String search = nomeLuogo.toLowerCase().strip().replaceAll(" ", "");
            ArrayList<Visita> visiteLuogo = visite.stream()
                    .filter(v -> v.getIDVisita().contains(search))
                    .collect(Collectors.toCollection(ArrayList::new));
            Luogo luogo = new Luogo(nomeLuogo, descrizioneLuogo, gps, visiteLuogo);
            luoghi.add(luogo);
            index++;
        }
    }
}