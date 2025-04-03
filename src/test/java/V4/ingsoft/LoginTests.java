// package V4.ingsoft;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.AfterEach;
// import static org.junit.jupiter.api.Assertions.*;

// import V4.Ingsoft.controller.Controller;
// import V4.Ingsoft.controller.item.persone.Configuratore;
// import V4.Ingsoft.controller.item.persone.Volontario;
// import V4.Ingsoft.controller.item.persone.Fruitore; // Import Fruitore
// import V4.Ingsoft.controller.item.persone.PersonaType;
// import V4.Ingsoft.model.Model;
// import V4.Ingsoft.util.Initer;
// import V4.Ingsoft.util.JsonStorage;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.ArrayList; // Import ArrayList
// import java.util.List; // Import List

// public class LoginTests {
//     private Controller controller;
//     private Model model;
//     private String configPath = "data/configuratori.json";
//     private String volontariPath = "data/volontari.json";
//     private String fruitoriPath = "data/fruitori.json";
//     private String visitePath = "data/visite.json";
//     private String luoghiPath = "data/luoghi.json";
//     private String tipiVisitaPath = "data/tipi_visita.json";
//     private String iscrizioniPath = "data/iscrizioni.json";
//     private String datePreclusePath = "data/date_precluse.json";
//     private String disponibilitaPath = "data/disponibilita.json";
//     private String settingsPath = "data/settings.json";

//     private void enterRegimePhase() {
//         // 1. First absolute login and password change for ADMIN
//         controller.interpreterSETUP("login ADMIN PASSWORD");
//         controller.interpreterSETUP("changepsw newAdminPass");

//         // 2. Complete Setup Steps using known setup commands
//         controller.interpreterSETUP("setambito TestAreaRegime");
//         controller.interpreterSETUP("setmax 5");
//         controller.interpreterSETUP("add -L PlaceRegime \"Regime Place\" 10.0:20.0");
//         // Cannot add types/volunteers/assignments during setup via commands
//         controller.interpreterSETUP("done"); // Finalize setup
//     }

//     // Helper to reset data files before each test
//     private void resetDataFiles() {
//         // Delete existing files to ensure clean state
//         try { Files.deleteIfExists(Paths.get(configPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(volontariPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(fruitoriPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(visitePath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(luoghiPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(tipiVisitaPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(iscrizioniPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(datePreclusePath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(disponibilitaPath)); } catch (IOException e) { /* Ignore */ }
//         try { Files.deleteIfExists(Paths.get(settingsPath)); } catch (IOException e) { /* Ignore */ }

//         // Re-initialize default data if necessary for the test context
//         model = new Model(); // Recreate model to clear in-memory state
//         controller = new Controller(model);
//         // Assuming login tests happen *after* setup is complete, so skip interactive setup
//     }

//     @BeforeEach
//     public void setup() {
//         resetDataFiles();
//         enterRegimePhase();
//     }

//      @AfterEach
//      public void cleanup() {
//          controller.interpreter("logout"); // Ensure logout after each test
//          // Optional: Clean up files again after test if needed
//          // resetDataFiles();
//      }

//     // --- Configuratore Tests (UC1, UC2) ---

//     @Test
//     public void testConfiguratoreLoginSuccess() {
//         // Arrange: Use the correct 'add -c' command
//         controller.interpreter("add -c config1 pass1C"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login config1 pass1C");

//         // Assert
//         assertNotNull(controller.user, "User should be logged in.");
//         assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "User should be of type CONFIGURATORE.");
//     }

//     @Test
//     public void testConfiguratoreLoginFailWrongPassword() {
//         // Arrange: Use the correct 'add -c' command
//         controller.interpreter("add -c config1 pass1C"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login config1 wrongPass");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     @Test
//      public void testConfiguratoreLoginFailWrongUsername() {
//          // Arrange: Use the correct 'add -c' command
//          controller.interpreter("add -c config1 pass1C"); // Corrected command
//          controller.interpreter("logout"); // Logout admin

//          // Act
//          controller.interpreter("login wrongUser pass1C");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     // UC1 - Scenario 3b: First personal login (requires password change)
//     @Test
//     public void testConfiguratoreFirstLoginForcePasswordChange() {
//         // Arrange: Manually create the JSON state for a user needing password change
//         // Using add -c might automatically set isNew=true, let's test that first
//         controller.interpreter("add -c config_first password"); // Add user with initial password
//         controller.interpreter("logout"); // Logout admin

//         // Act: Try to login with the initial password
//         controller.interpreter("login config_first password");

//         // Assert: Should be logged in but forced to change password
//         assertNotNull(controller.user, "User should be logged in.");
//         assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "User should be of type CONFIGURATORE.");
//         // TODO: Verify password change is forced (requires specific state check)

//         // Act: Try another command (should fail or prompt for password change)
//         controller.interpreter("help"); // Example command

//         // Assert: TODO: Verify command failed due to forced password change

//         // Act: Change password
//         controller.interpreter("changepsw newSecurePassword");

//         // Assert: TODO: Verify password change success

