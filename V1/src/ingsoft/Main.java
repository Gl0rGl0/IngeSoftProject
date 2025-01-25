package ingsoft;

public class Main {
    public static void main(String[] args) {
        // Ottieni la lista di configuratori
        App app = new App();

        System.out.println(app.getConfiguratoriListString());
        System.out.println(app.getConfiguratoriList());

        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        //System.out.println(app.login("config2", "pass2")); //CONFIGURATORE
        System.out.println(app.login("config3", "pass3")); //CONFIGURATORE
        System.out.println(app.login("config3", "123")); //ERRORE CREDENZIALI
        System.out.println(app.login("nonesisto", "1234")); //ERRORE CREDENZIALI

        //System.out.println(app.getLuoghiList());
    } 
}