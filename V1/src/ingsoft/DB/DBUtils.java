package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.persone.Volontario;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class DBUtils {
    private final DBConfiguratoreHelper dbConfiguratoreHelper;
    private final DBFruitoreHelper dbFruitoreHelper;
    private final DBVolontarioHelper dbVolontarioHelper;
    private final DBVisiteHelper dbVisiteHelper;
    private final DBLuoghiHelper dbLuoghiHelper;

    public DBUtils() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper(dbVisiteHelper);
        loadDatesFromFile();
    }

    //Getter per tutti gli helper
    public ArrayList<Configuratore> getConfiguratori() {
        return dbConfiguratoreHelper.getPersonList();
    }

    public ArrayList<Fruitore> getFruitori() {
        return dbFruitoreHelper.getPersonList();
    }

    public ArrayList<Volontario> getVolontari() {
        return dbVolontarioHelper.getPersonList();
    }

    public ArrayList<Luogo> getLuoghi() {
        return dbLuoghiHelper.getLuoghi();
    }

    public ArrayList<Visita> getVisite() {
        return dbVisiteHelper.getVisite();
    }

    //Adder con persone già create
    public boolean addConfiguratore(Configuratore c) {
        return dbConfiguratoreHelper.addPersona(c);
    }

    public boolean addFruitore(Fruitore f) {
        return dbFruitoreHelper.addPersona(f);
    }

    public boolean addVolontario(Volontario v) {
        return dbVolontarioHelper.addPersona(v);
    }

    //TOADD adderVisite/adderLuoghi

    //Adder di persone da creare tramite username e psw
    public boolean addConfiguratore(String user, String psw) {
        return dbConfiguratoreHelper.addPersona(new Configuratore(user, psw, "1"));
    }

    public boolean addFruitore(String user, String psw) {
        return dbFruitoreHelper.addPersona(new Fruitore(user, psw, "1"));
    }

    public boolean addVolontario(String user, String psw) {
        return dbVolontarioHelper.addPersona(new Volontario(user, psw, "1"));
    }

    //Remover dato l'username [sufficiente e necessario]
    public boolean removeConfiguratore(String username) {
        return dbConfiguratoreHelper.removePersona(username);
    }

    public boolean removeFruitore(String username) {
        return dbFruitoreHelper.removePersona(username);
    }

    public boolean removeVolontario(String username) {
        return dbVolontarioHelper.removePersona(username);
    }

    //Come prima ogni persona ha il suo Helper che gestisce le chiamate ai giusti ArrayList
    //Return true se il cambio ha successo, false viceversa
    public boolean changePassword(String username, String newPsw, PersonaType tipoPersona) {
        return switch (tipoPersona) {
            case CONFIGURATORE -> dbConfiguratoreHelper.changePassword(username, newPsw);
            case FRUITORE -> dbFruitoreHelper.changePassword(username, newPsw);
            case VOLONTARIO -> dbVolontarioHelper.changePassword(username, newPsw);
            default -> false;
        };
    }

    //Dato che qui non si può trovare direttamente il tipo si usa una ricerca di priorità C->V->F ritornando la persona trovata o null
    public Persona findUser(String username) {
        Persona out;

        out = dbConfiguratoreHelper.findPersona(username);
        if(out != null) return out;

        out = dbVolontarioHelper.findPersona(username);
        if(out != null) return out;

        out = dbFruitoreHelper.findPersona(username);
        if(out != null) return out;

        return null;
    }

    //Come in find si scorrono tutte le persone per priorità C->V->F, implementabile loginIstantaneo con uso del finder + switch e chiamata a (ex) correttohelper.directLogin(user,psw)
    //ritorna la persona che ha effettuato il login o guest in caso di nessun utente trovato
    public Persona login(String user, String psw){
        Persona out;
        out = dbConfiguratoreHelper.login(user, psw);
        if(out != null) return out;

        out = dbVolontarioHelper.login(user, psw);
        if(out != null) return out;

        out = dbFruitoreHelper.login(user, psw);
        if(out != null) return out;
        
        return new Guest();
    }


    //IMPLEMENTARE
    // public ArrayList<Visita> getlistaVisiteFromLuogo(String luogo) {
    //     String cerca = luogo.toLowerCase().strip().replaceAll(" ", "");
    //     return visite.stream()
    //             .filter(v -> v.getIDVisita().contains(cerca))
    //             .collect(Collectors.toCollection(ArrayList::new));
    // }

    private final String filePath = "./V1/data/dateSpeciali.txt";
    private final HashSet<Date> specialDates = new HashSet<>();

    /**
     * Restituisce il set delle date speciali.
     *
     * @return HashSet<Date> delle date speciali
     */
    public HashSet<Date> getSpecialDates() {
        return specialDates;
    }

    private void loadDatesFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            // Se il file non esiste, non c'è nulla da caricare.
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Legge il file riga per riga
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // salta righe vuote
                }
                // Crea un oggetto Date usando il costruttore che accetta una Stringa
                Date d = new Date(line);
                System.out.println(d);
                // Aggiunge la data al set senza riscriverla nel file
                specialDates.add(d);
            }
        } catch (IOException e) {
            ViewSE.print(e);
        }
    }
    
    /**
     * Aggiunge la data speciale (con commento) nel file in modalità append.
     * Restituisce true se l'operazione va a buon fine, false altrimenti.
     *
     * @param date l'oggetto Date da salvare
     * @return true se la scrittura è andata a buon fine, false in caso di errore
     */
    public boolean addDateToDB(Date date) {
        if (specialDates.contains(date)) {
            return false;
        } else {
            specialDates.add(date);
        }
        File file = new File(filePath);
        // Assicura che la directory ./V1/data esista
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        // Usa try-with-resources per gestire automaticamente la chiusura dello stream
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            // Scrive la data (usando toString()) e, se presente, il commento
            bw.write(date.toString());
            String comment = date.getDataMessage();
            if (comment != null && !comment.isEmpty()) {
                // Usa lo stesso delimitatore atteso dal costruttore Date(String)
                bw.write("=" + comment);
            }
            bw.newLine();
            return true;
        } catch (IOException e) {
            ViewSE.print(e);
            return false;
        }
    }
    
}