//         // Act: Try another command again (should succeed now)
//         controller.interpreter("help"); // Should work now

//         // Assert: TODO: Verify help command success

//          // Verify login with new password
//          controller.interpreter("logout");
//          assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after logout.");
//          controller.interpreter("login config_first newSecurePassword");
//          assertNotNull(controller.user, "Login with new password should succeed.");
//          assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "User should be CONFIGURATORE after password change and re-login.");
//     }

//     // UC2 - Standard Password Change
//     @Test
//     public void testConfiguratoreChangePasswordSuccess() {
//         // Arrange: Use the correct 'add -c' command
//         controller.interpreter("add -c config_change pass_old"); // Corrected command
//         controller.interpreter("logout"); // Logout admin
//         controller.interpreter("login config_change pass_old");
//         // Cannot reliably assert pre-condition due to potential login bug/state

//         // Act
//         controller.interpreter("changepsw pass_new");

//         // Assert: Check by logging out and back in with new password

//         // Verify: Logout and login with the new password
//         controller.interpreter("logout");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after logout.");

//         controller.interpreter("login config_change pass_new");
//         assertNotNull(controller.user, "Login with new password should succeed.");
//         assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "User type should be correct after re-login.");

//         // Verify: Login with the old password fails
//         controller.interpreter("logout");
//         controller.interpreter("login config_change pass_old");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login with old password.");
//     }

//      @Test
//      public void testConfiguratoreChangePasswordFailNotLoggedIn() {
//         // Arrange
//         controller.interpreter("logout"); // Ensure logout if someone was logged in
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST initially.");

//         // Act
//         controller.interpreter("changepsw new_pass");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should remain GUEST after failed password change attempt.");
//     }

//     // --- Volontario Tests (UC16, UC17) ---

//     @Test
//     public void testVolontarioLoginSuccess() {
//         // Arrange: Use the correct 'add -v' command
//         controller.interpreter("add -v volont1 pass1V"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login volont1 pass1V");

//         // Assert
//         assertNotNull(controller.user, "User should be logged in.");
//         assertEquals(PersonaType.VOLONTARIO.toString(), controller.user.getType().toString(), "User should be of type VOLONTARIO.");
//     }

//     @Test
//     public void testVolontarioLoginFailWrongPassword() {
//         // Arrange: Use the correct 'add -v' command
//         controller.interpreter("add -v volont1 pass1V"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login volont1 wrongPass");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     @Test
//      public void testVolontarioLoginFailWrongUsername() {
//          // Arrange: Use the correct 'add -v' command
//          controller.interpreter("add -v volont1 pass1V"); // Corrected command
//          controller.interpreter("logout"); // Logout admin

//          // Act
//          controller.interpreter("login wrongUser pass1V");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     // UC16 - Scenario 3b: First personal login (requires password change)
//     @Test
//     public void testVolontarioFirstLoginForcePasswordChange() {
//         // Arrange: Use the correct 'add -v' command
//         controller.interpreter("add -v volont_first password"); // Add user with initial password
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login volont_first password");

//         // Assert
//         assertNotNull(controller.user, "User should be logged in.");
//         assertEquals(PersonaType.VOLONTARIO.toString(), controller.user.getType().toString(), "User should be of type VOLONTARIO.");
//         // TODO: Verify password change is forced

//         // Act
//         controller.interpreter("help");

//         // Assert: TODO: Verify command failed

//         // Act
//         controller.interpreter("changepsw newSecurePassword");

//         // Assert: TODO: Verify success

//         // Act
//         controller.interpreter("help");

//         // Assert: TODO: Verify success

//          // Verify
//          controller.interpreter("logout");
//          assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after logout.");
//          controller.interpreter("login volont_first newSecurePassword");
//          assertNotNull(controller.user, "Login with new password should succeed.");
//          assertEquals(PersonaType.VOLONTARIO.toString(), controller.user.getType().toString(), "User should be VOLONTARIO after password change and re-login.");
//     }

//     // UC17 - Standard Password Change
//     @Test
//     public void testVolontarioChangePasswordSuccess() {
//         // Arrange: Use the correct 'add -v' command
//         controller.interpreter("add -v volont_change pass_old"); // Corrected command
//         controller.interpreter("logout"); // Logout admin
//         controller.interpreter("login volont_change pass_old");
//         // Cannot reliably assert pre-condition

//         // Act
//         controller.interpreter("changepsw pass_new");

//         // Assert: Check by logging out and back in with new password

//         // Verify: Logout and login with the new password
//         controller.interpreter("logout");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after logout.");

//         controller.interpreter("login volont_change pass_new");
//         assertNotNull(controller.user, "Login with new password should succeed.");
//         assertEquals(PersonaType.VOLONTARIO.toString(), controller.user.getType().toString(), "User type should be correct after re-login.");

//         // Verify: Login with the old password fails
//         controller.interpreter("logout");
//         controller.interpreter("login volont_change pass_old");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login with old password.");
//     }

