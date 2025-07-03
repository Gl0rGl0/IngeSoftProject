package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.Guest;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class LoginLogoutCommandTest extends DefaultTest{
    Payload<?> o;
    @BeforeEach
    void setupLog(){
        c.interpreter("logout");
        c.skipSetup();
    }

    @Test
    void baseTest(){
        assertTrue(c.getCurrentUser() instanceof Guest);

        successLogin();
        successLogout();
    }

    @Test
    void firstLogin(){
        successLogin();
        Configuratore con = (Configuratore) c.getCurrentUser();
        con.setNew(true);
        successLogout();

        o = c.interpreter("login ADMIN PASSWORD");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("First login for ADMIN", o.getLogMessage());
    }

    void successLogin(){
        c.interpreter("login ADMIN PASSWORD");
        assertFalse(c.getCurrentUser() instanceof Guest);
        assertTrue(c.getCurrentUser() instanceof Configuratore);
    }

    void successLogout(){
        c.interpreter("logout");
        assertTrue(c.getCurrentUser() instanceof Guest);
    }

    @Test
    void errorLogin(){
        o = c.interpreter("login");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Login error, missing credentials", o.getLogMessage());

        o = c.interpreter("login ADMIN WRONGPASSWORD");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Invalid password for user ADMIN", o.getLogMessage());

        successLogin();
        o = c.interpreter("login ADMIN PASSWORD");
        assertEquals(Status.ERROR, o.getStatus());
        //nascosto dalla schermatura dell'interprete
        assertEquals("Interpreter: permission denied for login", o.getLogMessage());
    }

    @Test
    void successRegistration(){
        o = c.interpreter("login REGISTER PASSWORD PASSWORD");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("New account registered for REGISTER", o.getLogMessage());
    }

    @Test
    void failRegistration(){
        o = c.interpreter("login REGISTER ONEPASSWORD");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Registration password missing for new user REGISTER", o.getLogMessage());

        o = c.interpreter("login REGISTER ONEPASSWORD TWOPASSWORD");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Password mismatch during registration for REGISTER", o.getLogMessage());
    }
}