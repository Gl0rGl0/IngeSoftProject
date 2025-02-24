package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.TipoVisita;
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
    public final DBVolontarioHelper dbVolontarioHelper;
    public final DBTipoVisiteHelper dbTipoVisiteHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;

    // Costruttore e inizializzazione degli helper
    public DBUtils() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbTipoVisiteHelper = new DBTipoVisiteHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper();
        dbDatesHelper = new DBDatesHelper();
    }

    // ================================================================
    // Getters per gli oggetti persistenti
    // ================================================================
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

    public ArrayList<TipoVisita> getTipi() {
        return dbTipoVisiteHelper.getTipiVisita();
    }

    //GETTER VARIABILI
    public ArrayList<TipoVisita> getTipoVisiteIstanziabili(){
        return dbTipoVisiteHelper.getTipoVisiteIstanziabili();
    }

    // ================================================================
    // Adders per oggetti già creati
    // ================================================================
    public boolean addConfiguratore(Configuratore c) {
        return dbConfiguratoreHelper.addPersona(c);
    }

    public boolean addFruitore(Fruitore f) {
        return dbFruitoreHelper.addPersona(f);
    }

    public boolean addVolontario(Volontario v) {
        return dbVolontarioHelper.addPersona(v);
    }

    public boolean addLuogo(String nome, String descrizione, GPS gps) {
        return dbLuoghiHelper.addLuogo(nome, descrizione, gps);
    }

    public boolean addTipoVisita(TipoVisita tv) {
        return dbTipoVisiteHelper.addTipoVisita(tv);
    }

    public void addVisita(Visita visita) {
        dbVisiteHelper.addVisita(visita);
    }

    // public void aggiungiTipoVisita(TipoVisita v) {
    // dbTipoVisiteHelper.addTipoVisita(v);
    // }

    // ================================================================
    // Adders per oggetti da creare tramite username/psw - String
    // ================================================================
    public boolean addConfiguratore(String user, String psw) {
        return dbConfiguratoreHelper.addPersona(new Configuratore(user, psw, "1"));
    }

    public boolean addFruitore(String user, String psw) {
        return dbFruitoreHelper.addPersona(new Fruitore(user, psw, "1"));
    }

    public boolean addVolontario(String user, String psw) {
        return dbVolontarioHelper.addPersona(new Volontario(user, psw, "1"));
    }

    public boolean aggiungiTipoVisita(String[] args, Date d) {
        return dbTipoVisiteHelper.addTipoVisita(new TipoVisita(args, d));
    }

    // ================================================================
    // Remove methods
    // ================================================================
    public boolean removeConfiguratore(String username) {
        return dbConfiguratoreHelper.removePersona(username);
    }

    public boolean removeFruitore(String username) {
        return dbFruitoreHelper.removePersona(username);
    }

    public boolean removeVolontario(String username) {
        return dbVolontarioHelper.removePersona(username);
    }

    public boolean removeTipoVisita(String nomeVisita) {
        return dbTipoVisiteHelper.removeTipoVisita(nomeVisita);
    }

    public void removeVisita(String nomeVisita, String data) {
        dbVisiteHelper.removeVisita(nomeVisita, data);
    }

    public boolean removeLuogo(String nomeLuogo) {
        return dbLuoghiHelper.removeLuogo(nomeLuogo);
    }

    // ================================================================
    // Metodi di aggiornamento (change password)
    // ================================================================
    public boolean changePassword(String username, String newPsw, PersonaType tipoPersona) {
        return switch (tipoPersona) {
            case CONFIGURATORE -> dbConfiguratoreHelper.changePassword(username, newPsw);
            case FRUITORE -> dbFruitoreHelper.changePassword(username, newPsw);
            case VOLONTARIO -> dbVolontarioHelper.changePassword(username, newPsw);
            default -> false;
        };
    }

    public void refreshPrecludedDate(Date d){
        dbDatesHelper.refreshPrecludedDate(d);
    }

    public void refreshVisiteTipoVisite(Date d){
        checkVisiteInTerminazione(d);
        checkTipoVisiteAttese(d);
    }

    private void checkTipoVisiteAttese(Date d){
        dbTipoVisiteHelper.checkTipoVisiteAttese(d);
    }

    private void checkVisiteInTerminazione(Date d){
        dbVisiteHelper.checkVisiteInTerminazione(d);
    }

    // ================================================================
    // Metodi di ricerca e login
    // ================================================================
    public Persona findUser(String username) {
        Persona out;

        out = dbConfiguratoreHelper.findPersona(username);
        if (out != null)
            return out;

        out = dbVolontarioHelper.findPersona(username);
        if (out != null)
            return out;

        out = dbFruitoreHelper.findPersona(username);
        if (out != null)
            return out;

        return null;
    }

    public Volontario findVolontario(String username) {
        return dbVolontarioHelper.findPersona(username);
    }

    public Persona login(String user, String psw) {
        Persona out;
        out = dbConfiguratoreHelper.login(user, psw);
        if (out != null)
            return out;

        out = dbVolontarioHelper.login(user, psw);
        if (out != null)
            return out;

        out = dbFruitoreHelper.login(user, psw);
        if (out != null)
            return out;

        return new Guest();
    }

    // ================================================================
    // Gestione delle date precluse
    // ================================================================
    public ArrayList<Date> getPrecludedDates() {
        return dbDatesHelper.getPrecludedDates();
    }

    public void addPrecludedDate(Date date) {
        dbDatesHelper.addPrecludedDate(date);
    }

    public void removePrecludedDate(Date date) {
        dbDatesHelper.removePrecludedDate(date);
    }

    // ================================================================
    // Recupero di TipoVisita e Luogo tramite nome
    // ================================================================
    public TipoVisita getTipoVisitaByName(String titoloVisita) {
        return dbTipoVisiteHelper.findTipoVisita(titoloVisita);
    }

    public Luogo getLuogoByName(String nomeLuogo) {
        return dbLuoghiHelper.findLuogo(nomeLuogo);
    }

    public TipoVisita getTipoVisita(String nome) {
        return dbTipoVisiteHelper.findTipoVisita(nome);
    }

    // ================================================================
    // Getter tramite UID
    // ================================================================
    public Luogo getLuoghiByUID(String uid) {
        return dbLuoghiHelper.getLuogoByUID(uid);
    }

    public TipoVisita getTipiByUID(String uid) {
        return dbTipoVisiteHelper.getTipiVisitaByUID(uid);
    }
}