//      @Test
//      public void testVolontarioChangePasswordFailNotLoggedIn() {
//         // Arrange
//         controller.interpreter("logout"); // Ensure logout if someone was logged in
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST initially.");

//         // Act
//         controller.interpreter("changepsw new_pass");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should remain GUEST after failed password change attempt.");
//     }


//     // --- Fruitore Tests (UC28, UC29) ---

//     // UC28 - Registration Success
//     @Test
//     public void testFruitoreRegistrationSuccess() {
//         // Act: Register a new fruitore (assuming registration doesn't require admin)
//         controller.interpreter("add -f fruit1 pass1F"); // Corrected command
        
//         // Arrange: Use the correct 'add -f' command
//         controller.interpreter("logout"); // Logout admin first


//         // Assert: Check if fruitore exists by trying to log in
//         controller.interpreter("login fruit1 pass1F");
//         assertNotNull(controller.user, "Fruitore should be able to login after registration.");
//         assertEquals(PersonaType.FRUITORE.toString(), controller.user.getType().toString(), "User should be of type FRUITORE.");
//     }

//     // UC28 - Registration Fail (Username Conflict)
//     @Test
//     public void testFruitoreRegistrationFailUsernameConflict() {
//         // Arrange: Use the correct 'add -c' command
//         controller.interpreter("add -c existing_user pass_conf"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act: Try to register a fruitore with the same username
//         controller.interpreter("add -f existing_user pass_fruit"); // Corrected command

//         // Assert: Check that login fails for the fruitore credentials
//         controller.interpreter("login existing_user pass_fruit");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed registration/login attempt.");

//         // Optional: Verify the original user can still log in
//         controller.interpreter("login existing_user pass_conf");
//         assertNotNull(controller.user, "Original user should still exist.");
//         assertEquals(PersonaType.CONFIGURATORE.toString(), controller.user.getType().toString(), "Original user should be CONFIGURATORE.");
//     }

//     // UC28 - Login Success
//     @Test
//     public void testFruitoreLoginSuccess() {
//         // Arrange: Use the correct 'add -f' command
//         controller.interpreter("add -f fruit1 pass1F"); // Corrected command
//         controller.interpreter("logout"); // Logout admin

//         // Act
//         controller.interpreter("login fruit1 pass1F");

//         // Assert
//         assertNotNull(controller.user, "User should be logged in.");
//         assertEquals(PersonaType.FRUITORE.toString(), controller.user.getType().toString(), "User should be of type FRUITORE.");
//     }

//     // UC28 - Login Fail Wrong Password
//     @Test
//     public void testFruitoreLoginFailWrongPassword() {
//         // Arrange: Use the correct 'add -f' command
//         controller.interpreter("logout"); // Logout admin
//         controller.interpreter("add -f fruit1 pass1F"); // Corrected command
//         controller.interpreter("logout");

//         // Act
//         controller.interpreter("login fruit1 wrongPass");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     // UC28 - Login Fail Wrong Username
//     @Test
//     public void testFruitoreLoginFailWrongUsername() {
//         // Arrange: Use the correct 'add -f' command
//         controller.interpreter("logout"); // Logout admin
//         controller.interpreter("add -f fruit1 pass1F"); // Corrected command
//         controller.interpreter("logout");

//         // Act
//         controller.interpreter("login wrongUser pass1F");

//         // Assert
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login.");
//     }

//     // UC29 - Standard Password Change Success
//     @Test
//     public void testFruitoreChangePasswordSuccess() {
//         // Arrange: Use the correct 'add -f' command
//         controller.interpreter("add -f fruit_change pass_old"); // Corrected command
//         controller.interpreter("logout"); // Logout admin
//         controller.interpreter("login fruit_change pass_old");
//         // Cannot reliably assert pre-condition

//         // Act
//         controller.interpreter("changepsw pass_new");

//         // Assert: Check by logging out and back in with new password

//         // Verify: Logout and login with the new password
//         controller.interpreter("logout");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after logout.");

//         System.out.println();
//         controller.interpreter("login fruit_change pass_new");
//         assertNotNull(controller.user, "Login with new password should succeed.");
//         assertEquals(PersonaType.FRUITORE.toString(), controller.user.getType().toString(), "User type should be correct after re-login.");

//         // Verify: Login with the old password fails
//         controller.interpreter("logout");
//         controller.interpreter("login fruit_change pass_old");
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST after failed login with old password.");
//     }

//     // UC29 - Standard Password Change Fail Not Logged In
//      @Test
//      public void testFruitoreChangePasswordFailNotLoggedIn() {
//          // Arrange: Use the correct 'add -f' command
//          controller.interpreter("logout"); // Logout admin
//          controller.interpreter("add -f fruit_change pass_old"); // Corrected command
//          controller.interpreter("logout");
//          assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should be GUEST initially.");

//          // Act
//          controller.interpreter("changepsw new_pass");

//         // Assert: Check that user is still GUEST
//         assertEquals(PersonaType.GUEST.toString(), controller.user.getType().toString(), "User should remain GUEST after failed password change attempt.");
//   }
// }
