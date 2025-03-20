package V1.ingsoft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.item.persone.Volontario;
import V1.ingsoft.model.Model;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.JsonStorage;
import V1.ingsoft.view.ViewSE;

//UTILIZZO TIPO
//init: Main -> Test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
//controller: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-options] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...
public class Main {

    public static void main(String[] args) {
        // JsonStorage.saveList("test/elencoluoghi", new ArrayList<>(Arrays.asList(luoghi)));
        // JsonStorage.saveList("test/elencovisite", new ArrayList<>(Arrays.asList(visite)));

        Model model = new Model();
        Controller controller = new Controller(model);
        ViewSE view = new ViewSE(controller);

        controller.dailyAction();

        // Mettere controllo in db se tutti i db.prop esistono (se non c'è già...)
        controller.skipSetupTesting = true; // !model.isNew();

        // if (controller.skipSetupTesting)
        Test(controller); // Prima di avviare il ciclo...
        // System.out.println("CONFIGURATORI:" + controller.db.getConfiguratori());
        // System.out.println("VOLONTARI:" + controller.db.getVolontari());
        // System.out.println("FRUITORI:" + controller.db.getFruitori());
        // System.out.println("LUOGHI:" + controller.db.getLuoghi());
        // System.out.println("TIPIVISITA:" + controller.db.getTipi());

        // initAvailability(controller);

        // Usi controller.intepreter per dare direttamente i comandi anche prima
        // dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'controller senza
        // passare dalla tastiera

        controller.interpreter("login config1 pass1C");
        controller.interpreter("changepsw pass1C pass1C");


        controller.db.dbTipoVisiteHelper.getTipiVisita().forEach(t -> System.out.println(t.getTitle() + " " + t.getStatus()));

        view.run();
    }

    public static void Test(Controller controller) {
        controller.interpreter("login ADMIN PASSWORD");

        initDBInterprete(controller);
        initAssign(controller);
        initAvailability(controller);

        controller.interpreter("logout");
    }

    static String[] luoghi = {
            "\"Parco delle Meraviglie\"",
            "\"Riserva Naturale Aurora\"",
            "\"Lago di Serenità\"",
            "\"Foresta Incantata\"",
            "\"Monti del Silenzio\"",
            "\"Deserto dei Sogni\"",
            "\"Vulcani d'Argento\"",
            "\"Giardino dei Fiori\"",
            "\"Grotte del Tempo\"",
            "\"Isola del Mistero\"",
            "\"Parco Grotta Cascata del Varone\"",
            "\"Pinacoteca Tosio-Martinengo di Brescia\"",
            "\"Luogo Test\""
    };
// static ArrayList<String> l = 


    static String[] visite = {
            "\"Tour della Foresta\"",
            "\"Avventura sul Fiume\"",
            "\"Esplorazione Montagna\"",
            "\"Scoperta del Bosco\"",
            "\"Giro dei Laghi\"",
            "\"Viaggio nel Deserto\"",
            "\"Percorso dei Vulcani\"",
            "\"Escursione al Crepuscolo\"",
            "\"Passeggiata degli Antichi\"",
            "\"Avventura Urbana\"",
            "\"Tour della Valle\"",
            "\"Scoperta del Lago\"",
            "\"Esplorazione dei Campi\"",
            "\"Scoperta del Sentiero\"",
            "\"Viaggio delle Meraviglie\"",
            "\"Alla scoperta della cascata\"",
            "\"Passeggiata naturalistica\"",
            "\"Alla scoperta del Moretto\"",
            "\"Visita Test\""
    };

