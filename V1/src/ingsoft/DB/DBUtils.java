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
import ingsoft.util.GPS;
import java.util.ArrayList;

public class DBUtils {
    private final DBConfiguratoreHelper dbConfiguratoreHelper;
    private final DBFruitoreHelper dbFruitoreHelper;
    private final DBVolontarioHelper dbVolontarioHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;

    public DBUtils() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper();
        dbDatesHelper = new DBDatesHelper();
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

    public boolean addLuogo(String nome, String descrizione, GPS gps/*, String[] visiteCollegate*/){
        // ArrayList<Visita> vc = new ArrayList<>();
        // for (String nomeVisita : visiteCollegate) {
        //     for (Visita visita : getVisite()) {
        //         if(visita.isName(nomeVisita)){
        //             vc.add(visita);
        //             break;
        //         }
        //     }
        // }
        return dbLuoghiHelper.addLuogo(nome, descrizione, gps/*, vc*/);
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

    public boolean removeLuogo(String nomeLuogo){
        return dbLuoghiHelper.removeLuogo(nomeLuogo);
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

    public Volontario findVolontario(String username){
        return dbVolontarioHelper.findPersona(username);
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

    public ArrayList<Date> getPrecludedDates() {
        return dbDatesHelper.getPrecludedDates();
    }

    public void addPrecludedDate(Date date) {
        dbDatesHelper.addPrecludedDate(date);
    }

    public void removePrecludedDate(Date date) {
        dbDatesHelper.removePrecludedDate(date);
    }

    public Visita getVisitaByName(String titoloVisita){
        return dbVisiteHelper.findVisita(titoloVisita);
    }

    public Luogo getLuogoByName(String nomeLuogo){
        return dbLuoghiHelper.findLuogo(nomeLuogo);
    }
    //IMPLEMENTARE
    // public ArrayList<Visita> getlistaVisiteFromLuogo(String luogo) {
    //     String cerca = luogo.toLowerCase().strip().replaceAll(" ", "");
    //     return visite.stream()
    //             .filter(v -> v.getIDVisita().contains(cerca))
    //             .collect(Collectors.toCollection(ArrayList::new));
    // }

    
    
}