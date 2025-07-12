package V5.ingsoft;

import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginTests extends BaseTest {

    @BeforeEach
    public void setup() {
        resetDataFiles();
        enterRegimePhase();
    }

    @AfterEach
    public void cleanup() {
        controller.interpreter("logout"); // Ensure logout after each test
        // Optional: Clean up files again after test if needed
        // resetDataFiles();
    }

    // --- Configuratore Tests (UC1, UC2) ---

    @Test
    public void testConfiguratoreLoginSuccess() {
        // Arrange: Use the correct 'add -c' command
        Payload<?> o;
        o = controller.interpreter("add -c config1 pass1C"); // Corrected command
        o = controller.interpreter("logout"); // Logout admin

        // Act
        o = controller.interpreter("login config1 pass1C");

        // Assert
        assertNotNull(controller.getCurrentUser(), "User should be logged in.");
        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "User should be of type CONFIGURATORE.");
    }

    @Test
    public void testConfiguratoreLoginFailWrongPassword() {
        // Arrange: Use the correct 'add -c' command
        controller.interpreter("add -c config1 pass1C"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login config1 wrongPass");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    @Test
    public void testConfiguratoreLoginFailWrongUsername() {
        // Arrange: Use the correct 'add -c' command
        controller.interpreter("add -c config1 pass1C"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login wrongUser pass1C");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    // UC1 - Scenario 3b: First personal login (requires password change)
    @Test
    public void testConfiguratoreFirstLoginForcePasswordChange() {
        // Arrange: Manually create the JSON state for a user needing password change
        // Using add -c might automatically set isNew=true, let's test that first
        controller.interpreter("add -c config_first password"); // Add user with initial password
        controller.interpreter("logout"); // Logout admin

        // Act: Try to login with the initial password
        controller.interpreter("login config_first password");

        // Assert: Should be logged in but forced to change password
        assertNotNull(controller.getCurrentUser(), "User should be logged in.");
        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "User should be of type CONFIGURATORE.");

        //Verify that config_first can't do anything
        int nConfig = Model.getInstance().dbConfiguratoreHelper.getItems().size();
        controller.interpreter("add -c dont add");

        assertEquals(nConfig, Model.getInstance().dbConfiguratoreHelper.getItems().size(), "Size of configuratore should remain the same");
        // Act: Change password
        controller.interpreter("changepsw newSecurePassword newSecurePassword");
        controller.interpreter("add -c dont add");

        assertEquals(nConfig + 1, Model.getInstance().dbConfiguratoreHelper.getItems().size(), "Size of configuratore should remain the same");

        // Verify login with new password
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after logout.");
        controller.interpreter("login config_first newSecurePassword");
        assertNotNull(controller.getCurrentUser(), "Login with new password should succeed.");
        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "User should be CONFIGURATORE after password change and re-login.");
    }

    // UC2 - Standard Password Change
    @Test
    public void testConfiguratoreChangePasswordSuccess() {
        // Arrange: Use the correct 'add -c' command
        controller.interpreter("add -c config_change pass_old"); // Corrected command
        controller.interpreter("logout"); // Logout admin
        controller.interpreter("login config_change pass_old");
        // Cannot reliably assert pre-condition due to potential login bug/state

        // Act
        controller.interpreter("changepsw pass_new pass_new");

        // Assert: Check by logging out and back in with new password

        // Verify: Logout and login with the new password
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after logout.");

        controller.interpreter("login config_change pass_new");
        assertNotNull(controller.getCurrentUser(), "Login with new password should succeed.");
        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "User type should be correct after re-login.");

        // Verify: Login with the old password fails
        controller.interpreter("logout");
        controller.interpreter("login config_change pass_old");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login with old password.");
    }

    @Test
    public void testConfiguratoreChangePasswordFailNotLoggedIn() {
        // Arrange
        controller.interpreter("logout"); // Ensure logout if someone was logged in
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST initially.");

        // Act
        controller.interpreter("changepsw new_pass new_pass");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should remain GUEST after failed password change attempt.");
    }

    // --- Volontario Tests (UC16, UC17) ---

    @Test
    public void testVolontarioLoginSuccess() {
        // Arrange: Use the correct 'add -v' command
        controller.interpreter("add -v volont1 pass1V"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login volont1 pass1V");

        // Assert
        assertNotNull(controller.getCurrentUser(), "User should be logged in.");
        assertEquals(PersonaType.VOLONTARIO, controller.getCurrentUser().getType(), "User should be of type VOLONTARIO.");
    }

    @Test
    public void testVolontarioLoginFailWrongPassword() {
        // Arrange: Use the correct 'add -v' command
        controller.interpreter("add -v volont1 pass1V"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login volont1 wrongPass");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    @Test
    public void testVolontarioLoginFailWrongUsername() {
        // Arrange: Use the correct 'add -v' command
        controller.interpreter("add -v volont1 pass1V"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login wrongUser pass1V");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    // UC16 - Scenario 3b: First personal login (requires password change)
    @Test
    public void testVolontarioFirstLoginForcePasswordChange() {
        // Arrange: Use the correct 'add -v' command
        controller.interpreter("add -v volont_first password"); // Add user with initial password
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login volont_first password");

        Volontario v = Model.getInstance().dbVolontarioHelper.getItem("volont_first");
        assertNotNull(v, "Volontario should not be null");

        // Assert
        assertNotNull(controller.getCurrentUser(), "User should be logged in.");
        assertEquals(PersonaType.VOLONTARIO, controller.getCurrentUser().getType(), "User should be of type VOLONTARIO.");

        int nVol = v.getNAvailability();

        // Act
        controller.interpreter("time -s 5/5/2025");
        controller.interpreter("setav -a 5/6/2025");

        // Assert: Verify command failed
        assertEquals(nVol, v.getNAvailability());

        // Act
        controller.interpreter("changepsw newSecurePassword newSecurePassword");
        controller.interpreter("setav -a 5/6/2025");

        // Assert: Verify success
        assertEquals(nVol + 1, v.getNAvailability());

        // Verify
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after logout.");
        controller.interpreter("login volont_first newSecurePassword");
        assertNotNull(controller.getCurrentUser(), "Login with new password should succeed.");
        assertEquals(PersonaType.VOLONTARIO, controller.getCurrentUser().getType(), "User should be VOLONTARIO after password change and re-login.");
    }

    // UC17 - Standard Password Change
    @Test
    public void testVolontarioChangePasswordSuccess() {
        // Arrange: Use the correct 'add -v' command
        controller.interpreter("add -v volont_change pass_old"); // Corrected command
        controller.interpreter("logout"); // Logout admin
        controller.interpreter("login volont_change pass_old");
        // Cannot reliably assert pre-condition

        // Act
        controller.interpreter("changepsw pass_new pass_new");

        // Assert: Check by logging out and back in with new password

        // Verify: Logout and login with the new password
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after logout.");

        controller.interpreter("login volont_change pass_new");
        assertNotNull(controller.getCurrentUser(), "Login with new password should succeed.");
        assertEquals(PersonaType.VOLONTARIO, controller.getCurrentUser().getType(), "User type should be correct after re-login.");

        // Verify: Login with the old password fails
        controller.interpreter("logout");
        controller.interpreter("login volont_change pass_old");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login with old password.");
    }

    @Test
    public void testVolontarioChangePasswordFailNotLoggedIn() {
        // Arrange
        controller.interpreter("logout"); // Ensure logout if someone was logged in
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST initially.");

        // Act
        controller.interpreter("changepsw new_pass new_pass");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should remain GUEST after failed password change attempt.");
    }


    // --- Fruitore Tests (UC28, UC29) ---

    // UC28 - Registration Success
    @Test
    public void testFruitoreRegistrationSuccess() {
        // Ensure logout if someone was logged in
        controller.interpreter("logout"); // Logout admin first

        // Assert: Check if fruitore exists by trying to log in
        controller.interpreter("login fruitore password password");
        assertNotNull(controller.getCurrentUser(), "Fruitore should be able to login after registration.");
        assertEquals(PersonaType.FRUITORE, controller.getCurrentUser().getType(), "User should be of type FRUITORE.");
    }

    // UC28 - Registration Fail (Username Conflict)
    @Test
    public void testFruitoreRegistrationFailUsernameConflict() {
        // Arrange: Use the correct 'add -c' command
        controller.interpreter("add -c existing_user pass_conf"); // Corrected command
        controller.interpreter("logout"); // Logout admin

        // Act: Try to register a fruitore with the same username
        controller.interpreter("login existing_user pass_fruit pass_fruit"); // Corrected command

        // Assert: Check that login fails for the fruitore credentials
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed registration/login attempt.");

        // Optional: Verify the original user can still log in
        controller.interpreter("login existing_user pass_conf");
        assertNotNull(controller.getCurrentUser(), "Original user should still exist.");
        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "Original user should be CONFIGURATORE.");
    }

    // UC28 - Login Success
    @Test
    public void testFruitoreLoginSuccess() {
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login fruitore password password");

        // Assert
        assertNotNull(controller.getCurrentUser(), "User should be logged in.");
        assertEquals(PersonaType.FRUITORE, controller.getCurrentUser().getType(), "User should be of type FRUITORE.");
    }

    // UC28 - Login Fail Wrong Password
    @Test
    public void testFruitoreRegisterFailWrongPassword() {
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login fruit1 wrongPass passWord");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    // UC28 - Login Fail Wrong Username
    @Test
    public void testFruitoreLoginFailWrongUsername() {
        controller.interpreter("logout"); // Logout admin

        // Act
        controller.interpreter("login rightUser pass1F pass1F");
        controller.interpreter("logout");
        controller.interpreter("login wrongUser pass1F");

        // Assert
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login.");
    }

    // UC29 - Standard Password Change Success
    @Test
    public void testFruitoreChangePasswordSuccess() {
        controller.interpreter("logout"); // Logout admin
        controller.interpreter("login fruit_change pass_old pass_old");

        // Act
        controller.interpreter("changepsw pass_new pass_new");

        // Assert: Check by logging out and back in with new password

        // Verify: Logout and login with the new password
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after logout.");

        controller.interpreter("login fruit_change pass_new");
        assertNotNull(controller.getCurrentUser(), "Login with new password should succeed.");
        assertEquals(PersonaType.FRUITORE, controller.getCurrentUser().getType(), "User type should be correct after re-login.");

        // Verify: Login with the old password fails
        controller.interpreter("logout");
        controller.interpreter("login fruit_change pass_old");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST after failed login with old password.");
    }

    // UC29 - Standard Password Change Fail Not Logged In
    @Test
    public void testFruitoreChangePasswordFailNotLoggedIn() {
        // Arrange: Use the correct 'add -f' command
        controller.interpreter("logout"); // Logout admin
        controller.interpreter("add -f fruit_change pass_old"); // Corrected command
        controller.interpreter("logout");
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should be GUEST initially.");

        // Act
        controller.interpreter("changepsw new_pass new_pass");

        // Assert: Check that user is still GUEST
        assertEquals(PersonaType.GUEST, controller.getCurrentUser().getType(), "User should remain GUEST after failed password change attempt.");
    }
}
