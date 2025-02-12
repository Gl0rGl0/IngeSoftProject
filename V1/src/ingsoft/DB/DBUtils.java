package ingsoft.DB;

import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.persone.Volontario;
import java.util.ArrayList;

public class DBUtils {
    private final DBConfiguratoreHelper dbConfiguratoreHelper;
    private final DBFruitoreHelper dbFruitoreHelper;
    private final DBVolontarioHelper dbVolontarioHelper;
    private final DBVisiteHelper dbVisiteHelper;
    private final DBLuoghiHelper dbLuoghiHelper;

    public DBUtils() {
        dbConfiguratoreHelper = new DBConfiguratoreHelper();
        dbFruitoreHelper = new DBFruitoreHelper();
        dbVolontarioHelper = new DBVolontarioHelper();
        dbVisiteHelper = new DBVisiteHelper();
        dbLuoghiHelper = new DBLuoghiHelper(dbVisiteHelper);
    }

    public ArrayList<Configuratore> getConfiguratori() {
        return dbConfiguratoreHelper.getPersonList();
    }

    public ArrayList<Fruitore> getFruitori() {
        return dbFruitoreHelper.getPersonList();
    }

    public ArrayList<Volontario> getVolontari() {
        return dbVolontarioHelper.getPersonList();
    }

    public ArrayList<Luogo> getLuoghi() {
        return dbLuoghiHelper.getLuoghi();
    }

    public ArrayList<Visita> getVisite() {
        return dbVisiteHelper.getVisite();
    }

    public boolean addConfiguratore(Configuratore c) {
        return dbConfiguratoreHelper.addPersona(c);
    }

    public boolean addFruitore(Fruitore f) {
        return dbFruitoreHelper.addPersona(f);
    }

    public boolean addVolontario(Volontario v) {
        return dbVolontarioHelper.addPersona(v);
    }

    public boolean addConfiguratore(String user, String psw) {
        return dbConfiguratoreHelper.addPersona(new Configuratore(user, psw, "1"));
    }

    public boolean addFruitore(String user, String psw) {
        return dbFruitoreHelper.addPersona(new Fruitore(user, psw, "1"));
    }

    public boolean addVolontario(String user, String psw) {
        return dbVolontarioHelper.addPersona(new Volontario(user, psw, "1"));
    }

    public boolean removeConfiguratore(String username) {
        return dbConfiguratoreHelper.removePersona(username);
    }

    public boolean removeFruitore(String username) {
        return dbFruitoreHelper.removePersona(username);
    }

    public boolean removeVolontario(String username) {
        return dbVolontarioHelper.removePersona(username);
    }

    public boolean changePassword(String username, String newPsw, PersonaType tipoPersona) {
        return switch (tipoPersona) {
            case CONFIGURATORE -> dbConfiguratoreHelper.changePassword(username, newPsw);
            case FRUITORE -> dbFruitoreHelper.changePassword(username, newPsw);
            case VOLONTARIO -> dbVolontarioHelper.changePassword(username, newPsw);
            default -> false;
        };
    }

    public Persona findUser(String username) {
        Persona out;

        out = dbConfiguratoreHelper.findPersona(username);
        if(out != null) return out;

        out = dbVolontarioHelper.findPersona(username);
        if(out != null) return out;

        out = dbFruitoreHelper.findPersona(username);
        if(out != null) return out;

        return null;
    }

    public boolean login(String user, String psw){
        //VALUTAZIONE IN CORTO CIRCUITO A PARTIRE DAL CONFIGURATORE
        return dbConfiguratoreHelper.login(user, psw) || dbVolontarioHelper.login(user, psw) || dbFruitoreHelper.login(user, psw);
    }
}