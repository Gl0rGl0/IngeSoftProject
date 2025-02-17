package ingsoft.DB;

import ingsoft.util.Date;
import java.util.ArrayList;

public class DBDatesHelper {
    //private final String filePath = "./V1/data/dateSpeciali.txt";
    private final ArrayList<Date> precludedDates = new ArrayList<>();

    public DBDatesHelper() {
    }
    /**
     * Restituisce il set delle date speciali.
     *
     * @return HashSet<Date> delle date speciali
     */
    public ArrayList<Date> getPrecludedDates() {
        return precludedDates;
    }

    // public void loadDatesFromFile() {
    //     File file = new File(filePath);
    //     if (!file.exists()) {
    //         // Se il file non esiste, non c'è nulla da caricare.
    //         return;
    //     }
    //     try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    //         String line;
    //         // Legge il file riga per riga
    //         while ((line = br.readLine()) != null) {
    //             line = line.trim();
    //             if (line.isEmpty()) {
    //                 continue; // salta righe vuote
    //             }
    //             // Crea un oggetto Date usando il costruttore che accetta una Stringa
    //             Date d = new Date(line);
    //             // Aggiunge la data al set senza riscriverla nel file
    //             specialDates.add(d);
    //         }
    //     } catch (IOException e) {
    //         ViewSE.print(e);
    //     }
    // }
    
    // /**
    //  * Aggiunge la data speciale (con commento) nel file in modalità append.
    //  * Restituisce true se l'operazione va a buon fine, false altrimenti.
    //  *
    //  * @param date l'oggetto Date da salvare
    //  * @return true se la scrittura è andata a buon fine, false in caso di errore
    //  */
    public boolean addPrecludedDate(Date date) {
        if (precludedDates.contains(date)) {
            return false;
        } else {
            return precludedDates.add(date);
        }
        // File file = new File(filePath);
        // // Assicura che la directory ./V1/data esista
        // File parentDir = file.getParentFile();
        // if (parentDir != null && !parentDir.exists()) {
        //     parentDir.mkdirs();
        // }
        // // Usa try-with-resources per gestire automaticamente la chiusura dello stream
        // try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
        //     // Scrive la data (usando toString()) e, se presente, il commento
        //     bw.write(date.toString());
        //     String comment = date.getDataMessage();
        //     if (comment != null && !comment.isEmpty()) {
        //         // Usa lo stesso delimitatore atteso dal costruttore Date(String)
        //         bw.write("=" + comment);
        //     }
        //     bw.newLine();
        //     return true;
        // } catch (IOException e) {
        //     ViewSE.print(e);
        //     return false;
        // }
    }

    public boolean removePrecludedDate(Date date) {
        return precludedDates.remove(date);
    }
}
