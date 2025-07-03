package V.Ingsoft.controller.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Configuratore;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class AddCommandTest extends DefaultTest{
    private Payload<?> o;

    @BeforeEach
    void setupConfiguratore(){
        c.skipSetup();
        c.interpreter("login ADMIN PASSWORD");
        c.interpreter("time -m 1");
    }

    @Test
    void invalidPrompt(){
        o = c.interpreter("add");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Missing or invalid option in AddCommand", o.getLogMessage());

        o = c.interpreter("add -x");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Unrecognized option in AddCommand: x", o.getLogMessage());
    }

    @Test
    void addConfiguratore(){
        c.interpreter("add -c USERNAME PROVA");
        Configuratore conf = Model.getInstance().dbConfiguratoreHelper.getItem("USERNAME");
        assertNotNull(conf);
    }

    @Test
    void addFailConfiguratore(){
        o = c.interpreter("add -c USERNAME");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for add -c", o.getLogMessage());

        c.interpreter("add -c USERNAME PASSWORD");
        o = c.interpreter("add -c USERNAME PASSWORD");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to add configurator: USERNAME", o.getLogMessage());
    }

    @Test
    void addVolontario(){
        c.interpreter("add -v USERNAME PROVA");
        Volontario vol = Model.getInstance().dbVolontarioHelper.getItem("USERNAME");
        assertNotNull(vol);
    }

    @Test
    void addFailVolontario(){
        o = c.interpreter("add -v USERNAME");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for add -v", o.getLogMessage());

        c.interpreter("add -v USERNAME PASSWORD");
        o = c.interpreter("add -v USERNAME PASSWORD");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to add volunteer: USERNAME", o.getLogMessage());
    }

    @Test
    void addLuogo(){
        Payload<?> o = c.interpreter("add -L \"Parco delle Meraviglie\" \"Un parco straordinario con natura rigogliosa e sentieri pittoreschi.\" \"45.12:10.34\"");
        Luogo luo = Model.getInstance().dbLuoghiHelper.findLuogo("Parco delle Meraviglie");
        assertNotNull(luo);
    }

    @Test
    void addFailLuogo(){
        o = c.interpreter("add -L TITLE");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for add -L", o.getLogMessage());

        c.interpreter("add -L TITLE DESCRIPTION PLACE");
        o = c.interpreter("add -L TITLE DESCRIPTION PLACE PLACE");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to add place: TITLE", o.getLogMessage());
    }

    @Test
    void addTipoVisita(){
        c.interpreter("add -T \"Tour della Foresta\" \"Esplora i sentieri nascosti della foresta.\" 12.34:56.78 01/06/2025 15/06/2025 08:30 120 true 5 20 LuMa");
        TipoVisita tv = Model.getInstance().dbTipoVisiteHelper.findTipoVisita("Tour della Foresta");
        assertNotNull(tv);
    }

    @Test
    void addFailTipoVisita(){
       o = c.interpreter("add -T \"Tour della Foresta\" \"Esplora i sentieri nascosti della foresta.\" 12.34:56.78 01/06/2025 15/06/2025 08:30 120 true 5 20");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Insufficient args for add -T", o.getLogMessage());

        c.interpreter("add -T \"Tour della Foresta\" \"Esplora i sentieri nascosti della foresta.\" 12.34:56.78 01/06/2025 15/06/2025 08:30 120 true 5 20 LuMa");
        o = c.interpreter("add -T \"Tour della Foresta\" \"Esplora i sentieri nascosti della foresta.\" 12.34:56.78 01/06/2025 15/06/2025 08:30 120 true 5 20 LuMa");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("Failed to add TipoVisita: Tour della Foresta", o.getLogMessage());
    }
}
