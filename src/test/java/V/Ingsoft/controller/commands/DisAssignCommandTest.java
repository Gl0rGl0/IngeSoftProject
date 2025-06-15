package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class DisAssignCommandTest extends DefaultTest {
    private Payload<?> o;
    private static final String VOLUNTEER_USERNAME = "VOLTEST_DIS";
    private static final String PLACE_NAME = "Luogo Test Dis";
    private static final String VISIT_NAME = "Visita Dis";

    @BeforeEach
    void setupConfiguratoreAndEntities() {
        c.skipSetup();
        c.interpreter("login ADMIN PASSWORD");
        c.interpreter("time -m 1");

        // Create entities for disassignment tests
        c.interpreter("add -v " + VOLUNTEER_USERNAME + " password");
        c.interpreter("add -L \"" + PLACE_NAME + "\" \"Descrizione\" \"40.0:10.0\"");
        c.interpreter("add -T \"" + VISIT_NAME + "\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");

        // Perform initial assignments to then disassign
        c.interpreter("assign -V \"" + VISIT_NAME + "\" " + VOLUNTEER_USERNAME);
        c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME + "\"");
    }

    @Test
    void invalidPrompt() {
        o = c.interpreter("disassign");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing option for AssignCommand", o.getLogMessage()); // Note: Message refers to AssignCommand, potentially copy-paste error

        o = c.interpreter("disassign -x");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Unknown option in DisAssignCommand: x", o.getLogMessage());
    }

    @Test
    void disassignVolontarioSuccess() {
        o = c.interpreter("disassign -V \"" + VISIT_NAME + "\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Disassigned in DisAssignCommand: volunteer " + VOLUNTEER_USERNAME + ", visit " + VISIT_NAME, o.getLogMessage());

        // Verify disassignment in Model
        Volontario vol = Model.getInstance().dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(VISIT_NAME);
        assertNotNull(vol);
        assertNotNull(tv);
        assertFalse(vol.getTipivisiteAssignedUIDs().contains(tv.getUID()));
        assertFalse(tv.getVolontariUIDs().contains(VOLUNTEER_USERNAME));
    }

    @Test
    void disassignVolontarioFailInsufficientArgs() {
        o = c.interpreter("disassign -V \"" + VISIT_NAME + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for disassign -V", o.getLogMessage());
    }

    @Test
    void disassignVolontarioFailVolunteerNotFound() {
        o = c.interpreter("disassign -V \"" + VISIT_NAME + "\" NON_EXISTENT_VOL");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Volunteer not found: NON_EXISTENT_VOL", o.getLogMessage());
    }

    @Test
    void disassignVolontarioFailVisitNotFound() {
        o = c.interpreter("disassign -V \"NON_EXISTENT_VISIT\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("TipoVisita not found: NON_EXISTENT_VISIT", o.getLogMessage());
    }

    @Test
    void disassignVolontarioFailNotAssigned() {
        // Create a new volunteer and visit that are not assigned to each other
        c.interpreter("add -v VOL_NOT_ASSIGNED pass");
        c.interpreter("add -T \"VISIT_NOT_ASSIGNED\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");

        o = c.interpreter("disassign -V \"VISIT_NOT_ASSIGNED\" VOL_NOT_ASSIGNED");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Failed addTipoVisita for volunteer: VOL_NOT_ASSIGNED", o.getLogMessage()); // Message for add, but likely means remove failed
    }

    @Test
    void disassignVolontarioFailVolunteerDisabled() {
        // Create and disable a volunteer, then try to disassign
        c.interpreter("add -v DISABLED_VOL_DIS password");
        Volontario disabledVol = Model.getInstance().dbVolontarioHelper.getPersona("DISABLED_VOL_DIS");
        assertNotNull(disabledVol);
        disabledVol.setStatus(StatusItem.DISABLED);
        Model.getInstance().dbVolontarioHelper.saveDB();

        // Assign to a visit first (if logic allows disabled volunteers to be assigned)
        // Or directly try to disassign, assuming it's an error if disabled
        o = c.interpreter("disassign -V \"" + VISIT_NAME + "\" DISABLED_VOL_DIS");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Volunteer disabled: DISABLED_VOL_DIS", o.getLogMessage());
    }

    @Test
    void disassignVolontarioFailVisitDisabled() {
        // Create and disable a visit, then try to disassign
        c.interpreter("add -T \"DISABLED_VISIT_DIS\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");
        TipoVisita disabledVisit = Model.getInstance().dbTipoVisiteHelper.findTipoVisita("DISABLED_VISIT_DIS");
        assertNotNull(disabledVisit);
        disabledVisit.setStatus(StatusItem.DISABLED);
        Model.getInstance().dbTipoVisiteHelper.saveDB();

        o = c.interpreter("disassign -V \"DISABLED_VISIT_DIS\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Visit type disabled: DISABLED_VISIT_DIS", o.getLogMessage());
    }


    @Test
    void disassignLuogoSuccess() {
        o = c.interpreter("disassign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME + "\"");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Disassigned in DisAssignCommand: visit " + VISIT_NAME + ", place " + PLACE_NAME, o.getLogMessage());

        // Verify disassignment in Model
        Luogo luogo = Model.getInstance().getLuogoByName(PLACE_NAME);
        TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(VISIT_NAME);
        assertNotNull(luogo);
        assertNotNull(tv);
        // !!! ATTENZIONE: Bug potenziale nel tuo codice DisAssignCommand - l.addTipoVisita(t.getUID())
        // Se il tuo codice fosse corretto (l.removeTipoVisita), queste asserzioni funzionerebbero.
        // Con il codice attuale (addTipoVisita), se la visita era già assegnata, il metodo addTipoVisita
        // potrebbe fallire o tentare di aggiungere un duplicato.
        assertFalse(luogo.getTipoVisitaUID().contains(tv.getUID())); // Should be removed
        assertEquals("null", tv.getLuogoUID()); // Should be set to "null"
        assertEquals(StatusItem.DISABLED, tv.getStatus()); // Visit should be disabled
    }

    @Test
    void disassignLuogoFailInsufficientArgs() {
        o = c.interpreter("disassign -L \"" + PLACE_NAME + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for disassign -L", o.getLogMessage());
    }

    @Test
    void disassignLuogoFailLuogoNotFound() {
        o = c.interpreter("disassign -L \"NON_EXISTENT_PLACE\" \"" + VISIT_NAME + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Luogo not found: NON_EXISTENT_PLACE", o.getLogMessage());
    }

    @Test
    void disassignLuogoFailVisitNotFound() {
        o = c.interpreter("disassign -L \"" + PLACE_NAME + "\" \"NON_EXISTENT_VISIT\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("TipoVisita not found: NON_EXISTENT_VISIT", o.getLogMessage());
    }

    @Test
    void disassignLuogoFailNotAssigned() {
        // Create a new place and visit that are not assigned to each other
        c.interpreter("add -L \"PLACE_NOT_ASSIGNED\" \"Desc\" \"0:0\"");
        c.interpreter("add -T \"VISIT_NOT_ASSIGNED_PLACE\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");

        o = c.interpreter("disassign -L \"PLACE_NOT_ASSIGNED\" \"VISIT_NOT_ASSIGNED_PLACE\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Failed addTipoVisita for place: PLACE_NOT_ASSIGNED", o.getLogMessage()); // Message refers to add, which is the bug
    }

    @Test
    void disassignLuogoFailLuogoDisabled() {
        // Create and disable a place, then try to disassign
        c.interpreter("add -L \"DISABLED_PLACE_DIS\" \"Desc\" \"0:0\"");
        Luogo disabledPlace = Model.getInstance().getLuogoByName("DISABLED_PLACE_DIS");
        assertNotNull(disabledPlace);
        disabledPlace.setStatus(StatusItem.DISABLED);
        Model.getInstance().dbLuoghiHelper.saveDB();

        o = c.interpreter("disassign -L \"DISABLED_PLACE_DIS\" \"" + VISIT_NAME + "\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Luogo disabled: DISABLED_PLACE_DIS", o.getLogMessage());
    }

    @Test
    void disassignLuogoFailVisitDisabled() {
        // Create and disable a visit, then try to disassign
        c.interpreter("add -T \"DISABLED_VISIT_DIS_PLACE\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");
        TipoVisita disabledVisit = Model.getInstance().dbTipoVisiteHelper.findTipoVisita("DISABLED_VISIT_DIS_PLACE");
        assertNotNull(disabledVisit);
        disabledVisit.setStatus(StatusItem.DISABLED);
        Model.getInstance().dbTipoVisiteHelper.saveDB();

        o = c.interpreter("disassign -L \"" + PLACE_NAME + "\" \"DISABLED_VISIT_DIS_PLACE\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Visit type disabled: DISABLED_VISIT_DIS_PLACE", o.getLogMessage());
    }
}