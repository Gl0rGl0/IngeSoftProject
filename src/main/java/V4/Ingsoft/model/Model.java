package V4.Ingsoft.model;

import java.util.ArrayList;
import java.util.Iterator;

import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.*;
import V4.Ingsoft.util.Date;

public class Model {
    public final DBConfiguratoreHelper dbConfiguratoreHelper;
    public final DBFruitoreHelper dbFruitoreHelper;
    public final DBVolontarioHelper dbVolontarioHelper;
    public final DBTipoVisiteHelper dbTipoVisiteHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;
    public final DBIscrizioniHelper dbIscrizionisHelper;

    public String ambitoTerritoriale = null;

    private static volatile Model instance = null;

    public static Model getInstance() {
        if (instance == null) {
            synchronized(Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }
        return instance;
    }

    // Constructor and helper initialization
    private Model() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbTipoVisiteHelper = new DBTipoVisiteHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper();
        dbDatesHelper = new DBDatesHelper();
        dbIscrizionisHelper = new DBIscrizioniHelper();
    }

    public void setAmbito(String ambito) {
        this.ambitoTerritoriale = ambito;
    }

    public String getAmbito() {
        return this.ambitoTerritoriale;
    }

    // ================================================================
    // Remove methods
    // ================================================================
    public boolean removeFruitore(String username) {
        // dbIscrizionisHelper.removeAllIscrizioni
        Volontario v = dbVolontarioHelper.getPersona(username);
        //DA RIMUOVERE TUTTE LE ISCRIZIONI
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
        //DA RIMUOVERE TUTTE LE ISCRIZIONI
        dbVisiteHelper.removeVisita(type, date);
    }

    public boolean removeLuogo(String name) {
        Luogo toRemove = getLuogoByName(name);
        if (toRemove == null)
            return false;

        return dbLuoghiHelper.removeLuogo(name);
    }

    // ================================================================
    // Update methods (change password)
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
        // First remove any visit types from the DB
        refreshTipoVisite();

        // Then remove volunteers to avoid NOT removing a volunteer
        // belonging to a non-existent visit type
        refreshVolontari();

        dbTipoVisiteHelper.checkTipoVisiteAttese(d); // Check pending visit types
        dbVisiteHelper.checkVisiteInTerminazione(d);

        refreshIscrizioni();
    }

    // Check each volunteer
    // If at least one visit in their list of (types)Visits still exists => OK
    // If ALL visits do not exist (or list is empty) => delete the volunteer from the DB
    // For each visit not found, remove the uid from their visits
    private void refreshVolontari() {
        Iterator<Volontario> iterator = dbVolontarioHelper.getPersonList().iterator();

        while (iterator.hasNext()) {
            Volontario v = iterator.next();
            boolean toRemove = true;

            // Use an iterator to avoid ConcurrentModificationException
            Iterator<String> uidIterator = v.getTipiVisiteUIDs().iterator();

            while (uidIterator.hasNext()) {
                String tv = uidIterator.next();
                TipoVisita toCheck = dbTipoVisiteHelper.getTipiVisitaByUID(tv);

                if (toCheck == null) {
                    uidIterator.remove(); // Safe removal
                    continue;
                }

                toRemove &= !toCheck.assignedTo(v.getUsername());

                if (!toRemove)
                    break;
            }

            if (toRemove) {
                iterator.remove(); // Safe removal of the volunteer
            }
        }
    }

    // Check each TipoVisita
    // If the uid is 'null' or not present in the places => remove the visit type
    private void refreshTipoVisite() {
        for (TipoVisita v : dbTipoVisiteHelper.getTipiVisita()) {
            String uidLuogo = v.getLuogo();

            if (!dbLuoghiHelper.containsLuogoUID(uidLuogo))
                dbTipoVisiteHelper.removeTipoVisita(v.getTitle());
        }
    }

    /*
     * For each registration (iscrizione), it is initially assumed it should be removed (toRemove = true).
     * Iterate through all visits and their registrations to check if the current registration is referenced.
     * If a match is found, the toRemove flag becomes false, and the iteration is interrupted.
     * Finally, if toRemove remains true, the registration is deleted from the database.
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
    // Search and login methods
    // ================================================================
    public Persona findUser(String username) {
        Persona out;

        out = dbConfiguratoreHelper.getPersona(username);
        if (out != null)
            return out;

        out = dbVolontarioHelper.getPersona(username);
        if (out != null)
            return out;

        out = dbFruitoreHelper.getPersona(username);
        if (out != null)
            return out;

        return null;
    }

    public Volontario findVolontario(String username) {
        return dbVolontarioHelper.getPersona(username);
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
    // Management of precluded dates
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
    // Retrieval of TipoVisita and Luogo via name
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
    // Getters via UID
    // ================================================================

    public ArrayList<Visita> trovaVisiteByVolontario(Volontario v) {
        ArrayList<Visita> out = new ArrayList<>();

        for (String visitaUID : v.getTipiVisiteUIDs()) {
            out.add(dbVisiteHelper.getVisitaByUID(visitaUID));
        }

        return out;
    }

    public ArrayList<Visita> trovaVisiteByLuogo(Luogo l) {
        ArrayList<Visita> out = new ArrayList<>();

        for (String visitaUID : l.getTipoVisitaUID()) {
            out.add(dbVisiteHelper.getVisitaByUID(visitaUID));
        }

        return out;
    }

    public ArrayList<TipoVisita> trovaTipoVisiteByVolontario(Volontario v) {
        ArrayList<TipoVisita> out = new ArrayList<>();
        String uidV = v.getUsername();
        for (TipoVisita t : dbTipoVisiteHelper.getTipiVisita()) {
            if (t.assignedTo(uidV))
                out.add(t);
        }
        return out;
    }

    public void closeAll() {
        dbConfiguratoreHelper.close();
        dbFruitoreHelper.close();
        dbVolontarioHelper.close();
        dbLuoghiHelper.close();
        dbVisiteHelper.close();
        dbTipoVisiteHelper.close();
    }

    public void clearAll() {
        dbConfiguratoreHelper.clear();
        dbFruitoreHelper.clear();
        dbVolontarioHelper.clear();
        dbLuoghiHelper.clear();
        dbVisiteHelper.clear();
        dbTipoVisiteHelper.clear();
    }
}