    public static void initDBInterprete(Controller controller) {
        String addVisita = "add -t ";

        String visita1 = visite[0] + " \"Esplora i sentieri nascosti della foresta.\" 12.34:56.78 01/06/25 15/06/25 08:30 120 true 5 20 LuMa";
        String visita2 = visite[1] + " \"Un emozionante percorso lungo il fiume in piena natura.\" 23.45:67.89 05/06/25 20/06/25 09:00 90 false 6 18 MeGi";
        String visita3 = visite[2] + " \"Scopri le vette e i panorami mozzafiato.\" 34.56:78.90 10/06/25 25/06/25 07:45 150 true 8 25 VeSa";
        String visita4 = visite[3] + " \"Percorso guidato attraverso un bosco secolare.\" 45.67:89.01 12/06/25 27/06/25 10:15 110 false 4 15 DoLu";
        String visita5 = visite[4] + " \"Visita ai laghi più suggestivi della zona.\" 56.78:90.12 15/06/25 30/06/25 08:00 100 true 7 22 MaMe";
        String visita6 = visite[5] + " \"Avventura tra dune e oasi inaspettate.\" 67.89:01.23 20/06/25 05/07/25 14:00 80 false 5 12 GiVe";
        String visita7 = visite[6] + " \"Esplora le meraviglie geologiche dei vulcani.\" 78.90:12.34 25/06/25 10/07/25 09:30 130 true 6 20 SaDo";
        String visita8 = visite[7] + " \"Un viaggio suggestivo al tramonto tra paesaggi incantati.\" 89.01:23.45 28/06/25 12/07/25 18:00 70 false 5 15 LuMe";
        String visita9 = visite[8] + " \"Rivivi la storia attraverso un percorso archeologico.\" 90.12:34.56 01/07/25 16/07/25 10:00 95 true 8 30 MaGi";
        String visita10 = visite[9] + " \"Scopri l'arte e la cultura nascosta in città.\" 12.23:45.67 05/07/25 20/07/25 11:00 85 false 4 16 GiDo";
        String visita11 = visite[10] + " \"Percorso attraverso una valle rigogliosa e storica.\" 23.34:56.78 08/07/25 23/07/25 07:30 105 true 6 24 LuVe";
        String visita12 = visite[11] + " \"Una rilassante passeggiata attorno a un lago cristallino.\" 34.45:67.89 10/07/25 25/07/25 08:45 90 false 5 18 MaSa";
        String visita13 = visite[12] + " \"Un viaggio tra le vaste campagne e i loro segreti.\" 45.56:78.90 12/07/25 27/07/25 09:15 120 true 7 20 MeDo";
        String visita14 = visite[13] + " \"Cammina su un antico sentiero naturale e misterioso.\" 56.67:89.01 15/07/25 30/07/25 10:30 100 false 4 15 MeSa";
        String visita15 = visite[14] + " \"Un tour esclusivo tra le meraviglie nascoste della regione.\" 67.78:90.12 18/07/25 02/08/25 08:00 110 true 8 25 LuDo";
        String visita16 = visite[15] + " \"Un percorso guidato per scoprire le meraviglie naturali del parco.\" 10.8:39.31 15/05/25 21/12/25 9:00 90 false 10 20 SaDo";
        String visita17 = visite[16] + " \"Una camminata rilassante immersa nella natura.\" 1.1:3.3 9/06/25 29/08/25 16:30 30 true 5 15 MeVeSa";
        String visita18 = visite[17] + " \"Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.\" 45.539:10.220 15/01/25 28/09/25 14:00 120 true 8 30 LuDo";
        String visita19 = visite[18] + " \"Descrizione Test\" 12.1:33.3 27/02/25 14/09/25 14:00 90 true 10 30 LuMaMe";

        controller.interpreter(addVisita + visita1);
        controller.interpreter(addVisita + visita2);
        controller.interpreter(addVisita + visita3);
        controller.interpreter(addVisita + visita4);
        controller.interpreter(addVisita + visita5);
        controller.interpreter(addVisita + visita6);
        controller.interpreter(addVisita + visita7);
        controller.interpreter(addVisita + visita8);
        controller.interpreter(addVisita + visita9);
        controller.interpreter(addVisita + visita10);
        controller.interpreter(addVisita + visita11);
        controller.interpreter(addVisita + visita12);
        controller.interpreter(addVisita + visita13);
        controller.interpreter(addVisita + visita14);
        controller.interpreter(addVisita + visita15);
        controller.interpreter(addVisita + visita16);
        controller.interpreter(addVisita + visita17);
        controller.interpreter(addVisita + visita18);
        controller.interpreter(addVisita + visita19);

        // Uso setup perchè nella V1/V2 non si possono aggiungere i luoghi dopo il setup
        String addLuogo = "add -L ";

        String luogo1 = luoghi[0] + " \"Un parco straordinario con natura rigogliosa e sentieri pittoreschi.\" 45.12:10.34";
        String luogo2 = luoghi[1] + " \"Un'area protetta dove la natura si risveglia con colori unici.\" 46.23:11.45";
        String luogo3 = luoghi[2] + " \"Un lago tranquillo che incanta per la sua bellezza e calma.\" 44.56:9.87";
        String luogo4 = luoghi[3] + " \"Una fitta foresta ricca di miti e leggende antiche.\" 47.89:12.34";
        String luogo5 = luoghi[4] + " \"Montagne maestose dove la quiete e la bellezza si fondono.\" 48.01:13.45";
        String luogo6 = luoghi[5] + " \"Un paesaggio surreale tra dune e oasi nascoste.\" 33.33:44.44";
        String luogo7 = luoghi[6] + " \"Un territorio affascinante dominato da antichi vulcani.\" 50.55:14.22";
        String luogo8 = luoghi[7] + " \"Un'oasi di colori con fiori esotici e profumati.\" 42.67:8.90";
        String luogo9 = luoghi[8] + " \"Caverne misteriose che custodiscono segreti millenari.\" 41.78:7.65";
        String luogo10 = luoghi[9] + " \"Un'isola isolata avvolta nel mistero e nella leggenda.\" 39.90:5.43";
        String luogoT1 = luoghi[10] + " \"Un incantevole parco naturale con una spettacolare cascata sotterranea.\" 45.885:10.821";
        String luogoT2 = luoghi[11] + " \"Sede museale ospitata nel palazzo Martinengo da Barco che espone opere pittoriche dal Trecento all'Ottocento.\" 45.539:10.220";
        String luogoTT = luoghi[12] + " \"Descrizione luogo test\" 12.34:56.78";

        controller.interpreterSETUP(addLuogo + luogo1);
        controller.interpreterSETUP(addLuogo + luogo2);
        controller.interpreterSETUP(addLuogo + luogo3);
        controller.interpreterSETUP(addLuogo + luogo4);
        controller.interpreterSETUP(addLuogo + luogo5);
        controller.interpreterSETUP(addLuogo + luogo6);
        controller.interpreterSETUP(addLuogo + luogo7);
        controller.interpreterSETUP(addLuogo + luogo8);
        controller.interpreterSETUP(addLuogo + luogo9);
        controller.interpreterSETUP(addLuogo + luogo10);

        controller.interpreterSETUP(addLuogo + luogoT1);
        controller.interpreterSETUP(addLuogo + luogoT2);
        controller.interpreterSETUP(addLuogo + luogoTT);

        String addCofig = "add -c ";
        String configuratore1 = "config1 pass1C";
        String configuratore2 = "config2 pass2C";

        controller.interpreter(addCofig + configuratore1);
        controller.interpreter(addCofig + configuratore2);

        String addVolont = "add -v ";
        String volontario1 = "volont1 pass1V";
        String volontario2 = "volont2 pass2V";
        String volontario3 = "volont3 pass3V";
        String volontario4 = "volont4 pass4V";
        String volontario5 = "volont5 pass5V";

        controller.interpreter(addVolont + volontario1);
        controller.interpreter(addVolont + volontario2);
        controller.interpreter(addVolont + volontario3);
        controller.interpreter(addVolont + volontario4);
        controller.interpreter(addVolont + volontario5);

        String addFruit = "add -f ";
        String fruitore1 = "fruit1 pass1F";
        String fruitore2 = "fruit2 pass2F";
        String fruitore3 = "fruit3 pass3F";

        controller.interpreter(addFruit + fruitore1);
        controller.interpreter(addFruit + fruitore2);
        controller.interpreter(addFruit + fruitore3);
    }

    public static void initAssign(Controller controller) {

        for (int i = 0; i < 13; i++)
            controller.interpreter("assign -L " + luoghi[i] + " " + visite[i]);


        for (int i = 0; i < 6; i++)
            controller.interpreter("assign -L " + luoghi[i] + " " + visite[13 + i]);

        Random r = new Random();
        for (String visita : visite) {
            int ra = r.nextInt(1, 5);
            String assignCommand = "assign -V " + visita + " volont" + ra;
            controller.interpreter(assignCommand);
        }
    }

    public static void initAvailability(Controller controller) {
        Random r = new Random();
        for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
            for (int i = 0; i < 15; i++)
                v.setAvailability(controller.date, new Date(String.format("%d/%d/2025", r.nextInt(1, 28), 6)));
            System.out.println(v.getNumAvailability());
        }
    }
}
