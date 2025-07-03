package V.Ingsoft;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.model.helper.DBConfiguratoreHelper;
import V5.Ingsoft.model.helper.DBDatesHelper;
import V5.Ingsoft.model.helper.DBFruitoreHelper;
import V5.Ingsoft.model.helper.DBIscrizioniHelper;
import V5.Ingsoft.model.helper.DBLuoghiHelper;
import V5.Ingsoft.model.helper.DBTipoVisiteHelper;
import V5.Ingsoft.model.helper.DBVisiteHelper;
import V5.Ingsoft.model.helper.DBVolontarioHelper;

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
