package V4.ingsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date; // Assuming Date utility exists

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Tests for Use Cases UC10-UC15, UC20-UC27 (+ Regime Phase)
public class ConfiguratorRegimeTests {
    private Controller controller;
    private Model model;
    private String configPath = "data/configuratori.json";
    private String volontariPath = "data/volontari.json";
    private String fruitoriPath = "data/fruitori.json";
    private String luoghiPath = "data/luoghi.json";
    private String tipiVisitaPath = "data/tipoVisite.json";
    

    // Helper to reset data files before each test
    private void resetDataFiles() {
        // Delete existing files to ensure clean state for setup
        try { Files.deleteIfExists(Paths.get(configPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(volontariPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(fruitoriPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(luoghiPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(tipiVisitaPath)); } catch (IOException e) { /* Ignore */ }

        // Re-initialize model and controller for a fresh start
        model = new Model(); // Should implicitly create default ADMIN/PASSWORD if configPath is empty
        controller = new Controller(model);
        // DO NOT skip setup testing here, as these tests ARE the setup phase
    }

    // Helper method to complete the setup phase and log in as a regular configurator
    private void enterRegimePhase() {
        // 1. First absolute login and password change for ADMIN
        controller.interpreter("login ADMIN PASSWORD");
        controller.interpreter("changepsw newAdminPass");

        // 2. Complete Setup Steps using known setup commands
        controller.interpreter("setambito TestAreaRegime");
        controller.interpreter("setmax 5");
        controller.interpreter("add -L PlaceRegime \"Regime Place\" 10.0:20.0");
        // Cannot add types/volunteers/assignments during setup via commands
        controller.interpreter("done"); // Finalize setup

        controller.interpreter("time -s 16/1/2025");

        // 3. Add another configurator using the running phase command (now logged in as ADMIN)
        // Add initial TipoVisita and Volontario needed for some regime tests
        // Assuming 'add -t' format: <UID> <LuogoTitle> <OraInizio> <Durata> <MinPart> <MaxPart> [Descrizione]
        // Assuming 'assign' format: <VolUsername> <TipoVisitaUID>
        controller.interpreter("add -t TVRegime Description 1:1 20/06/2025 27/08/2025 10:00 60 false 1 10 Ma");
        controller.interpreter("assign -L PlaceRegime TVRegime");
        controller.interpreter("add -v VolRegime PassVol");
        controller.interpreter("assign -V TVRegime VolRegime");
        controller.interpreter("add -c configRegime passRegime");

        // 4. Logout ADMIN and Login as the new configurator
        controller.interpreter("logout");
        controller.interpreter("login configRegime passRegime");
        controller.interpreter("changepsw passRegime");
        assertNotNull(controller.user, "Configurator should be logged in for regime tests.");
        assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "User should be of type CONFIGURATORE.");
    }


    @BeforeEach
    public void setup() {
        resetDataFiles();
        enterRegimePhase(); // Ensure system is in regime phase before each test
    }

     @AfterEach
     public void cleanup() {
         controller.interpreter("logout"); // Ensure logout
         // Optional: Clean up files again after test if needed
         // resetDataFiles();
     }

    // --- Regime Phase Tests ---

    // UC10 - Preclusione Data
    // @Test
    // public void testRegimePrecludeDateSuccess() {
    //     // Arrange
    //     String today = "1/1/2025";
    //     String futureDate = "15/03/2025";   //Every march's day

    //     // Act
    //     controller.interpreter("time -s " + today);
    //     controller.interpreter("preclude " + futureDate);

    //     // Assert
    //     assertTrue(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should be precluded after command.");

    //     // Arrange
    //     today = "16/1/2025";
    //     futureDate = "15/04/2025";   //Every april's day

    //     // Act
    //     controller.interpreter("time -s " + today);
    //     controller.interpreter("preclude " + futureDate);

    //     // Assert
    //     assertTrue(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should be precluded after command.");
    // }

    // @Test
    // public void testRegimePrecludeDateFailDueToInvalidRange() {
    //     // Arrange
    //     String today = "16/1/2025";
    //     String futureDate = "15/03/2025";   //Every march's day

    //     // Act
    //     controller.interpreter("time -s " + today);
    //     controller.interpreter("preclude " + futureDate);

    //     // Assert
    //     assertFalse(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date shouldn't be precluded after command.");
    // }

    // @Test
    // public void testRegimePrecludeDateFailDuplicate() {
    //     // Arrange
    //     String today = "1/1/2025";
    //     String futureDate = "15/03/2025";
    //     controller.interpreter("time -s " + today);
    //     controller.interpreter("preclude " + futureDate); // Assumed command - uncommented

    //     assertEquals(1, controller.db.dbDatesHelper.getPrecludedDates().size(), "Precluded date should be one");

    //     // Act
    //     controller.interpreter("preclude " + futureDate); // Try precluding again

    //     // Assert
    //     // Command should fail gracefully, date should still be precluded.
    //     assertTrue(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date(futureDate)), "Date should remain precluded after duplicate attempt.");
    //     assertEquals(1, controller.db.dbDatesHelper.getPrecludedDates().size(), "Precluded date should be one");
    // }

    // @Test
    // public void testRegimePrecludeDateFailInvalidFormat() {
    //     // Arrange

    //     // Act
    //     controller.interpreter("preclude 2025-07-15"); // Invalid format

    //     // Assert
    //     // Command should fail, date should not be added.
    //     // Need to be careful comparing Date objects if format matters internally.
    //     // Let's assume Date("15/07/2025") is the canonical form if added.
    //     assertFalse(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date("15/07/2025")), "Date with invalid format should not be precluded.");
    // }

    //  @Test
    //  public void testRegimePrecludeDateFailInvalidDate() {
    //      // Arrange

    //      // Act
    //      controller.interpreter("preclude 31/02/2025"); // Invalid date

    //     // Assert
    //     // Command should fail, date should not be added.
    //     // We cannot easily check if "31/02/2025" is precluded as Date constructor might throw error.
    //     // Check size or specific known valid dates.
    //     assertFalse(controller.db.dbDatesHelper.getPrecludedDates().contains(new Date("01/03/2025")), "A valid date should not be precluded by an invalid one."); // Example check
    //     // TODO: Check log output for error.
    //  }

    //  // TODO: Add test for precluding outside the allowed time window (requires date simulation/mocking Controller.date)


    // // UC11 - Modifica Numero Massimo Persone per Iscrizione
    // @Test
    // public void testRegimeSetPersoneMaxSuccess() {
    //     // Arrange (Setup set it to 5 initially in enterRegimePhase)

    //     // Act
    //     controller.interpreter("setmax 10");

    //     // Assert
    //     assertEquals(10, controller.maxPrenotazioniPerPersona, "Max persone should be updated to 10.");
    //     // Also verify it's saved in the model/settings if applicable
    //     // assertEquals(10, model.db.getSettings().getMaxPersonePerIscrizione()); // Assuming such getter exists
    // }

    // @Test
    // public void testRegimeSetPersoneMaxFailZero() {
    //     // Arrange (Value is 5)

    //     // Act
    //     controller.interpreter("setmax 0"); // Should fail

    //     // Assert
    //     assertEquals(5, controller.maxPrenotazioniPerPersona, "Max persone should remain 5 after trying to set 0.");
    //     // TODO: Check log output for error.
    // }

    //  @Test
    //  public void testRegimeSetPersoneMaxFailNegative() {
    //      // Arrange (Value is 5)

    //      // Act
    //      controller.interpreter("setmax -2"); // Should fail

    //      // Assert
    //      assertEquals(5, controller.maxPrenotazioniPerPersona, "Max persone should remain 5 after trying to set negative value.");
    //      // TODO: Check log output for error.
    //  }

    // // UC12 - Elenco Volontari e Tipi Visita Associati
    // // NOTE: Testing 'list' commands is difficult without capturing stdout.
    // // We'll assert the underlying data state instead where possible.
    // @Test
    // public void testRegimeListVolontariTipiVisita() {
    //     // Arrange (VolRegime assigned to TVRegime in setup helper)
    //     assertFalse(controller.db.dbVolontarioHelper.getPersonList().isEmpty(), "Prerequisite: Volunteer should exist.");
    //     assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().isEmpty(), "Prerequisite: Volunteer should be assigned.");

    //     // Act
    //     controller.interpreter("list volontari");

    //     // Assert
    //     // Cannot directly assert console output. Assume command runs if data exists.
    //     assertTrue(true, "List command executed (cannot verify output).");
    // }

    // @Test
    // public void testRegimeListVolontariTipiVisitaEmpty() {
    //     // Arrange
    //     // Remove the default volunteer added in setup
    //     controller.interpreter("remove volontario VolRegime");
    //     assertTrue(controller.db.dbVolontarioHelper.getPersonList().isEmpty(), "Prerequisite: No volunteers should exist.");

    //     // Act
    //     controller.interpreter("list volontari");

    //     // Assert
    //     // Cannot directly assert console output (e.g., "Nessun volontario trovato."). Assume command runs.
    //     assertTrue(true, "List command executed for empty list (cannot verify output).");
    // }

    // // UC13 - Elenco Luoghi Visitabili
    // // NOTE: Testing 'list' commands is difficult without capturing stdout.
    // @Test
    // public void testRegimeListLuoghi() {
    //     // Arrange (PlaceRegime added in setup)
    //     assertFalse(controller.db.dbLuoghiHelper.getLuoghi().isEmpty(), "Prerequisite: Place should exist.");

    //     // Act
    //     controller.interpreter("list luoghi");

    //     // Assert
    //     // Cannot directly assert console output. Assume command runs if data exists.
    //     assertTrue(true, "List command executed (cannot verify output).");
    // }

    //  @Test
    //  public void testRegimeListLuoghiEmpty() {
    //      // Arrange
    //      // Remove the default place added in setup
    //      controller.interpreter("remove luogo PlaceRegime"); // This will also cascade-remove TVRegime
    //      assertTrue(controller.db.dbLuoghiHelper.getLuoghi().isEmpty(), "Prerequisite: No places should exist.");

    //      // Act
    //      controller.interpreter("list luoghi");

    //      // Assert
    //      // Cannot directly assert console output (e.g., "Nessun luogo trovato."). Assume command runs.
    //      assertTrue(true, "List command executed for empty list (cannot verify output).");
    //  }


    // // UC14 - Elenco Tipi Visita per Luogo
    // // NOTE: Testing 'list' commands is difficult without capturing stdout.
    // @Test
    // public void testRegimeListTipiVisitaPerLuogo() {
    //     // Arrange (PlaceRegime and TVRegime added/linked in setup)
    //     assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Place should exist.");
    //     assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type should exist.");
    //     // Check association (might need specific getter or check list)
    //     // assertTrue(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime").getTipiVisiteAssociati().contains("TVRegime"));

    //     // Act
    //     controller.interpreter("list tipivisitaperluogo PlaceRegime");

    //     // Assert
    //     // Cannot directly assert console output. Assume command runs if data exists.
    //     assertTrue(true, "List command executed (cannot verify output).");
    // }

    // @Test
    // public void testRegimeListTipiVisitaPerLuogoNonExistent() {
    //     // Arrange
    //     assertNull(controller.db.dbLuoghiHelper.findLuogo("NonExistentPlace"), "Prerequisite: Place should not exist.");

    //     // Act
    //     controller.interpreter("list tipivisitaperluogo NonExistentPlace");

    //     // Assert
    //     // Cannot directly assert console output (e.g., "Luogo non trovato."). Assume command runs.
    //     assertTrue(true, "List command executed for non-existent place (cannot verify output).");
    // }

    //  @Test
    //  public void testRegimeListTipiVisitaPerLuogoEmpty() {
    //      // Arrange
    //      // Remove the default type associated with the place
    //      controller.interpreter("remove tipovisita TVRegime");
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Place should exist.");
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type should not exist.");
    //      // assertTrue(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime").getTipiVisiteAssociati().isEmpty()); // Check association list

    //      // Act
    //      controller.interpreter("list tipivisitaperluogo PlaceRegime");

    //      // Assert
    //      // Cannot directly assert console output (e.g., "Nessun tipo di visita trovato per..."). Assume command runs.
    //      assertTrue(true, "List command executed for place with no types (cannot verify output).");
    //  }

    // // UC15 - Elenco Visite per Stato
    // // NOTE: Testing 'list' commands is difficult without capturing stdout.
    // // NOTE: Requires UC20 (Plan Generation) to be implemented and callable.
    // @Test
    // public void testRegimeListVisitePerStatoProposta() {
    //     // Arrange:
    //     // TODO: Implement and call plan generation (UC20) here.
    //     // controller.interpreter("generate plan <month>"); // Hypothetical command
    //     // Assume plan generation creates visits in PROPOSED state.
    //     // assertFalse(controller.db.dbVisiteHelper.getVisiteProposte().isEmpty(), "Prerequisite: Visits in PROPOSED state should exist.");

    //     // Act
    //     controller.interpreter("list visite proposta");

    //     // Assert
    //     assertTrue(true, "List command executed (cannot verify output or prerequisites). Needs UC20.");
    // }

    //  @Test
    //  public void testRegimeListVisitePerStatoInvalid() {
    //      // Arrange

    //      // Act
    //      controller.interpreter("list visite unknown_state"); // Invalid state

    //      // Assert
    //      // Command should fail gracefully. Cannot assert output.
    //      assertTrue(true, "List command executed with invalid state (cannot verify output).");
    //      // TODO: Check log output for error.
    //  }

    //  @Test
    //  public void testRegimeListVisitePerStatoEmpty() {
    //      // Arrange
    //      // Ensure no visits exist in the 'confirmed' state (likely true after reset)
    //      assertTrue(controller.db.dbVisiteHelper.getConfermate().isEmpty(), "Prerequisite: No visits in CONFIRMED state should exist.");

    //      // Act
    //      controller.interpreter("list visite confermata");

    //      // Assert
    //      // Cannot assert output (e.g., "Nessuna visita trovata..."). Assume command runs.
    //      assertTrue(true, "List command executed for empty state (cannot verify output).");
    //   }


    //  // TODO: UC20 - Implement tests for Produzione Piano Visite Mensile

    //  // UC21 - Aggiunta Luogo (Regime)
    //  @Test
    //  public void testRegimeAddLuogoSuccess() {
    //      // Arrange

    //      // Act
    //      controller.interpreter("add -L PlaceRegime2 \"New Place Added in Regime\" 30.0,40.0");

    //      // Assert
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime2"), "New place should exist in DB.");
    //      assertEquals("New Place Added in Regime", controller.db.dbLuoghiHelper.findLuogo("PlaceRegime2").getDescription());
    //  }

    //  @Test
    //  public void testRegimeAddLuogoFailDuplicateTitle() {
    //      // Arrange
    //      // PlaceRegime added in enterRegimePhase setup

    //      // Act
    //      controller.interpreter("add -L PlaceRegime \"Trying to duplicate\" 50.0,60.0"); // Should fail

    //      // Assert
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Original place should still exist.");
    //      assertEquals("Regime Place", controller.db.dbLuoghiHelper.findLuogo("PlaceRegime").getDescription(), "Original place description should not change.");
    //      // TODO: Check log output for error. Check count of places hasn't increased.
    //  }

    //  // TODO: Add tests for adding Luogo with associated TipiVisita/Volontari if 'add -L' command supports it directly.

    //  // UC22 - Aggiunta Tipo Visita a Luogo Esistente
    //  @Test
    //  public void testRegimeAddTipoVisitaSuccess() {
    //      // Arrange
    //      // PlaceRegime added in enterRegimePhase setup

    //      // Act
    //      // NOTE: Assuming this 'add -t' format is correct for the running phase command.
    //      controller.interpreter("add -t TVRegime2 PlaceRegime 14:00 90 1 8 \"New Tour\"");

    //      // Assert
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime2"), "New type should exist in DB.");
    //      // Check association with place
    //      // assertTrue(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime").getTipiVisiteAssociati().contains("TVRegime2"));
    //  }

    //  @Test
    //  public void testRegimeAddTipoVisitaFailDuplicateUID() {
    //      // Arrange (TVRegime added in setup helper)
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Initial type should exist.");

    //      // Act
    //      controller.interpreter("add -t TVRegime PlaceRegime 16:00 45 3 12 \"Trying Duplicate UID\""); // Should fail

    //      // Assert
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Original type should still exist.");
    //      assertEquals("Initial Tour", controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getDescription(), "Original type description should not change.");
    //      // TODO: Check log output for error. Check count of types hasn't increased.
    //  }

    //  @Test
    //  public void testRegimeAddTipoVisitaFailLuogoNonExistent() {
    //      // Arrange

    //      // Act
    //      controller.interpreter("add -t TV_NoPlace NonExistentPlace 11:00 60 1 10"); // Should fail

    //      // Assert
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TV_NoPlace"), "Type should not be created for non-existent place.");
    //      // TODO: Check log output for error.
    //  }

    //  // TODO: Add test for temporal constraint violation (UC22 Precondition iv) - Requires specific model logic check (e.g., overlapping times).


     // UC23 - Aggiunta Volontario a Tipo Visita Esistente
     @Test
     public void testRegimeAddAndAssignNewVolontarioSuccess() {
         TipoVisita tipo = controller.db.dbTipoVisiteHelper.findTipoVisita("TVRegime");

         // Arrange (TVRegime exists from setup)
         assertNotNull(tipo, "Prerequisite: Type TVRegime should exist.");
         assertNull(controller.db.dbVolontarioHelper.getPersona("NewVolRegime"), "Prerequisite: New volunteer should not exist yet.");
         // Act
         controller.interpreter("add -v NewVolRegime PassNewVol");
         controller.interpreter("time -s 16/01/2025");
         controller.interpreter("assign -V TVRegime NewVolRegime"); // NOTE: Assuming 'assign Vol TV' format
         // Assert
         assertNotNull(controller.db.dbVolontarioHelper.getPersona("NewVolRegime"), "New volunteer should exist in DB.");
         assertTrue(controller.db.dbVolontarioHelper.getPersona("NewVolRegime").getTipiVisiteUIDs().contains(tipo.getUID()), "Volunteer should be assigned to TVRegime.");
         // assertTrue(controller.db.dbTipoVisiteHelper.getTipoVisita("TVRegime").getVolontariAssociati().contains("NewVolRegime"), "TVRegime should list NewVolRegime as assigned.");
     }

    //  @Test
    //  public void testRegimeAssignExistingVolontarioSuccess() {
    //      // Arrange (VolRegime, PlaceRegime exist from setup)
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer VolRegime should exist.");
    //      controller.interpreter("add -t TVRegime2 PlaceRegime 15:00 60 1 5 \"Second Tour\""); // Add another type
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime2"), "Prerequisite: Type TVRegime2 should exist.");
    //      assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime2"), "Prerequisite: VolRegime should not be assigned to TVRegime2 yet.");

    //      // Act
    //      controller.interpreter("assign VolRegime TVRegime2"); // NOTE: Assuming 'assign Vol TV' format

    //      // Assert
    //      assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime2"), "VolRegime should now be assigned to TVRegime2.");
    //      // assertTrue(controller.db.dbTipoVisiteHelper.getTipoVisita("TVRegime2").getVolontariAssociati().contains("VolRegime"), "TVRegime2 should list VolRegime as assigned.");
    //  }

    //  @Test
    //  public void testRegimeAssignVolontarioFailTipoVisitaNonExistent() {
    //      // Arrange (VolRegime exists from setup)
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer VolRegime should exist.");
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("NonExistentTV"), "Prerequisite: Type NonExistentTV should not exist.");
    //      int initialAssignments = controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size();

    //      // Act
    //      controller.interpreter("assign VolRegime NonExistentTV"); // Should fail

    //      // Assert
    //      assertEquals(initialAssignments, controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size(), "Volunteer assignment count should not change.");
    //      assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("NonExistentTV"), "Volunteer should not be assigned to non-existent type.");
    //      // TODO: Check log output for error.
    //  }

    //  @Test
    //  public void testRegimeAssignVolontarioFailVolontarioNonExistent() {
    //      // Arrange (TVRegime exists from setup)
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type TVRegime should exist.");
    //      assertNull(controller.db.dbVolontarioHelper.getPersona("NonExistentVol"), "Prerequisite: Volunteer NonExistentVol should not exist.");
    //      int initialAssignments = controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().size(); // Use getVolontariUIDs()

    //      // Act
    //      controller.interpreter("assign NonExistentVol TVRegime"); // Should fail

    //      // Assert
    //      assertEquals(initialAssignments, controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().size(), "Type assignment count should not change.");
    //      assertFalse(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("NonExistentVol"), "Type should not list non-existent volunteer as assigned.");
    //      // TODO: Check log output for error.
    //  }

    //   @Test
    //   public void testRegimeAssignVolontarioFailDuplicate() {
    //       // Arrange (VolRegime assigned to TVRegime in setup helper)
    //       assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Prerequisite: Volunteer should already be assigned.");
    //       int initialAssignments = controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size();

    //       // Act
    //       controller.interpreter("assign VolRegime TVRegime"); // Try assigning again, should fail gracefully

    //       // Assert
    //       assertEquals(initialAssignments, controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().size(), "Volunteer assignment count should not change on duplicate attempt.");
    //       assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Volunteer should remain assigned.");
    //       // TODO: Check log output for error/warning.
    //   }


    //  // UC24 - Rimozione Luogo
    //  @Test
    //  public void testRegimeRemoveLuogoSuccessAndCascade() {
    //      // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Place should exist.");
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type should exist.");
    //      assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Prerequisite: Volunteer should be assigned.");

    //      // Act
    //      controller.interpreter("remove luogo PlaceRegime");

    //      // Assert
    //      assertNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should be removed.");
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Associated type should be removed (cascade).");
    //      // Volunteer still exists, but assignment is gone because the type is gone.
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Volunteer should still exist.");
    //      assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Volunteer assignment should be removed (cascade).");
    //  }

    //  @Test
    //  public void testRegimeRemoveLuogoFailNonExistent() {
    //      // Arrange
    //      assertNull(controller.db.dbLuoghiHelper.findLuogo("NonExistentPlace"), "Prerequisite: Place should not exist.");
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Prerequisite: Other place should exist."); // Ensure DB isn't empty

    //      // Act
    //      controller.interpreter("remove luogo NonExistentPlace"); // Should fail

    //      // Assert
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Existing place should not be affected.");
    //      // TODO: Check log output for error.
    //  }

    //  // UC25 - Rimozione Tipo Visita
    //  @Test
    //  public void testRegimeRemoveTipoVisitaSuccessAndCascade() {
    //      // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
    //      controller.interpreter("add -t TVRegime2 PlaceRegime 15:00 60 1 5 \"Second Tour\""); // Add another type
    //      controller.interpreter("assign VolRegime TVRegime2"); // Assign volunteer to it too
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type TVRegime should exist.");
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime2"), "Prerequisite: Type TVRegime2 should exist.");
    //      assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Prerequisite: Vol assigned to TVRegime.");
    //      assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime2"), "Prerequisite: Vol assigned to TVRegime2.");

    //      // Act
    //      controller.interpreter("remove tipovisita TVRegime"); // Remove the first type

    //      // Assert
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Type TVRegime should be removed.");
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime2"), "Type TVRegime2 should still exist.");
    //      assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Place PlaceRegime should still exist.");
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Volunteer VolRegime should still exist.");
    //      assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Volunteer assignment to TVRegime should be removed (cascade).");
    //      assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime2"), "Volunteer assignment to TVRegime2 should remain.");
    //  }

    //   @Test
    //   // Test name seems misleading based on UC25. Renaming slightly.
    //   public void testRegimeRemoveTipoVisitaUnassignsVolunteer() {
    //       // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
    //       assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Type should exist.");
    //       assertTrue(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Prerequisite: Volunteer should be assigned.");

    //       // Act
    //       controller.interpreter("remove tipovisita TVRegime");

    //       // Assert
    //       assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Type should be removed.");
    //       assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should NOT be removed.");
    //       assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Volunteer should NOT be removed.");
    //       assertFalse(controller.db.dbVolontarioHelper.getPersona("VolRegime").getTipiVisiteUIDs().contains("TVRegime"), "Volunteer assignment should be removed (cascade).");
    //   }

    //  @Test
    //  public void testRegimeRemoveTipoVisitaFailNonExistent() {
    //      // Arrange
    //      assertNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("NonExistentTV"), "Prerequisite: Type should not exist.");
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Prerequisite: Other type should exist."); // Ensure DB isn't empty

    //      // Act
    //      controller.interpreter("remove tipovisita NonExistentTV"); // Should fail

    //      // Assert
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Existing type should not be affected.");
    //      // TODO: Check log output for error.
    //  }


    //  // UC26 - Rimozione Volontario
    //  @Test
    //  public void testRegimeRemoveVolontarioSuccess() {
    //      // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
    //      controller.interpreter("add -v VolRegime2 PassV2"); // Add another volunteer
    //      controller.interpreter("assign -V VolRegime2 TVRegime"); // Assign it to the same type
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: VolRegime should exist.");
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime2"), "Prerequisite: VolRegime2 should exist.");
    //      assertTrue(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime"), "Prerequisite: TVRegime assigned to VolRegime.");
    //      assertTrue(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime2"), "Prerequisite: TVRegime assigned to VolRegime2.");

    //      // Act
    //      controller.interpreter("remove volontario VolRegime"); // Remove the first volunteer

    //      // Assert
    //      assertNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Volunteer VolRegime should be removed.");
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime2"), "Volunteer VolRegime2 should still exist.");
    //      assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Type TVRegime should still exist.");
    //      assertFalse(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime"), "Type TVRegime should no longer list VolRegime as assigned (cascade).");
    //      assertTrue(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime2"), "Type TVRegime should still list VolRegime2 as assigned.");
    //  }

    //   @Test
    //   // Test name seems misleading based on UC26. Renaming slightly.
    //   public void testRegimeRemoveVolontarioUnassignsType() {
    //       // Arrange (PlaceRegime, TVRegime, VolRegime linked in setup helper)
    //       assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Volunteer should exist.");
    //       assertTrue(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime"), "Prerequisite: Type should be assigned.");

    //       // Act
    //       controller.interpreter("remove volontario VolRegime");

    //       // Assert
    //       assertNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Volunteer should be removed.");
    //       assertNotNull(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime"), "Type should NOT be removed.");
    //       assertNotNull(controller.db.dbLuoghiHelper.findLuogo("PlaceRegime"), "Place should NOT be removed.");
    //       assertFalse(controller.db.dbTipoVisiteHelper.getTipiVisitaByUID("TVRegime").getVolontariUIDs().contains("VolRegime"), "Type assignment should be removed (cascade).");
    //   }

    //  @Test
    //  public void testRegimeRemoveVolontarioFailNonExistent() {
    //      // Arrange
    //      assertNull(controller.db.dbVolontarioHelper.getPersona("NonExistentVol"), "Prerequisite: Volunteer should not exist.");
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Prerequisite: Other volunteer should exist."); // Ensure DB isn't empty

    //      // Act
    //      controller.interpreter("remove volontario NonExistentVol"); // Should fail

    //      // Assert
    //      assertNotNull(controller.db.dbVolontarioHelper.getPersona("VolRegime"), "Existing volunteer should not be affected.");
    //      // TODO: Check log output for error.
    //  }

    //  // TODO: UC27 - Implement tests for Gestione Ciclo Mensile (Meta UC)

}
