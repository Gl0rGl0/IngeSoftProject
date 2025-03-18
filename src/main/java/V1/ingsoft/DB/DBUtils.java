package V1.ingsoft.DB;

import V1.ingsoft.luoghi.Luogo;
import V1.ingsoft.luoghi.TipoVisita;
import V1.ingsoft.luoghi.Visita;
import V1.ingsoft.persone.*;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.GPS;

import java.util.ArrayList;

public class DBUtils {
    private final DBConfiguratoreHelper dbConfiguratoreHelper;
    private final DBFruitoreHelper dbFruitoreHelper;
    public final DBVolontarioHelper dbVolontarioHelper;
    public final DBTipoVisiteHelper dbTipoVisiteHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;
    public final DBIscrizioniHelper dbIscrizionisHelper;

    private boolean isNew;
    public String ambitoTerritoriale;

    // Costruttore e inizializzazione degli helper
    public DBUtils() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbTipoVisiteHelper = new DBTipoVisiteHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper();
        dbDatesHelper = new DBDatesHelper();
        dbIscrizionisHelper = new DBIscrizioniHelper();

        this.isNew = dbConfiguratoreHelper.isNew() && dbLuoghiHelper.isNew();
    }

    public boolean isNew() {
        return this.isNew;
    }

    public void setAmbito(String ambito) {
        this.ambitoTerritoriale = ambito;
    }

    public String getAmbito() {
        return this.ambitoTerritoriale;
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

    // GETTER VARIABILI
    public ArrayList<TipoVisita> getTipoVisiteIstanziabili() {
        return dbTipoVisiteHelper.getTipoVisiteIstanziabili();
    }

    // ================================================================
    // Adders per oggetti già creati
    // ================================================================
    public boolean addConfiguratore(Configuratore c) {
        if (findUser(c.getUsername()) != null)
            return false;
        return dbConfiguratoreHelper.addPersona(c);
    }

    public boolean addFruitore(Fruitore f) {
        if (findUser(f.getUsername()) != null)
            return false;
        return dbFruitoreHelper.addPersona(f);
    }

    public boolean addVolontario(Volontario v) {
        if (findUser(v.getUsername()) != null)
            return false;
        return dbVolontarioHelper.addPersona(v);
    }

    public boolean addLuogo(String name, String description, GPS gps) {
        return dbLuoghiHelper.addLuogo(name, description, gps);
    }

    public boolean addTipoVisita(TipoVisita tv) {
        return dbTipoVisiteHelper.addTipoVisita(tv);
    }

    public void addVisita(Visita visita) {
        dbVisiteHelper.addVisita(visita);
    }

    // ================================================================
    // Adders per oggetti da creare tramite username/psw - String
    // ================================================================
    public boolean addConfiguratore(String user, String psw) {
        if (findUser(user) != null)
            return false;
        return dbConfiguratoreHelper.addPersona(new Configuratore(user, psw, true, true));
    }

    public boolean addFruitore(String user, String psw) {
        if (findUser(user) != null)
            return false;
        return dbFruitoreHelper.addPersona(new Fruitore(user, psw, true, true));
    }

    public boolean addVolontario(String user, String psw) {
        if (findUser(user) != null)
            return false;
        return dbVolontarioHelper.addPersona(new Volontario(user, psw, true, true));
    }

    public boolean addTipoVisita(String[] args, Date d) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return dbTipoVisiteHelper.addTipoVisita(new TipoVisita(args, d));
    }

    // ================================================================
    // Remove methods
    // ================================================================
    public boolean removeConfiguratore(String username) {
        return dbConfiguratoreHelper.removePersona(username);
    }

    public boolean removeFruitore(String username) {
        // eliminare tutte le iscrizioni a lui collegate
        return dbFruitoreHelper.removePersona(username);
    }

    public boolean removeVolontario(String username) {
        Volontario toRemove = findVolontario(username);
        if (toRemove == null)
            return false;

        for (TipoVisita tv : dbTipoVisiteHelper.getTipiVisita()) {
            tv.removeVolontario(username);
        }

        return dbVolontarioHelper.removePersona(username);
    }

    public boolean removeTipoVisita(String type) {
        TipoVisita toRemove = getTipoVisitaByName(type);
        if (toRemove == null)
            return false;

        for (Luogo l : dbLuoghiHelper.getLuoghi()) {
            l.removeTipoVisita(type);
        }

        return dbTipoVisiteHelper.removeTipoVisita(type);
    }

    public void removeVisita(String type, String date) {
        dbVisiteHelper.removeVisita(type, date);
    }

    public boolean removeLuogo(String name) {
        Luogo toRemove = getLuogoByName(name);
        if (toRemove == null)
            return false;

        return dbLuoghiHelper.removeLuogo(name);
    }

    // ================================================================
    // Metodi di aggiornamento (change password)
    // ================================================================
    public boolean changePassword(String username, String newPsw, PersonaType type) {
        return switch (type) {
            case CONFIGURATORE -> dbConfiguratoreHelper.changePassword(username, newPsw);
            case FRUITORE -> dbFruitoreHelper.changePassword(username, newPsw);
            case VOLONTARIO -> dbVolontarioHelper.changePassword(username, newPsw);
            default -> false;
        };
    }

    public void refreshPrecludedDate(Date d) {
        dbDatesHelper.refreshPrecludedDate(d);
    }

    public void refresher(Date d) {
        // Prima tolgo le eventuali tipologie di visite dal DB
        refreshTipoVisite();

        // Poi rimuovo i volontari per evitare di NON rimuovere un volontario
        // appartenente a un tipovisita non esistente
        refreshVolontari();

        dbTipoVisiteHelper.checkTipoVisiteAttese(d);
        dbVisiteHelper.checkVisiteInTerminazione(d);

        refreshIscrizioni();
    }

    // Controlla ogni volontario
    // Se almeno una visita dentro la sua lista di (tipi)Visite esiste ancora => OK
    // Se TUTTE le visite non esistono (o è vuoto) => si elimina il volontario dal
    // DB
    // Per ogni visita non trovata si toglie l'uid dalle sue visite
    private void refreshVolontari() {
        for (Volontario v : dbVolontarioHelper.getPersonList()) {
            boolean toRemove = true;
            for (String tv : v.getTipiVisiteUIDs()) {
                TipoVisita toCheck = dbTipoVisiteHelper.getTipiVisitaByUID(tv);
                if (toCheck == null) {
                    v.removeUIDVisita(tv);
                    continue;
                }

                toRemove &= !toCheck.assignedTo(v.getUsername());

                if (!toRemove)
                    break;
            }

            if (toRemove)
                dbVolontarioHelper.removePersona(v.getUsername());
        }
    }

    // Controlla ogni TipoVisita
    // Se l'uid è 'null' o non è presente nei luoghi => rimuovo il tipo visita
    private void refreshTipoVisite() {
        for (TipoVisita v : dbTipoVisiteHelper.getTipiVisita()) {
            String uidLuogo = v.getLuogo();

            if (!dbLuoghiHelper.containsLuogoUID(uidLuogo))
                dbTipoVisiteHelper.removeTipoVisita(v.getTitle());
        }
    }

    /*
     * Per ogni iscrizione si assume inizialmente che debba essere rimossa (toRemove
     * = true).
     * Si itera su tutte le visite e sulle loro iscrizioni per verificare se
     * l'iscrizione corrente è referenziata.
     * Se viene trovata una corrispondenza, il flag toRemove diventa false e
     * l'iterazione viene interrotta.
     * Alla fine, se toRemove rimane true, l'iscrizione viene eliminata dal
     * database.
     */
    private void refreshIscrizioni() {
        for (Iscrizione i : dbIscrizionisHelper.getIscrizioni()) {
            boolean toRemove = true;
            for (Visita v : dbVisiteHelper.getVisite()) {
                for (Iscrizione ii : v.getIscrizioni()) {
                    toRemove &= !i.getUIDIscrizione().equals(ii.getUIDIscrizione());

                    if (!toRemove)
                        break;
                }

                if (!toRemove)
                    break;
            }

            if (toRemove)
                dbIscrizionisHelper.removeIscrizione(i.getUIDIscrizione());
        }
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
    // Recupero di TipoVisita e Luogo tramite name
    // ================================================================
    public TipoVisita getTipoVisitaByName(String title) {
        return dbTipoVisiteHelper.findTipoVisita(title);
    }

    public Luogo getLuogoByName(String name) {
        return dbLuoghiHelper.findLuogo(name);
    }

    public Visita getVisitaByName(String string, String date) {
        return dbVisiteHelper.findVisita(string, date);
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

    public Visita getVisitaByUID(String uid) {
        return dbVisiteHelper.getVisitaByUID(uid);
    }

    public ArrayList<Visita> trovaVisiteByVolontario(Volontario v) {
        ArrayList<Visita> out = new ArrayList<>();

        for (String visitaUID : v.getTipiVisiteUIDs()) {
            out.add(getVisitaByUID(visitaUID));
        }

        return out;
    }

    public ArrayList<Visita> trovaVisiteByLuogo(Luogo l) {
        ArrayList<Visita> out = new ArrayList<>();

        for (String visitaUID : l.getTipoVisitaUID()) {
            out.add(getVisitaByUID(visitaUID));
        }

        return out;
    }
}