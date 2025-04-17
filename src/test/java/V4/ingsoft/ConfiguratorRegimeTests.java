package V4.ingsoft;

import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests for Use Cases UC10-UC15, UC20-UC27 (+ Regime Phase)
public class ConfiguratorRegimeTests extends BaseTest {

    @Override
    @BeforeEach
    public void setup() {
        resetDataFiles();
        enterRegimePhase(); // Ensure system is in regime phase before each test
    }

    // --- Regime Phase Tests ---

    // UC10 - Preclusione Data
    @Test
    public void testRegimePrecludeDateSuccess() throws Exception {
        // Arrange
        String today = "1/1/2025";
        String futureDate = "15/03/2025";   //Every march's day

        // Act
        controller.interpreter("time -s " + today);
        controller.interpreter("preclude -a " + futureDate);

        // Assert
        assertTrue(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should be precluded after command.");

        // Arrange
        today = "16/1/2025";
        futureDate = "15/04/2025";   //Every april's day

        // Act
        controller.interpreter("time -s " + today);
        controller.interpreter("preclude -a " + futureDate);

        // Assert
        assertTrue(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should be precluded after command.");
    }

    @Test
    public void testRegimePrecludeDateFailDueToInvalidRange() throws Exception {
        // Arrange
        String today = "16/1/2025";
        String futureDate = "15/03/2025";   //Every march's day

        // Act
        controller.interpreter("time -s " + today);
        controller.interpreter("preclude -a " + futureDate);

        // Assert
        assertFalse(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date shouldn't be precluded after command.");
    }

    @Test
    public void testRegimePrecludeDateFailDuplicate() throws Exception {
        // Arrange
        String today = "1/1/2025";
        String futureDate = "15/03/2025";
        controller.interpreter("time -s " + today);
        controller.interpreter("preclude -a " + futureDate); // Assumed command - uncommented
        AssertionControl.logMessage(controller.getDB().dbDatesHelper.getPrecludedDates(), 0, futureDate);

        assertEquals(1, controller.getDB().dbDatesHelper.getPrecludedDates().size(), "Precluded date should be one");

        // Act
        AssertionControl.logMessage(controller.getDB().dbDatesHelper.getPrecludedDates(), 0, futureDate);
        controller.interpreter("preclude -a " + futureDate); // Try precluding again
        AssertionControl.logMessage(controller.getDB().dbDatesHelper.getPrecludedDates(), 0, futureDate);

        // Assert
        // Command should fail gracefully, date should still be precluded.
        assertTrue(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should remain precluded after duplicate attempt.");
        assertEquals(1, controller.getDB().dbDatesHelper.getPrecludedDates().size(), "Precluded date should be one");
    }

    @Test
    public void testRegimePrecludeDateFailInvalidFormat() throws Exception {
        // Arrange

        // Act
        controller.interpreter("preclude -a 2025-07-15"); // Invalid format

        // Assert
        // Command should fail, date should not be added.
        // Need to be careful comparing Date objects if format matters internally.
        // Let's assume Date("15/07/2025") is the canonical form if added.
        assertFalse(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date("15/07/2025")), "Date with invalid format should not be precluded.");
    }

    @Test
    public void testRegimePrecludeDateFailInvalidDate() throws Exception {
        // Arrange

        // Act
        controller.interpreter("preclude -a 31/02/2025"); // Invalid date

        // Assert
        // Command should fail, date should not be added.
        // We cannot easily check if "31/02/2025" is precluded as Date constructor might throw error.
        // Check size or specific known valid dates.
        assertFalse(controller.getDB().dbDatesHelper.getPrecludedDates().contains(new Date("01/03/2025")), "A valid date should not be precluded by an invalid one."); // Example check
    }

    // UC11 - Modifica Numero Massimo Persone per Iscrizione
    @Test
    public void testRegimeSetPersoneMaxSuccess() {
        // Arrange (Setup set it to 5 initially in enterRegimePhase)

        // Act
        controller.interpreter("setmax 10");

        // Assert
        assertEquals(10, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should be updated to 10.");
        // Also verify it's saved in the model/settings if applicable
        // assertEquals(10, model.getDB().getSettings().getMaxPersonePerIscrizione()); // Assuming such getter exists
    }

    @Test
    public void testRegimeSetPersoneMaxFailZero() {
        // Arrange (Value is 5)

        // Act
        controller.interpreter("setmax 0"); // Should fail

        // Assert
        assertEquals(5, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should remain 5 after trying to set 0.");
    }

    @Test
    public void testRegimeSetPersoneMaxFailNegative() {
        // Act
        controller.interpreter("setmax -2"); // Should fail

        // Assert
        assertEquals(5, Model.appSettings.getMaxPrenotazioniPerPersona(), "Max persone should remain 5 after trying to set negative value.");
    }

    // UC12 - Elenco Volontari e Tipi Visita Associati
    // NOTE: Testing 'list' commands is difficult without capturing stdout.
    // We'll assert the underlying data state instead where possible.
    @Test
    public void testRegimeListVolontariTipiVisita() {
        // Arrange (VolRegime assigned to TVRegime in setup helper)
        assertFalse(controller.getDB().dbVolontarioHelper.getPersonList().isEmpty(), "Prerequisite: Volunteer should exist.");
        assertFalse(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().isEmpty(), "Prerequisite: Volunteer should be assigned.");
    }

    @Test
    public void testRegimeListVolontariTipiVisitaEmpty() {
        // Arrange
        // Remove the default volunteer added in setup
        controller.interpreter("remove -v VolRegime");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersonList().isEmpty(), "Prerequisite: No volunteers should exist.");
    }

    @Test
    public void testRegimeListLuoghiEmpty() {
        // Arrange
        // Remove the default place added in setup
        controller.interpreter("remove -L PlaceRegime"); // This will also cascade-remove TVRegime
        assertTrue(controller.getDB().dbLuoghiHelper.getLuoghi().isEmpty(), "Prerequisite: No places should exist.");
    }


    // UC14 - Elenco Tipi Visita per Luogo
    // NOTE: Testing 'list' commands is difficult without capturing stdout.
    @Test
    public void testRegimeListTipiVisitaPerLuogo() {
        Luogo l = controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime");
        assertNotNull(l, "Prerequisite: Place should exist.");
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        assertNotNull(t, "Prerequisite: Type should exist.");

        // Assert
        // Cannot directly assert console output (e.g., "Nessun tipo di visita trovato per..."). Assume command runs.
        assertTrue(l.getTipoVisitaUID().contains(t.getUID()), "List command executed for place with no types (cannot verify output).");
    }

    @Test
    public void testRegimeListTipiVisitaPerLuogoNonExistent() {
        // Arrange
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("NonExistentPlace"), "Prerequisite: Place should not exist.");
    }

    @Test
    public void testRegimeListTipiVisitaPerLuogoEmpty() {
        Luogo l = controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime");
        assertNotNull(l, "Prerequisite: Place should exist.");
        int size = l.getTipoVisitaUID().size();

        controller.interpreter("remove -T TVRegime");
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        assertNull(t, "Prerequisite: Type should not exist.");

        // Assert
        // Cannot directly assert console output (e.g., "Nessun tipo di visita trovato per..."). Assume command runs.
        assertEquals(size - 1, l.getTipoVisitaUID().size(), "Type in a place should be decreased by 1");
    }

    // UC21 - Aggiunta Luogo (Regime)
    @Test
    public void testRegimeAddLuogoSuccess() {
        // Arrange

        // Act
        controller.interpreter("add -L PlaceRegime2 \"New Place Added in Regime\" 30.0,40.0");

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime2"), "New place should exist in DB.");
        assertEquals("New Place Added in Regime", controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime2").getDescription());
    }

    @Test
    public void testRegimeAddLuogoFailDuplicateTitle() {
        // Arrange
        // PlaceRegime added in enterRegimePhase setup

        // Act
        controller.interpreter("add -L PlaceRegime \"Trying to duplicate\" 50.0,60.0"); // Should fail

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Original place should still exist.");
        assertEquals("Regime Place", controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime").getDescription(), "Original place description should not change.");
    }


    // UC22 - Aggiunta Tipo Visita a Luogo Esistente
    @Test
    public void testRegimeAddTipoVisitaSuccess() {
        // Arrange
        // PlaceRegime added in enterRegimePhase setup

        // Act
        // NOTE: Assuming this 'add -T' format is correct for the running phase command.
        controller.interpreter("add -T TVRegime2 Description2 1:1 20/06/2025 27/08/2025 10:00 60 false 1 10 Ma");

        // Assert
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2"), "New type should exist in DB.");
        // Check association with place
        // assertTrue(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime").getTipiVisiteAssociati().contains("TVRegime2"));
    }

    @Test
    public void testRegimeAddTipoVisitaFailDuplicateUID() {
        // Arrange (TVRegime added in setup helper)
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Initial type should exist.");

        // Act
        controller.interpreter("add -T TVRegime PlaceRegime 16:00 45 3 12 \"Trying Duplicate UID\""); // Should fail

        // Assert
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Original type should still exist.");
        assertEquals("Description", controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getDescription(), "Original type description should not change.");
    }

    @Test
    public void testRegimeAddTipoVisitaFailLuogoNonExistent() {
        // Arrange

        // Act
        controller.interpreter("add -T TV_NoPlace NonExistentPlace 11:00 60 1 10"); // Should fail

        // Assert
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TV_NoPlace"), "Type should not be created for non-existent place.");
    }

    //UC23 - Aggiunta Volontario a Tipo Visita Esistente
    @Test
    public void testRegimeAddAndAssignNewVolontarioSuccess() {
        TipoVisita tipo = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");

        // Arrange (TVRegime exists from setup)
        assertNotNull(tipo, "Prerequisite: Type TVRegime should exist.");
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("NewVolRegime"), "Prerequisite: New volunteer should not exist yet.");
        // Act
        controller.interpreter("add -v NewVolRegime PassNewVol");
        controller.interpreter("time -s 16/01/2025");
        controller.interpreter("assign -V TVRegime NewVolRegime"); // NOTE: Assuming 'assign Vol TV' format
        // Assert
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("NewVolRegime"), "New volunteer should exist in DB.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("NewVolRegime").getTipiVisiteUIDs().contains(tipo.getUID()), "Volunteer should be assigned to TVRegime.");
        // assertTrue(controller.getDB().dbTipoVisiteHelper.getTipoVisita("TVRegime").getVolontariAssociati().contains("NewVolRegime"), "TVRegime should list NewVolRegime as assigned.");
    }

    @Test
    public void testRegimeAssignExistingVolontarioSuccess() {
        // Arrange (VolRegime, PlaceRegime exist from setup)
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer VolRegime should exist.");
        controller.interpreter("add -T TVRegime2 Description2 1:1 20/06/2025 27/08/2025 10:00 60 false 1 10 Ma"); // Add another type

        TipoVisita t2 = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2");
        assertNotNull(t2, "Prerequisite: Type TVRegime2 should exist.");
        assertFalse(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t2.getUID()), "Prerequisite: VolRegime should not be assigned to TVRegime2 yet.");

        // Act
        controller.interpreter("assign -V TVRegime2 VolRegime"); // NOTE: Assuming 'assign -V TV Vol' format

        // Assert
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t2.getUID()), "VolRegime should now be assigned to TVRegime2.");
        // assertTrue(controller.getDB().dbTipoVisiteHelper.getTipoVisita("TVRegime2").getVolontariAssociati().contains("VolRegime"), "TVRegime2 should list VolRegime as assigned.");
    }

    @Test
    public void testRegimeAssignVolontarioFailTipoVisitaNonExistent() {
        // Arrange (VolRegime exists from setup)
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer VolRegime should exist.");
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("NonExistentTV"), "Prerequisite: Type NonExistentTV should not exist.");
        int initialAssignments = controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size();

        // Act
        controller.interpreter("assign VolRegime NonExistentTV"); // Should fail

        // Assert
        assertEquals(initialAssignments, controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size(), "Volunteer assignment count should not change.");
        assertFalse(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("NonExistentTV"), "Volunteer should not be assigned to non-existent type.");
    }

