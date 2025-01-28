package ingsoft;

import ingsoft.persone.Configuratore;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        Test(app);
        //app.start();
    }


    public static void Test(App app){
        System.out.println(app.getConfiguratoriListString());
        System.out.println(app.getConfiguratoriList());

        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        System.out.println(app.login("config2", "pass2")); //CONFIGURATORE
        System.out.println(app.login("config2", "123")); //ERRORE CREDENZIALI
        System.out.println(app.login("nonesisto", "1234")); //ERRORE CREDENZIALI

        System.out.println(app.getConfiguratoriListString());
        System.out.println(app.db.removeConfiguratoreFromDB("config2"));
        System.out.println(app.getConfiguratoriListString());
        System.out.println(app.db.addConfiguratoreToDB(new Configuratore("config2", "pass2")));
        System.out.println(app.getConfiguratoriListString());
    
        //System.out.println(app.getLuoghiList());
    }
}