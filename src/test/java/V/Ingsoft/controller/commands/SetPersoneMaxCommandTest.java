package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class SetPersoneMaxCommandTest extends DefaultTest{
    Payload<?> o;

    @BeforeEach
    void setupTest(){
        c.interpreter("login ADMIN PASSWORD");
    }

    @Test
    void successSet(){
        o = c.interpreter("setmax 3");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals(3, Model.getInstance().appSettings.getMaxPrenotazioniPerPersona());
    }

    @Test
    void failSet(){
        Model m = Model.getInstance();
        int base = m.appSettings.getMaxPrenotazioniPerPersona();

        o = c.interpreter("setmax -9");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Missing argument for SetPersoneMaxCommand", o.getLogMessage());
    
        o = c.interpreter("setmax 0");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Attempted to set max < 1", o.getLogMessage());
        
        o = c.interpreter("setmax a");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("NumberFormatException", o.getLogMessage().split(" ")[0]);
        
        
        assertEquals(base, m.appSettings.getMaxPrenotazioniPerPersona());
    }
}
