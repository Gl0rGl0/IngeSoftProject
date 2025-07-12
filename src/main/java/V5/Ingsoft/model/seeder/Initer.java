package V5.Ingsoft.model.seeder;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Initer {

    public static void setupPhase(Controller controller) {
        controller.interpreter("time -s 1/1/2025");

        controller.interpreter("login ADMIN PASSWORD");

        controller.interpreter("setmax 5");
        controller.interpreter("add -L Universita \"Descrizione universitaria\" \"Via Branze 38, 25133 Brescia BS\"");
        controller.interpreter("add -T \"Visita al modulo\" \"Esplorazione dei laboratori sotterranei\" \"Via Senatore Diogene Valotti, 9\" 1/1/2025 23/12/2025 10:30 65 false 5 20 LuMeVe");
        controller.interpreter("add -v visitaUniverstaria passVUNI");

        controller.interpreter("assign -V \"Visita al modulo\" visitaUniverstaria");
        controller.interpreter("assign -L Universita \"Visita al modulo\"");

        controller.interpreter("add -c configUNI passCUNI");
        controller.interpreter("done");

        controller.interpreter("logout");

        controller.interpreter("time -m 2");

        controller.interpreter("time -s 16/5/2025");
    }

    public static void dimostrazione(Controller controller) {
        setupPhase(controller);
        seed(controller);
        //è il 16 aprile

        controller.interpreter("login ADMIN PASSWORD");

        controller.interpreter("add -c confTest2 c2Test");

        controller.interpreter("add -v volTest2 v2Test");

        List<String[]> visite = new ArrayList<>();
        visite.add(new String[]{"Tour della foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/01/25", "15/12/25", "08:30", "120", "true", "5", "20", "LuMaMeVe"});
        visite.add(new String[]{"Avventura sul fiume", "Un emozionante percorso lungo il fiume in piena natura.", "23.45:67.89", "05/01/25", "20/12/25", "18:00", "90", "false", "6", "18", "MeGi"});
        visite.forEach(v -> controller.interpreter("add -T " + StringUtils.arrayToStringClean(v)));
    
        controller.interpreter("add -L \"foresta Incantata\" \"Una fitta foresta ricca di miti e leggende antiche.\" 47.89:12.34");
    
        controller.interpreter("assign -L \"foresta Incantata\" \"Tour della foresta\"");
        controller.interpreter("assign -L \"foresta Incantata\" \"Avventura sul fiume\"");

        controller.interpreter("assign -V \"Tour della foresta\" visitaUniverstaria");
        controller.interpreter("assign -V \"Avventura sul fiume\" visitaUniverstaria");
        controller.interpreter("assign -V \"Avventura sul fiume\" volTest2");
        
        controller.interpreter("time -m 2");

        controller.interpreter("collection -c");
        controller.interpreter("makeplan");

        controller.interpreter("logout");
    }

    private static void seed(Controller c){
        // new SettingsSeeder().seed(c);
        
        new PlaceSeeder().seed(c);
        new TypeVisitSeeder().seed(c);
        new UserSeeder().seed(c);

        new AssignSeeder().seed(c);
        new DateSeeder().seed(c);
        new VolunteerAvailabilitySeeder().seed(c);
        
        // new VisitSeeder().seed(c);
    }
}