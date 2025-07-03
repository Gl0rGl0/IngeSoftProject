package V.Ingsoft.controller.commands.running;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class AvailabilityCommandTest extends DefaultTest {
    Payload<?> o;
    Model m;
    static final String VOLUNTEER_USERNAME = "voltestAvail";
    static final String ADMIN_USERNAME = "ADMIN";
    static final String FRUITORE_USERNAME = "fruitoreTestAvail";

    @BeforeEach
    void setupTest() throws Exception {
        c.skipSetup(); // Reset Model and Controller
        m = Model.getInstance();
        c.interpreter("time -s 17/01/2025"); // Set a base date

        // Create a volunteer to use in tests
        m.dbVolontarioHelper.addItem(new Volontario(VOLUNTEER_USERNAME, "password", false, true));
        // Create a fruitore to test non-volunteer access
        m.dbFruitoreHelper.addFruitore(new Fruitore(FRUITORE_USERNAME, "password", false, true));

        // Ensure volunteer collection is open by default for most tests
        c.interpreter("login ADMIN PASSWORD");
        o = c.interpreter("collection -o"); // Assuming this method exists in your test setup/controller
        c.interpreter("logout");
    }

    // --- Access Control Tests ---

    @Test
    void testAvailabilityFailNonVolunteer() {
        // Log in as Admin (Configuratore)
        c.interpreter("login ADMIN PASSWORD");
        o = c.interpreter("setav -a 01/02/2025");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Interpreter: permission denied for setav", o.getLogMessage());

        // Log in as Fruitore
        c.interpreter("login " + FRUITORE_USERNAME + " password");
        o = c.interpreter("setav -a 01/02/2025");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Interpreter: permission denied for setav", o.getLogMessage());
    }

    // --- Command Syntax & Parsing Tests ---

    @Test
    void testAvailabilityFailMissingOptionsOrArgs() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        o = c.interpreter("setav"); // No options, no args
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing options or arguments", o.getLogMessage());

        o = c.interpreter("setav -a"); // Option, but no args
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing options or arguments", o.getLogMessage());

        o = c.interpreter("setav 01/02/2025"); // Args, but no options
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing options or arguments", o.getLogMessage());
    }

    @Test
    void testAvailabilityFailUnknownOption() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        o = c.interpreter("setav -x 01/02/2025");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Unknown option 'x'", o.getLogMessage());
    }

    @Test
    void testAvailabilityFailInvalidDateFormat() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        o = c.interpreter("setav -a invalid-date");
        assertEquals(Status.INFO, o.getStatus()); // Note: Returns INFO because it processes valid dates and reports invalid ones in feedback
        assertEquals("Invalid date format: invalid-date", o.getData());

        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        // Ensure no availability was added for the invalid date
        assertFalse(vol.isAvailable(1)); // This check might need more specific date handling
    }

    // --- Manage Date (Add) Tests ---

    @Test
    void testAvailabilityAddMultipleDatesSuccess() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        //FEBBRAIO NON AGGIUNGERÀ NULLA
        o = c.interpreter("setav -a 01/02/2025 02/03/2025 05/03/2025");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Managed availability for 3 date(s)", o.getLogMessage());

        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertFalse(vol.isAvailable(1));
        assertTrue(vol.isAvailable(2));
        assertTrue(vol.isAvailable(5));
    }

    @Test
    void testAvailabilityAddFailCollectionClosed() {
        c.interpreter("login ADMIN PASSWORD");
        o = c.interpreter("collection -c");
        c.interpreter("logout");

        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        o = c.interpreter("setav -a 01/03/2025");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Collection closed.", o.getLogMessage());

        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertFalse(vol.isAvailable(1)); // Ensure not added
    }

    @Test
    void testAvailabilityAddFailPrecludedDate() throws Exception {
        // Preclude a date first
        c.interpreter("login ADMIN PASSWORD");
        o = c.interpreter("preclude -a 01/03/2025");
        assertNotNull(m.dbDatesHelper.getItems().getFirst());
        c.interpreter("logout");
        
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");
        // Try to add the precluded date
        o = c.interpreter("setav -a 01/03/2025 02/03/2025"); // Add one precluded, one valid
        assertEquals(Status.INFO, o.getStatus());
        // The message should indicate the precluded date was ignored and the valid one was added
        assertEquals("Managed availability for 2 date(s)", o.getLogMessage());

        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertFalse(vol.isAvailable(1)); // Precluded date not added
        assertTrue(vol.isAvailable(2));  // Valid date added
    }

    @Test //INUTILE
    void testAvailabilityAddNoChangesApplied() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");

        // Preclude the only date provided
        c.interpreter("preclude -a 01/03/2025");

        o = c.interpreter("setav -a 01/03/2025");
        assertEquals(Status.INFO, o.getStatus());
        // Message if only precluded dates or invalid formats are provided
        assertEquals("Managed availability for 1 date(s)", o.getLogMessage());

        // Now test if all are invalid dates
        o = c.interpreter("setav -a invalid1 invalid2");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Managed availability for 2 date(s)", o.getLogMessage());
    }

    // --- Manage Date (Remove) Tests ---

    @Test
    void testAvailabilityRemoveSingleDateSuccess() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");
        // First add some availability to remove
        c.interpreter("setav -a 01/03/2025");
        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertTrue(vol.isAvailable(1));

        o = c.interpreter("setav -r 01/03/2025");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Managed availability for 1 date(s)", o.getLogMessage());

        assertFalse(vol.isAvailable(1));
    }

    @Test
    void testAvailabilityRemoveMultipleDatesSuccess() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");
        c.interpreter("setav -a 01/03/2025 05/03/2025 10/03/2025");
        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertTrue(vol.isAvailable(1));
        assertTrue(vol.isAvailable(5));
        assertTrue(vol.isAvailable(10));

        o = c.interpreter("setav -r 01/03/2025 10/03/2025");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Managed availability for 2 date(s)", o.getLogMessage());

        assertFalse(vol.isAvailable(1));
        assertTrue(vol.isAvailable(5)); // This one should remain
        assertFalse(vol.isAvailable(10));
    }

    @Test
    void testAvailabilityRemoveFailCollectionClosed() {
        c.interpreter("login " + VOLUNTEER_USERNAME + " password");
        // Add availability first
        c.interpreter("setav -a 01/03/2025");
        Volontario vol = m.dbVolontarioHelper.getPersona(VOLUNTEER_USERNAME);
        assertNotNull(vol);
        assertTrue(vol.isAvailable(1));

        c.closeCollection();

        o = c.interpreter("setav -r 01/03/2025");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Collection closed.", o.getLogMessage());

        assertTrue(vol.isAvailable(1)); // Ensure not removed
    }
}