package V4.Ingsoft.model;

import java.util.ArrayList;

import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.*;
import V4.Ingsoft.util.AppSettings;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.util.JsonStorage;
import V4.Ingsoft.util.Payload;
import V4.Ingsoft.util.Payload.Status;

public class Model {
    public final DBConfiguratoreHelper dbConfiguratoreHelper;
    public final DBFruitoreHelper dbFruitoreHelper;
    public final DBVolontarioHelper dbVolontarioHelper;
    public final DBTipoVisiteHelper dbTipoVisiteHelper;
    public final DBVisiteHelper dbVisiteHelper;
    public final DBLuoghiHelper dbLuoghiHelper;
    public final DBDatesHelper dbDatesHelper;
    public final DBIscrizioniHelper dbIscrizionisHelper;

    public static AppSettings appSettings;

    public static volatile Model instance = null;

    public static Model getInstance() {
        if (instance == null) {
            synchronized(Model.class) {
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

    public static Model getInstance(String ambito){
        setAmbito(ambito);
        return getInstance();
    }

    public static void setAmbito(String ambito) {
        getInstance();
        appSettings.setAmbitoTerritoriale(ambito);
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

    // Removed setAmbito, getAmbito, getMaxPrenotazioni, setMaxPrenotazioni methods.
    // These are now handled via AppSettings in the Controller.

    // ================================================================
    // Remove methods with Cascading Logic
    // ================================================================
    public boolean removeFruitore(String username) {
        Fruitore fruitore = dbFruitoreHelper.getPersona(username);
        if (fruitore == null) {
            return false; // Fruitore not found
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
        Volontario volontario = dbVolontarioHelper.getPersona(username);
        if (volontario == null) {
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
        for (Visita visita : visiteToRemove) {
            // Removing a Visita also requires removing its Iscrizioni
            for (Iscrizione iscrizione : visita.getIscrizioni()) {
                dbIscrizionisHelper.removeIscrizione(iscrizione.getUIDIscrizione());
            }
            dbVisiteHelper.removeVisitaByUID(visita.getUID()); // Assumes DBVisiteHelper has this method
        }


        // Finally, remove the Volontario
        return dbVolontarioHelper.removePersona(username);
    }

    // Keep original remove by name, but delegate to UID version
    public boolean removeTipoVisita(String typeName) {
        TipoVisita tipoVisita = dbTipoVisiteHelper.findTipoVisita(typeName);
        if (tipoVisita == null) {
            return false;
        }
        return removeTipoVisitaByUID(tipoVisita.getUID());
    }

    // New/Refactored method for removal by UID with cascade
    public boolean removeTipoVisitaByUID(String tipoVisitaUID) {
        TipoVisita tipoVisita = dbTipoVisiteHelper.getTipiVisitaByUID(tipoVisitaUID);
        if (tipoVisita == null) {
            return false; // Already removed or never existed
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
         for (Visita visita : visiteToRemove) {
            // Removing a Visita also requires removing its Iscrizioni
            for (Iscrizione iscrizione : visita.getIscrizioni()) {
                dbIscrizionisHelper.removeIscrizione(iscrizione.getUIDIscrizione());
            }
            dbVisiteHelper.removeVisitaByUID(visita.getUID()); // Assumes DBVisiteHelper has this method
        }

        // Finally, remove the TipoVisita itself
        // Ensure the helper method exists and works by UID
        return dbTipoVisiteHelper.removeTipoVisitaByUID(tipoVisitaUID); // Assumes this method exists
    }


    public void removeVisita(String type, String date) {
        Visita visita = dbVisiteHelper.findVisita(type, date);
        if (visita != null) {
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
        Luogo luogo = dbLuoghiHelper.getLuogoByUID(luogoUID);
        if (luogo == null) {
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

    /**
     * Performs daily updates on the status of visits and visit types based on the current date.
     * Checks for visits nearing confirmation/cancellation deadlines and activates pending visit types.
     * @param d The current simulated date.
     */
    public void updateDailyStatuses(Date d) {
        // Check visits nearing confirmation/cancellation deadlines or completion
        dbVisiteHelper.checkVisiteInTerminazione(d);
        // Check pending visit types to see if they should become active
        dbTipoVisiteHelper.checkTipoVisiteAttese(d);
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

        appSettings.clear();
    }

    public boolean isInitialized() {
        return !this.dbConfiguratoreHelper.isNew() && appSettings.isAmbitoSet();
    }
}
