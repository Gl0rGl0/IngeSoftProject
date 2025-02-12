package ingsoft;

import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Volontario;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        Test(app);

        app.interpreter("login config1 pass1");

        app.start();
    }


    public static void Test(App app){
        //System.out.println(app.getConfiguratoriListString());
        //System.out.println(app.getConfiguratoriList());

        initPersone(app);

        //System.out.println(app.db.getDBconfiguratori());
        //System.out.println(app.db.changePsw(app.db.getConfiguratoreFromDB("config1"), "pass1C"));
        //System.out.println(app.db.getDBconfiguratori());
        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        //System.out.println(app.login("config2", "pass2")); //CONFIGURATORE
        //System.out.println(app.login("config2", "123")); //ERRORE CREDENZIALI
        //System.out.println(app.login("nonesisto", "1234")); //ERRORE CREDENZIALI

        //System.out.println(app.getConfiguratoriListString());
        //System.out.println(app.db.removeConfiguratoreFromDB("config2"));
        //System.out.println(app.getConfiguratoriListString());
        //System.out.println(app.db.addConfiguratoreToDB(new Configuratore("config2", "pass2")));
        //System.out.println(app.getConfiguratoriListString());
    
        //System.out.println(app.getLuoghiList());
    }

    public static void initPersone(App app){
        System.out.println(app.db.addConfiguratore("config1", "pass1C"));
        System.out.println(app.db.addConfiguratore(new Configuratore("config2", "pass2C", "1")));

        System.out.println(app.db.addFruitore("fruit1", "pass1F"));
        System.out.println(app.db.addFruitore("fruit2", "pass2F"));
        System.out.println(app.db.addFruitore(new Fruitore("fruit3", "pass3F", "1")));

        System.out.println(app.db.addVolontario("volont1", "pass1V"));
        System.out.println(app.db.addVolontario(new Volontario("volont2", "pass2V", "1")));

        app.db.getConfiguratori().forEach(c -> System.out.println(c.firstAccess()));
    }
}