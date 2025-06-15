package V.Ingsoft;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.model.DBConfiguratoreHelper;
import V5.Ingsoft.model.DBDatesHelper;
import V5.Ingsoft.model.DBFruitoreHelper;
import V5.Ingsoft.model.DBIscrizioniHelper;
import V5.Ingsoft.model.DBLuoghiHelper;
import V5.Ingsoft.model.DBTipoVisiteHelper;
import V5.Ingsoft.model.DBVisiteHelper;
import V5.Ingsoft.model.DBVolontarioHelper;
import V5.Ingsoft.model.Model;

public class DefaultTest {
    protected Controller c;

    @BeforeEach
    protected void setup() {
        Model m = Model.getInstance();
        m.dbConfiguratoreHelper =  new DBConfiguratoreHelper(new ArrayList<>());
        m.dbDatesHelper =          new DBDatesHelper(new ArrayList<>());
        m.dbFruitoreHelper =       new DBFruitoreHelper(new ArrayList<>());
        m.dbIscrizionisHelper =    new DBIscrizioniHelper(new ArrayList<>());
        m.dbLuoghiHelper =         new DBLuoghiHelper(new ArrayList<>());
        m.dbTipoVisiteHelper =     new DBTipoVisiteHelper(new ArrayList<>());
        m.dbVisiteHelper =         new DBVisiteHelper(new ArrayList<>());
        m.dbVolontarioHelper =     new DBVolontarioHelper(new ArrayList<>());

        c = new Controller(m);
    }
}
