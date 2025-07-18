package V3.Ingsoft.model;

import V3.Ingsoft.controller.item.luoghi.Luogo;
import V3.Ingsoft.controller.item.luoghi.TipoVisita;
import V3.Ingsoft.controller.item.luoghi.Visita;
import V3.Ingsoft.controller.item.persone.*;
import V3.Ingsoft.util.*;
import V3.Ingsoft.util.Payload.Status;

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

    // Removed setAmbito, getAmbito, getMaxPrenotazioni, setMaxPrenotazioni methods.
    // These are now handled via AppSettings in the Controller.

    // ================================================================
    // Remove methods with Cascading Logic
    // ================================================================
    public boolean removeFruitore(String username) {
        if (username == null || username.trim().isEmpty()) {
            AssertionControl.logMessage("Fruitore username cannot be null or empty for removal", 2, getClass().getSimpleName());
            return false;
        }

        Fruitore fruitore = dbFruitoreHelper.getPersona(username);
        if (fruitore == null) {
            AssertionControl.logMessage("Attempted to remove non-existent Fruitore: " + username, 1, "Model");
            return false;
        }

        // --- Cascade: Remove associated Iscrizioni ---
        // Iterate through a copy of Iscrizione UIDs to avoid concurrent modification
        ArrayList<String> iscrizioniToRemove = new ArrayList<>();
        for (Iscrizione iscrizione : dbIscrizionisHelper.getIscrizioni()) {
            if (iscrizione.getUIDFruitore().equals(username)) { // Corrected method name again based on Iscrizione.java
                iscrizioniToRemove.add(iscrizione.getUIDIscrizione());
            }
        }

        // Remove Iscrizioni from helpers and Visite
        for (String uidIscrizione : iscrizioniToRemove) {
            // Remove from the central helper
            dbIscrizionisHelper.removeIscrizione(uidIscrizione);
            // Remove from any Visita that holds it
            for (Visita visita : dbVisiteHelper.getVisite()) {
                visita.removeIscrizioneByUID(uidIscrizione); // Assumes Visita has this method
            }
        }

        // Finally, remove the Fruitore
        return dbFruitoreHelper.removePersona(username);
    }

    public boolean removeVolontario(String username) {
        if (username == null || username.trim().isEmpty()) {
            AssertionControl.logMessage("Volontario username cannot be null or empty for removal", 2, getClass().getSimpleName());
            return false;
        }

        Volontario volontario = dbVolontarioHelper.getPersona(username);
        if (volontario == null) {
            AssertionControl.logMessage("Attempted to remove non-existent Volontario: " + username, 1, "Model");
            return false; // Volontario not found
        }

        // --- Cascade: Remove volunteer from associated TipoVisita ---
        // Iterate through a copy to avoid concurrent modification if removeTipoVisitaByUID is called
        ArrayList<String> tipiVisitaUIDsCopy = new ArrayList<>(volontario.getTipiVisiteUIDs());
        for (String tipoVisitaUID : tipiVisitaUIDsCopy) {
            TipoVisita tipoVisita = dbTipoVisiteHelper.getTipiVisitaByUID(tipoVisitaUID);
            if (tipoVisita != null) {
                tipoVisita.removeVolontario(username); // Assumes TipoVisita has this method
                // Check if TipoVisita is now unassigned
                if (tipoVisita.getVolontariUIDs().isEmpty()) {
                    // If unassigned, trigger its removal cascade
                    removeTipoVisitaByUID(tipoVisitaUID);
                }
            }
        }

        // --- Cascade: Remove Visite assigned to this volunteer ---
        ArrayList<Visita> visiteToRemove = new ArrayList<>();
        for (Visita visita : dbVisiteHelper.getVisite()) {
            // Check if the volunteer is assigned to this visit
            if (visita.getUidVolontario() != null && visita.getUidVolontario().equals(username)) { // Corrected method name
                visiteToRemove.add(visita);
            }
        }

        removeVisits(visiteToRemove);

        // Finally, remove the Volontario
        return dbVolontarioHelper.removePersona(username);
    }

    // New/Refactored method for removal by UID with cascade
    public void removeTipoVisitaByUID(String tipoVisitaUID) {
        if (tipoVisitaUID == null || tipoVisitaUID.trim().isEmpty()) {
            AssertionControl.logMessage("TipoVisita UID cannot be null or empty for removal", 2, getClass().getSimpleName());
            return;
        }

        TipoVisita tipoVisita = dbTipoVisiteHelper.getTipiVisitaByUID(tipoVisitaUID);
        if (tipoVisita == null) {
            // This might happen legitimately during cascade, so maybe just log?
            AssertionControl.logMessage("Attempted to remove non-existent or already removed TipoVisita UID: " + tipoVisitaUID, 0, "Model");
            return; // Already removed or never existed
        }

        // --- Cascade: Remove from associated Luogo ---
        String luogoUID = tipoVisita.getLuogo();
        if (luogoUID != null) {
            Luogo luogo = dbLuoghiHelper.getLuogoByUID(luogoUID);
            if (luogo != null) {
                luogo.removeTipoVisita(tipoVisitaUID); // Assumes Luogo has this method
                // Check if Luogo is now empty
                if (luogo.getTipoVisitaUID().isEmpty()) {
                    // If empty, trigger its removal cascade
                    removeLuogoByUID(luogoUID); // Use UID based removal
                }
            }
        }

        // --- Cascade: Remove from associated Volontari ---
        // Iterate through a copy to avoid concurrent modification if removeVolontario is called
        ArrayList<String> volontariUIDsCopy = new ArrayList<>(tipoVisita.getVolontariUIDs());
        for (String volontarioUID : volontariUIDsCopy) {
            Volontario volontario = dbVolontarioHelper.getPersona(volontarioUID);
            if (volontario != null) {
                volontario.removeUIDVisita(tipoVisitaUID); // Corrected method name based on Volontario.java
                // Check if Volontario is now unassigned
                if (volontario.getTipiVisiteUIDs().isEmpty()) {
                    // If unassigned, trigger its removal cascade
                    removeVolontario(volontarioUID);
                }
            }
        }

        // --- Cascade: Remove associated Visite ---
        ArrayList<Visita> visiteToRemove = new ArrayList<>();
        for (Visita visita : dbVisiteHelper.getVisite()) {
            if (visita.tipoVisitaUID().equals(tipoVisitaUID)) { // Corrected method name
                visiteToRemove.add(visita);
            }
        }

        removeVisits(visiteToRemove);

        // Finally, remove the TipoVisita itself
        // Ensure the helper method exists and works by UID
        dbTipoVisiteHelper.removeTipoVisitaByUID(tipoVisitaUID);
    }

    private void removeVisits(ArrayList<Visita> toRemove){
        for (Visita visita : toRemove) {
            // Removing a Visita also requires removing its Iscrizioni
            for (Iscrizione iscrizione : visita.getIscrizioni()) {
                dbIscrizionisHelper.removeIscrizione(iscrizione.getUIDIscrizione());
            }
            dbVisiteHelper.removeVisitaByUID(visita.getUID()); // Assumes DBVisiteHelper has this method
        }
    }

    // Keep original remove by name, but delegate to UID version
    public boolean removeLuogo(String name) {
        Luogo luogo = getLuogoByName(name);
        if (luogo == null) {
            return false;
        }
        return removeLuogoByUID(luogo.getUID());
    }

    // New method for removal by UID with cascade
    public boolean removeLuogoByUID(String luogoUID) {

        if (luogoUID == null || luogoUID.trim().isEmpty()) {
            AssertionControl.logMessage("Luogo UID cannot be null or empty for removal", 2, getClass().getSimpleName());
            return false;
        }

        Luogo luogo = dbLuoghiHelper.getLuogoByUID(luogoUID);
        if (luogo == null) {
            // This might happen legitimately during cascade
            AssertionControl.logMessage("Attempted to remove non-existent or already removed Luogo UID: " + luogoUID, 0, "Model");
            return false; // Already removed or never existed
        }

        // --- Cascade: Remove associated TipoVisite ---
        // Iterate through a copy to avoid concurrent modification issues
        ArrayList<String> tipiVisitaUIDsCopy = new ArrayList<>(luogo.getTipoVisitaUID());
        for (String tipoVisitaUID : tipiVisitaUIDsCopy) {
            // Trigger the cascade for each associated TipoVisita
            removeTipoVisitaByUID(tipoVisitaUID);
        }

        // Finally, remove the Luogo itself
        return dbLuoghiHelper.removeLuogoByUID(luogoUID); // Assumes DBLuoghiHelper has removeLuogoByUID
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
