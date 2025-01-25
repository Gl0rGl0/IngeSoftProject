package ingsoft;

import java.util.ArrayList;

import ingsoft.luoghi.Luogo;
import ingsoft.persone.Configuratore;
import ingsoft.util.DBUtils;
import ingsoft.util.StatusLogin;

public class App {
    DBUtils db = new DBUtils();

    public StatusLogin login(String user, String psw){
        if (db.checkConfiguratore(user, psw)) {
            return StatusLogin.CONFIGURATORE;
        }
        return StatusLogin.ERROR_CREDENTIALS;
    }

    public String getConfiguratoriListString(){
        StringBuilder out = new StringBuilder();
        for (Configuratore c : db.getDBconfiguratori()) {
            out.append(c.getUsername()).append("\n");
        }
        return out.toString();
    }

    public ArrayList<Configuratore> getConfiguratoriList(){
        ArrayList<Configuratore> out = new ArrayList<Configuratore>();
        for (Configuratore configuratore : db.getDBconfiguratori()) {
            out.add(new Configuratore(configuratore.getUsername(), "*****"));
        }
        return out;
    }

    public ArrayList<Luogo> getLuoghiList(){
        return db.getDBLuoghi();
    }
}
