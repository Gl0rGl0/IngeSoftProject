package V.Ingsoft.controller.commands.running;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.Fruitore; // Assuming Fruitore class exists and is managed by Model
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class RemoveCommandTest extends DefaultTest {
    Payload<?> o;
    Model m;

    // Identifiers for test entities
    static final String CONFIG_USERNAME = "configUser";
    static final String VOL_USERNAME = "volUser";
    static final String FRUITORE_USERNAME = "fruitoreUser";
    static final String PLACE_NAME = "Test Place";
    static final String VISIT_TITLE = "Test Visit Type";

    @BeforeEach
    void setupTest() {
        c.skipSetup();
        //c.interpreter("time -m 1");
        c.interpreter("login ADMIN PASSWORD"); // Login as admin to perform removals

        m = Model.getInstance();

        // Add entities to be removed/scheduled for removal in tests
        c.interpreter("add -c " + CONFIG_USERNAME + " password");
        c.interpreter("add -v " + VOL_USERNAME + " password");
        // Assuming there's an 'add -f' command for Fruitore or it's implicitly created
        // For testing, we might need to manually add a Fruitore if 'add -f' doesn't exist
        // or mock its existence. Let's assume removeFruitore works on a direct username.
        // If Model.removeFruitore actually refers to an existing `dbFruitoreHelper`,
        // ensure you add one.
        c.interpreter("add -L \"" + PLACE_NAME + "\" \"Desc\" \"10:10\"");
        c.interpreter("add -T \"" + VISIT_TITLE + "\" \"Desc\" 10:10 01/01/2025 01/01/2025 09:00 60 true 5 20 Lu");

        // Set controller date to a day where isExecutable() is typically true (e.g., before the 16th)
        c.interpreter("time -s 16/01/2025");
        // Or set to a fixed date that you know makes isExecutable() true/false based on implementation.
        // For simplicity, we assume day 15 makes it executable.
    }

    // --- Common Failure Cases ---

    @Test
    void testRemoveInvalidPromptNoOptions() {
        o = c.interpreter("remove");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Missing or invalid option in RemoveCommand", o.getLogMessage());
    }

    @Test
    void testRemoveInvalidPromptNoArgs() {
        o = c.interpreter("remove -c"); // Option provided, but no args
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for remove -c", o.getLogMessage());

        o = c.interpreter("remove -L");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for remove -L", o.getLogMessage());
    }

    @Test
    void testRemoveUnknownOption() {
        o = c.interpreter("remove -x someid");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Unrecognized option in RemoveCommand: x", o.getLogMessage());
    }

    // --- Remove Configuratore ---

    @Test
    void testRemoveConfiguratoreSuccess() {
        Configuratore initialConf = m.dbConfiguratoreHelper.getItem(CONFIG_USERNAME);
        assertNotNull(initialConf); // Ensure it exists before removal

        o = c.interpreter("remove -c " + CONFIG_USERNAME);
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Removed configurator: " + CONFIG_USERNAME, o.getLogMessage());

        Configuratore removedConf = m.dbConfiguratoreHelper.getItem(CONFIG_USERNAME);
        assertNull(removedConf); // Verify it's actually removed from DB
    }

    @Test
    void testRemoveConfiguratoreFailNotFound() {
        o = c.interpreter("remove -c NON_EXISTENT_CONFIG");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to remove configurator: NON_EXISTENT_CONFIG", o.getLogMessage());
    }

    // --- Remove Fruitore (assuming direct removal by username) ---

    // To properly test this, we need a way to add a fruitore.
    // Assuming Model.addFruitore(username) or similar exists for setup.
    // If not, this test might need adjustment or skipping based on your 'add -f' command logic.
    @Test
    void testRemoveFruitoreSuccess() throws Exception {
        // Manually add a fruitore if `c.interpreter("add -f ...")` doesn't exist
        // For this example, let's assume `Model.addFruitore` directly
        // For the sake of the test, let's assume an add -f command exists
        m.dbFruitoreHelper.addFruitore(new Fruitore(FRUITORE_USERNAME, "password", true, true));
        Fruitore initialFruitore = m.dbFruitoreHelper.getPersona(FRUITORE_USERNAME);
        assertNotNull(initialFruitore);

        o = c.interpreter("remove -f " + FRUITORE_USERNAME);
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Removed fruitore: " + FRUITORE_USERNAME, o.getLogMessage());

        Fruitore removedFruitore = m.dbFruitoreHelper.getPersona(FRUITORE_USERNAME);
        assertNull(removedFruitore);
    }

    @Test
    void testRemoveFruitoreFailNotFound() {
        o = c.interpreter("remove -f NON_EXISTENT_FRUITORE");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to remove fruitore: NON_EXISTENT_FRUITORE", o.getLogMessage());
    }

    // --- Schedule Removal for Volontario, TipoVisita, Luogo (dependent on isExecutable) ---

    @Test
    void testRemoveVolontarioScheduledSuccess() {
        Volontario initialVol = m.dbVolontarioHelper.getPersona(VOL_USERNAME);
        assertNotNull(initialVol);
        assertNull(initialVol.getDeletionDate()); // Should not have a deletion date initially

        o = c.interpreter("remove -v " + VOL_USERNAME);
        assertEquals(Status.INFO, o.getStatus());
        assertTrue(o.getLogMessage().startsWith("Scheduled removal of volunteer: " + VOL_USERNAME));

        Volontario scheduledVol = m.dbVolontarioHelper.getPersona(VOL_USERNAME);
        assertNotNull(scheduledVol);
        assertNotNull(scheduledVol.getDeletionDate()); // Verify deletion date is set
        // Check if deletion date is approximately 2 months from controller.date
        Date expectedDate = c.date.clone().addMonth(2);
        assertEquals(expectedDate.toString(), scheduledVol.getDeletionDate().toString());
    }

    @Test
    void testRemoveVolontarioFailNotFound() {
        o = c.interpreter("remove -v NON_EXISTENT_VOL");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to remove volunteer: NON_EXISTENT_VOL", o.getLogMessage());
    }

    @Test
    void testRemoveVolontarioFailNotExecutable() {
        // Set date to a day where isExecutable() is typically false (e.g., 16th or after for a "try again on 16th" rule)
        c.interpreter("time -s 17/01/2025"); // Assuming this makes isExecutable() false
        Volontario initialVol = m.dbVolontarioHelper.getPersona(VOL_USERNAME);
        assertNotNull(initialVol);

        o = c.interpreter("remove -v " + VOL_USERNAME);
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("RemoveCommand.removeVolontario: not executable", o.getLogMessage());
        assertNull(initialVol.getDeletionDate()); // Ensure deletion date is not set
    }

    @Test
    void testRemoveTipoVisitaScheduledSuccess() {
        TipoVisita initialTv = m.dbTipoVisiteHelper.findTipoVisita(VISIT_TITLE);
        assertNotNull(initialTv);
        assertNull(initialTv.getDeletionDate());

        o = c.interpreter("remove -T \"" + VISIT_TITLE + "\"");
        assertEquals(Status.INFO, o.getStatus());
        assertTrue(o.getLogMessage().startsWith("Scheduled removal of TipoVisita: " + VISIT_TITLE));

        TipoVisita scheduledTv = m.dbTipoVisiteHelper.findTipoVisita(VISIT_TITLE);
        assertNotNull(scheduledTv);
        assertNotNull(scheduledTv.getDeletionDate());
        Date expectedDate = c.date.clone().addMonth(2);
        assertEquals(expectedDate.toString(), scheduledTv.getDeletionDate().toString());
    }

    @Test
    void testRemoveTipoVisitaFailNotFound() {
        o = c.interpreter("remove -T \"NON_EXISTENT_VISIT\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("RemoveCommand.removeTipoVisita: not found", o.getLogMessage());
    }

    @Test
    void testRemoveTipoVisitaFailNotExecutable() {
        c.interpreter("time -s 17/01/2025");
        TipoVisita initialTv = m.dbTipoVisiteHelper.findTipoVisita(VISIT_TITLE);
        assertNotNull(initialTv);

        o = c.interpreter("remove -T \"" + VISIT_TITLE + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("RemoveCommand.removeTipoVisita: not executable", o.getLogMessage());
        assertNull(initialTv.getDeletionDate());
    }

    @Test
    void testRemoveLuogoScheduledSuccess() {
        Luogo initialPlace = m.dbLuoghiHelper.findLuogo(PLACE_NAME);
        assertNotNull(initialPlace);
        assertNull(initialPlace.getDeletionDate());

        o = c.interpreter("remove -L \"" + PLACE_NAME + "\"");
        assertEquals(Status.INFO, o.getStatus());
        assertTrue(o.getLogMessage().startsWith("Scheduled removal of Luogo: " + PLACE_NAME));

        Luogo scheduledPlace = m.dbLuoghiHelper.findLuogo(PLACE_NAME);
        assertNotNull(scheduledPlace);
        assertNotNull(scheduledPlace.getDeletionDate());
        Date expectedDate = c.date.clone().addMonth(2);
        assertEquals(expectedDate.toString(), scheduledPlace.getDeletionDate().toString());
    }

    @Test
    void testRemoveLuogoFailNotFound() {
        o = c.interpreter("remove -L \"NON_EXISTENT_PLACE\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to remove place: NON_EXISTENT_PLACE", o.getLogMessage());
    }

    @Test
    void testRemoveLuogoFailNotExecutable() {
        c.interpreter("time -s 17/01/2025");
        Luogo initialPlace = m.dbLuoghiHelper.findLuogo(PLACE_NAME);
        assertNotNull(initialPlace);

        o = c.interpreter("remove -L \"" + PLACE_NAME + "\"");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("RemoveCommand.removeLuogo: not executable", o.getLogMessage());
        assertNull(initialPlace.getDeletionDate());
    }
}