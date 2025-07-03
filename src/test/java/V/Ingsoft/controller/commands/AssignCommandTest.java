package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class AssignCommandTest extends DefaultTest {
    private Payload<?> o;
    private static final String VOLUNTEER_USERNAME = "VOLTEST";
    private static final String PLACE_NAME = "Parco Test";
    private static final String VISIT_NAME_1 = "Tour Mattina";
    private static final String VISIT_NAME_2 = "Tour Pomeriggio";

    @BeforeEach
    void setupConfiguratoreAndEntities() {
        c.skipSetup();
        c.interpreter("login ADMIN PASSWORD");
        c.interpreter("time -m 1");

        c.interpreter("add -v " + VOLUNTEER_USERNAME + " password");
        c.interpreter("add -L \"" + PLACE_NAME + "\" \"Descrizione\" \"40.0:10.0\"");
        c.interpreter("add -T \"" + VISIT_NAME_1 + "\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");
        c.interpreter("add -T \"" + VISIT_NAME_2 + "\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:30 60 true 5 20 Lu");
    }

    @Test
    void invalidPrompt() {
        o = c.interpreter("assign");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing option for AssignCommand", o.getLogMessage());

        o = c.interpreter("assign -x");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Unknown option in AssignCommand: x", o.getLogMessage());
    }

    @Test
    void assignVolontarioSuccess() {
        o = c.interpreter("assign -V \"" + VISIT_NAME_1 + "\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Assigned in AssignCommand: volunteer " + VOLUNTEER_USERNAME + ", visit " + VISIT_NAME_1, o.getLogMessage());

        // Verify assignment in Model
        Volontario vol = Model.getInstance().dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(VISIT_NAME_1);
        assertNotNull(vol);
        assertNotNull(tv);
        assertTrue(vol.getTipivisiteAssignedUIDs().contains(tv.getUID()));
        assertTrue(tv.getVolontariUIDs().contains(VOLUNTEER_USERNAME));
    }

    @Test
    void assignVolontarioFailInsufficientArgs() {
        o = c.interpreter("assign -V \"" + VISIT_NAME_1 + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for assign -V", o.getLogMessage());
    }

    @Test
    void assignVolontarioFailVolunteerNotFound() {
        o = c.interpreter("assign -V \"" + VISIT_NAME_1 + "\" NON_EXISTENT_VOL");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Volunteer not found: NON_EXISTENT_VOL", o.getLogMessage());
    }

    @Test
    void assignVolontarioFailVisitNotFound() {
        o = c.interpreter("assign -V \"NON_EXISTENT_VISIT\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("TipoVisita not found: NON_EXISTENT_VISIT", o.getLogMessage());
    }

    @Test
    void assignVolontarioFailAlreadyAssigned() {
        c.interpreter("assign -V \"" + VISIT_NAME_1 + "\" " + VOLUNTEER_USERNAME); // First assignment
        o = c.interpreter("assign -V \"" + VISIT_NAME_1 + "\" " + VOLUNTEER_USERNAME); // Second attempt
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Failed addTipoVisita for volunteer: " + VOLUNTEER_USERNAME, o.getLogMessage()); // Assuming addTipoVisita fails if already present
    }

    @Test
    void assignVolontarioFailVolunteerDisabled() {
        // Create a volunteer and disable it
        c.interpreter("add -v DISABLED_VOL password");
        Volontario disabledVol = Model.getInstance().dbVolontarioHelper.getPersona("DISABLED_VOL");
        assertNotNull(disabledVol);
        disabledVol.setStatus(StatusItem.DISABLED); // Manually set status for testing
        Model.getInstance().dbVolontarioHelper.saveDB();

        o = c.interpreter("assign -V \"" + VISIT_NAME_1 + "\" DISABLED_VOL");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Volunteer disabled: DISABLED_VOL", o.getLogMessage());
    }

    @Test
    void assignVolontarioFailVisitDisabled() {
        // Create a visit and disable it
        c.interpreter("add -T \"DISABLED_VISIT\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");
        TipoVisita disabledVisit = Model.getInstance().dbTipoVisiteHelper.findTipoVisita("DISABLED_VISIT");
        assertNotNull(disabledVisit);
        disabledVisit.setStatus(StatusItem.DISABLED); // Manually set status for testing
        Model.getInstance().dbTipoVisiteHelper.saveDB();

        o = c.interpreter("assign -V \"DISABLED_VISIT\" " + VOLUNTEER_USERNAME);
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Visit type disabled: DISABLED_VISIT", o.getLogMessage());
    }

    @Test
    void assignLuogoSuccess() {
        o = c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME_1 + "\"");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Assigned in AssignCommand: visit " + VISIT_NAME_1 + ", place " + PLACE_NAME, o.getLogMessage());

        // Verify assignment in Model
        Luogo luogo = Model.getInstance().getLuogoByName(PLACE_NAME);
        TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(VISIT_NAME_1);
        assertNotNull(luogo);
        assertNotNull(tv);
        assertTrue(luogo.getTipoVisitaUID().contains(tv.getUID()));
        assertEquals(luogo.getUID(), tv.getLuogoUID());
    }

    @Test
    void assignLuogoFailInsufficientArgs() {
        o = c.interpreter("assign -L \"" + PLACE_NAME + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for assign -L", o.getLogMessage());
    }

    @Test
    void assignLuogoFailLuogoNotFound() {
        o = c.interpreter("assign -L \"NON_EXISTENT_PLACE\" \"" + VISIT_NAME_1 + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Luogo not found: NON_EXISTENT_PLACE", o.getLogMessage());
    }

    @Test
    void assignLuogoFailVisitNotFound() {
        o = c.interpreter("assign -L \"" + PLACE_NAME + "\" \"NON_EXISTENT_VISIT\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("TipoVisita not found: NON_EXISTENT_VISIT", o.getLogMessage());
    }

    @Test
    void assignLuogoFailAlreadyAssigned() {
        c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME_1 + "\""); // First assignment
        o = c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME_1 + "\""); // Second attempt
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Failed addTipoVisita for place: " + PLACE_NAME, o.getLogMessage()); // Assuming addTipoVisita fails if already present
    }

    @Test
    void assignLuogoFailLuogoDisabled() {
        // Create a place and disable it
        c.interpreter("add -L \"DISABLED_PLACE\" \"Desc\" \"0:0\"");
        Luogo disabledPlace = Model.getInstance().getLuogoByName("DISABLED_PLACE");
        assertNotNull(disabledPlace);
        disabledPlace.setStatus(StatusItem.DISABLED); // Manually set status for testing
        Model.getInstance().dbLuoghiHelper.saveDB();

        o = c.interpreter("assign -L \"DISABLED_PLACE\" \"" + VISIT_NAME_1 + "\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Luogo disabled: DISABLED_PLACE", o.getLogMessage());
    }

    @Test
    void assignLuogoFailVisitDisabled() {
        // Create a visit and disable it
        c.interpreter("add -T \"DISABLED_VISIT_FOR_PLACE\" \"Desc\" 0:0 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");
        TipoVisita disabledVisit = Model.getInstance().dbTipoVisiteHelper.findTipoVisita("DISABLED_VISIT_FOR_PLACE");
        assertNotNull(disabledVisit);
        disabledVisit.setStatus(StatusItem.DISABLED); // Manually set status for testing
        Model.getInstance().dbTipoVisiteHelper.saveDB();

        o = c.interpreter("assign -L \"" + PLACE_NAME + "\" \"DISABLED_VISIT_FOR_PLACE\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Visit type disabled: DISABLED_VISIT_FOR_PLACE", o.getLogMessage());
    }

    @Test
    void assignLuogoFailTimeOverlap() {
        // Assign first visit (9:00-10:00)
        c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME_1 + "\"");
        // Try to assign second visit (9:30-10:30) which overlaps
        o = c.interpreter("assign -L \"" + PLACE_NAME + "\" \"" + VISIT_NAME_2 + "\"");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Scheduling conflict for visit " + VISIT_NAME_2 + " at place " + PLACE_NAME, o.getLogMessage());
    }
}