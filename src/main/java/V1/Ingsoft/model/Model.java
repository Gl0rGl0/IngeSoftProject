package V1.Ingsoft.model;

import V1.Ingsoft.controller.item.luoghi.Luogo;
import V1.Ingsoft.controller.item.luoghi.TipoVisita;
import V1.Ingsoft.controller.item.luoghi.Visita;
import V1.Ingsoft.controller.item.persone.*;
import V1.Ingsoft.util.*;
import V1.Ingsoft.util.Payload.Status;

import java.util.ArrayList;

public class Model {
    public static AppSettings appSettings;
    public static volatile Model instance = null;
    public final DBConfiguratoreHelper dbConfiguratoreHelper;
    public final DBFruitoreHelper dbFruitoreHelper;
    public final DBVolontarioHelper dbVolontarioHelper;
    public final DBTipoVisiteHelper dbTipoVisiteHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;
    public final DBIscrizioniHelper dbIscrizionisHelper;

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

    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }

        appSettings = JsonStorage.loadObject(AppSettings.PATH, AppSettings.class);
        if (appSettings == null) {
            AssertionControl.logMessage("Settings file not found or invalid, creating default settings.", 1, "Model");
            appSettings = new AppSettings();
            // Attempt to save the new default settings immediately
            JsonStorage.saveObject(AppSettings.PATH, appSettings);
        }

        return instance;
    }

    public static void deleteInstance() {
        instance = null;
        appSettings = null;
    }

    public static void setAmbito(String ambito) {
        getInstance();
        appSettings.setAmbitoTerritoriale(ambito);
    }

    // ================================================================
    // Update methods (change password)
    // ================================================================
    public boolean changePassword(String username, String newPsw) {
        Persona p = findPersona(username);

        return switch (p.getType()) {
            case CONFIGURATORE -> dbConfiguratoreHelper.changePassword(username, newPsw);
            case FRUITORE -> dbFruitoreHelper.changePassword(username, newPsw);
            case VOLONTARIO -> dbVolontarioHelper.changePassword(username, newPsw);
            default -> false; //GUEST
        };
    }

    public Persona findPersona(String user) {
        if (user == null)
            return Guest.getInstance();

        Persona out;

        out = dbConfiguratoreHelper.getPersona(user);
        if (out != null)
            return out;

        out = dbVolontarioHelper.getPersona(user);
        if (out != null)
            return out;

        out = dbFruitoreHelper.getPersona(user);
        if (out != null)
            return out;

        return Guest.getInstance();
    }

    /**
     * Performs daily updates on the status of visits and visit types based on the current date.
     * Checks for visits nearing confirmation/cancellation deadlines and activates pending visit types.
     *
     * @param d The current simulated date.
     */
    public void updateDailyStatuses(Date d) {
        // Check visits nearing confirmation/cancellation deadlines or completion
        dbVisiteHelper.checkVisite(d);
        // Check pending visit types to see if they should become active
        dbTipoVisiteHelper.checkTipoVisite(d);
        // Check statuses of Volunteer
        dbVolontarioHelper.checkVolunteers(d);
        //Check luoghi
        dbLuoghiHelper.checkLuoghi(d);
    }

    // ================================================================
    // Search and login methods
    // ================================================================

    public Payload login(String user, String psw) {
        Payload out;

        out = dbConfiguratoreHelper.login(user, psw);
        if (out.getData() != null)
            return out;

        out = dbVolontarioHelper.login(user, psw);
        if (out.getData() != null)
            return out;

        out = dbFruitoreHelper.login(user, psw);
        if (out.getData() != null)
            return out;

        out.setStatus(Status.ERROR);
        out.setData(Guest.getInstance());

        return out;
    }

    // ================================================================
    // Retrieval of TipoVisita and Luogo via name
    // ================================================================

    public Luogo getLuogoByName(String name) {
        return dbLuoghiHelper.findLuogo(name);
    }

    // ================================================================
    // Getters via UID
    // ================================================================

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

        appSettings.clear();
    }

    public boolean isInitialized() {
        return !this.dbConfiguratoreHelper.isNew() && appSettings.isAmbitoSet();
    }
}