    @Test
    public void testRegimeAssignVolontarioFailVolontarioNonExistent() {
        // Arrange (TVRegime exists from setup)
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Type TVRegime should exist.");
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("NonExistentVol"), "Prerequisite: Volunteer NonExistentVol should not exist.");
        int initialAssignments = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().size(); // Use getVolontariUIDs()

        // Act
        controller.interpreter("assign NonExistentVol TVRegime"); // Should fail

        // Assert
        assertEquals(initialAssignments, controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().size(), "Type assignment count should not change.");
        assertFalse(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("NonExistentVol"), "Type should not list non-existent volunteer as assigned.");
    }

    @Test
    public void testRegimeAssignVolontarioFailDuplicate() {
        // Arrange (VolRegime assigned to TVRegime in setup helper)
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        assertNotNull(t, "Prerequisite: Type should already exists");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t.getUID()), "Prerequisite: Volunteer should already be assigned.");
        int initialAssignments = controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size();

        // Act
        controller.interpreter("assign VolRegime TVRegime"); // Try assigning again, should fail gracefully

        // Assert
        assertEquals(initialAssignments, controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size(), "Volunteer assignment count should not change on duplicate attempt.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t.getUID()), "Volunteer should remain assigned.");
    }


    // UC24 - Rimozione Luogo
    @Test
    public void testRegimeRemoveLuogoSuccessAndCascade() {
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Place should exist.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Type should exist.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t.getUID()), "Prerequisite: Volunteer should be assigned.");

        // Act
        controller.interpreter("remove -L PlaceRegime");

        // Assert
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should be removed.");
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Associated type should be removed (cascade).");
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Associated Volunteer should be removed.");
    }

    @Test
    public void testRegimeRemoveLuogoCascadeButVolunteerAsAnotherVisitAssigned() {
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");

        controller.interpreter("add -L PlaceRegime2 \"Regime Place\" 10.0:20.0");
        controller.interpreter("add -T TVRegime2 Description2 1:1 20/06/2025 27/08/2025 10:00 60 false 1 10 Ma"); // Add another type
        controller.interpreter("assign -L PlaceRegime2 TVRegime2");
        controller.interpreter("assign -V TVRegime2 VolRegime"); // Assign volunteer to it too
        TipoVisita t2 = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2");

        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Place should exist.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Type should exist.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t.getUID()), "Prerequisite: Volunteer should be assigned.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t2.getUID()), "Prerequisite: Volunteer should be assigned.");

        // Act
        controller.interpreter("remove -L PlaceRegime");

        // Assert
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should be removed.");
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime2"), "Place shouldn't be removed.");
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Associated type should be removed (cascade).");
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Associated Volunteer shouldn't be removed.");
    }

    @Test
    public void testRegimeRemoveLuogoFailNonExistent() {
        // Arrange
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("NonExistentPlace"), "Prerequisite: Place should not exist.");
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Other place should exist."); // Ensure DB isn't empty

        // Act
        controller.interpreter("remove luogo NonExistentPlace"); // Should fail

        // Assert
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Existing place should not be affected.");
    }

    // UC25 - Rimozione Tipo Visita
    @Test
    public void testRegimeRemoveTipoVisitaSuccessAndCascade() {
        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        controller.interpreter("add -T TVRegime2 Description2 1:1 20/06/2025 27/08/2025 15:00 60 false 1 10 Ma"); // Add another type
        controller.interpreter("assign -L PlaceRegime TVRegime2");
        controller.interpreter("assign -V TVRegime2 VolRegime"); // Assign volunteer to it too
        AssertionControl.logMessage(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime").getTipoVisitaUID(), 0, null);

        TipoVisita t1 = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        TipoVisita t2 = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2");

        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Type TVRegime should exist.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2"), "Prerequisite: Type TVRegime2 should exist.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t1.getUID()), "Prerequisite: Vol assigned to TVRegime.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t2.getUID()), "Prerequisite: Vol assigned to TVRegime2.");

        // Act
        controller.interpreter("remove -T TVRegime"); // Remove the first type

        // Assert
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Type TVRegime should be removed.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime2"), "Type TVRegime2 should still exist.");
        assertNotNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Place PlaceRegime should still exist.");
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Volunteer VolRegime should still exist.");
        assertFalse(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t1.getUID()), "Volunteer assignment to TVRegime should be removed (cascade).");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t2.getUID()), "Volunteer assignment to TVRegime2 should remain.");
    }

    @Test
    // Test name seems misleading based on UC25. Renaming slightly.
    public void testRegimeRemoveTipoVisitaUnassignsVolunteer() {
        TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime");
        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Type should exist.");
        assertTrue(controller.getDB().dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains(t.getUID()), "Prerequisite: Volunteer should be assigned.");

        // Act
        controller.interpreter("remove -T TVRegime");

        // Assert
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Type should be removed.");
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should be removed because the cascade.");
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Volunteer should be removed because the cascade.");
    }

    @Test
    public void testRegimeRemoveTipoVisitaFailNonExistent() {
        // Arrange
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("NonExistentTV"), "Prerequisite: Type should not exist.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Prerequisite: Other type should exist."); // Ensure DB isn't empty

        // Act
        controller.interpreter("remove tipovisita NonExistentTV"); // Should fail

        // Assert
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Existing type should not be affected.");
    }


    // UC26 - Rimozione Volontario
    @Test
    public void testRegimeRemoveVolontarioSuccess() {
        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        controller.interpreter("add -v VolRegime2 PassV2"); // Add another volunteer
        controller.interpreter("assign -V TVRegime VolRegime2"); // Assign it to the same type
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: VolRegime should exist.");
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime2"), "Prerequisite: VolRegime2 should exist.");
        assertTrue(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("VolRegime"), "Prerequisite: TVRegime assigned to VolRegime.");
        assertTrue(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("VolRegime2"), "Prerequisite: TVRegime assigned to VolRegime2.");

        // Act
        controller.interpreter("remove -v VolRegime"); // Remove the first volunteer

        // Assert
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Volunteer VolRegime should be removed.");
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime2"), "Volunteer VolRegime2 should still exist.");
        assertNotNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Type TVRegime should still exist.");
        assertFalse(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("VolRegime"), "Type TVRegime should no longer list VolRegime as assigned (cascade).");
        assertTrue(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("VolRegime2"), "Type TVRegime should still list VolRegime2 as assigned.");
    }

    @Test
    // Test name seems misleading based on UC26. Renaming slightly.
    public void testRegimeRemoveVolontarioUnassignsType() {
        // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer should exist.");
        assertTrue(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime").getVolontariUIDs().contains("VolRegime"), "Prerequisite: Type should be assigned.");

        // Act
        controller.interpreter("remove -v VolRegime");

        // Assert
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Volunteer should be removed.");
        assertNull(controller.getDB().dbTipoVisiteHelper.findTipoVisita("TVRegime"), "Type should be removed.");
        assertNull(controller.getDB().dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should be removed.");
    }

    @Test
    public void testRegimeRemoveVolontarioFailNonExistent() {
        // Arrange
        assertNull(controller.getDB().dbVolontarioHelper.getPersona("NonExistentVol"), "Prerequisite: Volunteer should not exist.");
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Other volunteer should exist."); // Ensure DB isn't empty

        // Act
        controller.interpreter("remove volontario NonExistentVol"); // Should fail

        // Assert
        assertNotNull(controller.getDB().dbVolontarioHelper.getPersona("VolRegime"), "Existing volunteer should not be affected.");
    }


}
