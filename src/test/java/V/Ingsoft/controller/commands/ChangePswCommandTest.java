package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class ChangePswCommandTest extends DefaultTest{
    private Payload<?> o;

    @BeforeEach
    void setupConfiguratoreAndEntities() {
        c.skipSetup();
        c.interpreter("login ADMIN PASSWORD");
        c.interpreter("time -m 1");
    }

    @Test
    void successChange(){
        Persona p = Model.getInstance().dbConfiguratoreHelper.getItem("ADMIN");
        String currentPsw = p.getPsw();

        o = c.interpreter("changepsw NUOVAPSW NUOVAPSW");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Password changed for user: ADMIN", o.getLogMessage());

        String nuovaPsw = p.getPsw();
        assertNotEquals(currentPsw, nuovaPsw);
    }

    @Test
    void errorChange(){
        Persona p = Model.getInstance().dbConfiguratoreHelper.getItem("ADMIN");
        String currentPsw = p.getPsw();

        o = c.interpreter("changepsw");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing password argument in ChangePswCommand", o.getLogMessage());

        String nuovaPsw = p.getPsw();
        assertEquals(currentPsw, nuovaPsw);
    }
}
