package V4.Ingsoft.util;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.item.persone.Volontario;

public class Initer {
    private static final int NCON = 3;
    private static final int NVOL = 5;

    public static Random r = new Random();

    public static void initPersone(Controller c){
        for (int i = 1; i <= NCON; i++)
            c.interpreter(String.format("add -c config%d pass%dC", i, i));

        for (int i = 1; i <= NVOL; i++)
            c.interpreter(String.format("add -v volont%d pass%dV", i, i));
    }

    public static void initVisiteLuoghi(Controller c){
        List<String[]> visite = new ArrayList<>();
        List<String[]> luoghi = new ArrayList<>();

        visite.add(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/25", "15/06/25", "08:30", "120", "true", "5", "20", "LuMa"});
        visite.add(new String[]{"Avventura sul Fiume", "Un emozionante percorso lungo il fiume in piena natura.", "23.45:67.89", "05/06/25", "20/06/25", "09:00", "90", "false", "6", "18", "MeGi"});
        visite.add(new String[]{"Esplorazione Montagna", "Scopri le vette e i panorami mozzafiato.", "34.56:78.90", "10/06/25", "25/06/25", "07:45", "150", "true", "8", "25", "VeSa"});
        visite.add(new String[]{"Scoperta del Bosco", "Percorso guidato attraverso un bosco secolare.", "45.67:89.01", "12/06/25", "27/06/25", "10:15", "110", "false", "4", "15", "DoLu"});
        visite.add(new String[]{"Giro dei Laghi", "Visita ai laghi più suggestivi della zona.", "56.78:90.12", "15/06/25", "30/06/25", "08:00", "100", "true", "7", "22", "MaMe"});
        visite.add(new String[]{"Viaggio nel Deserto", "Avventura tra dune e oasi inaspettate.", "67.89:01.23", "20/06/25", "05/07/25", "14:00", "80", "false", "5", "12", "GiVe"});
        visite.add(new String[]{"Percorso dei Vulcani", "Esplora le meraviglie geologiche dei vulcani.", "78.90:12.34", "25/06/25", "10/07/25", "09:30", "130", "true", "6", "20", "SaDo"});
        visite.add(new String[]{"Escursione al Crepuscolo", "Un viaggio suggestivo al tramonto tra paesaggi incantati.", "89.01:23.45", "28/06/25", "12/07/25", "18:00", "70", "false", "5", "15", "LuMe"});
        visite.add(new String[]{"Passeggiata degli Antichi", "Rivivi la storia attraverso un percorso archeologico.", "90.12:34.56", "01/07/25", "16/07/25", "10:00", "95", "true", "8", "30", "MaGi"});
        visite.add(new String[]{"Avventura Urbana", "Scopri l'arte e la cultura nascosta in città.", "12.23:45.67", "05/07/25", "20/07/25", "11:00", "85", "false", "4", "16", "GiDo"});
        visite.add(new String[]{"Tour della Valle", "Percorso attraverso una valle rigogliosa e storica.", "23.34:56.78", "08/07/25", "23/07/25", "07:30", "105", "true", "6", "24", "LuVe"});
        visite.add(new String[]{"Scoperta del Lago", "Una rilassante passeggiata attorno a un lago cristallino.", "34.45:67.89", "10/07/25", "25/07/25", "08:45", "90", "false", "5", "18", "MaSa"});
        visite.add(new String[]{"Esplorazione dei Campi", "Un viaggio tra le vaste campagne e i loro segreti.", "45.56:78.90", "12/07/25", "27/07/25", "09:15", "120", "true", "7", "20", "MeDo"});
        visite.add(new String[]{"Scoperta del Sentiero", "Cammina su un antico sentiero naturale e misterioso.", "56.67:89.01", "15/07/25", "30/07/25", "10:30", "100", "false", "4", "15", "MeSa"});
        visite.add(new String[]{"Viaggio delle Meraviglie", "Un tour esclusivo tra le meraviglie nascoste della regione.", "67.78:90.12", "18/07/25", "02/08/25", "08:00", "110", "true", "8", "25", "LuDo"});
        visite.add(new String[]{"Alla scoperta della cascata", "Un percorso guidato per scoprire le meraviglie naturali del parco.", "10.8:39.31", "15/05/25", "21/12/25", "9:00", "90", "false", "10", "20", "SaDo"});
        visite.add(new String[]{"Passeggiata naturalistica", "Una camminata rilassante immersa nella natura.", "1.1:3.3", "9/06/25", "29/08/25", "16:30", "30", "true", "5", "15", "MeVeSa"});
        visite.add(new String[]{"Alla scoperta del Moretto", "Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.", "45.539:10.220", "15/01/25", "28/09/25", "14:00", "120", "true", "8", "30", "LuDo"});
        visite.add(new String[]{"Visita Test", "Descrizione Test", "12.1:33.3", "27/02/25", "14/09/25", "14:00", "90", "true", "10", "30", "LuMaMe"});

    
        luoghi.add(new String[]{"Parco delle Meraviglie", "Un parco straordinario con natura rigogliosa e sentieri pittoreschi.", "45.12:10.34"});
        luoghi.add(new String[]{"Riserva Naturale Aurora", "Un'area protetta dove la natura si risveglia con colori unici.", "46.23:11.45"});
        luoghi.add(new String[]{"Lago di Serenita", "Un lago tranquillo che incanta per la sua bellezza e calma.", "44.56:9.87"});
        luoghi.add(new String[]{"Foresta Incantata", "Una fitta foresta ricca di miti e leggende antiche.", "47.89:12.34"});
        luoghi.add(new String[]{"Monti del Silenzio", "Montagne maestose dove la quiete e la bellezza si fondono.", "48.01:13.45"});
        luoghi.add(new String[]{"Deserto dei Sogni", "Un paesaggio surreale tra dune e oasi nascoste.", "33.33:44.44"});
        luoghi.add(new String[]{"Vulcani d'Argento", "Un territorio affascinante dominato da antichi vulcani.", "50.55:14.22"});
        luoghi.add(new String[]{"Giardino dei Fiori", "Un'oasi di colori con fiori esotici e profumati.", "42.67:8.90"});
        luoghi.add(new String[]{"Grotte del Tempo", "Caverne misteriose che custodiscono segreti millenari.", "41.78:7.65"});
        luoghi.add(new String[]{"Isola del Mistero", "Un'isola isolata avvolta nel mistero e nella leggenda.", "39.90:5.43"});
        luoghi.add(new String[]{"Parco Grotta Cascata del Varone", "Un incantevole parco naturale con una spettacolare cascata sotterranea.", "45.885:10.821"});
        luoghi.add(new String[]{"Pinacoteca Tosio-Martinengo di Brescia", "Sede museale ospitata nel palazzo Martinengo da Barco che espone opere pittoriche dal Trecento all'Ottocento.", "45.539:10.220"});
        luoghi.add(new String[]{"Luogo Test", "Descrizione luogo test", "12.34:56.78"});

        visite.forEach(v -> c.interpreter("add -t " + StringUtils.arrayToStringClean(v)));
        luoghi.forEach(v -> c.interpreter("add -L " + StringUtils.arrayToStringClean(v)));

        for (int i = 0; i < 13; i++)
            c.interpreter("assign -L \"" + luoghi.get(i)[0] + "\" \"" + visite.get(i)[0] + "\"");

        for (int i = 0; i < 6; i++)
            c.interpreter("assign -L \"" + luoghi.get(i)[0] + "\" \"" + visite.get(i + 13)[0] + "\"");

        for (String[] visita : visite) {
            int ra = r.nextInt(1, 5);
            String assignCommand = "assign -V \"" + visita[0] + "\" volont" + ra;
            c.interpreter(assignCommand);
        }
    }

    public static void initAvailability(Controller c) {
        Month meseNum = c.date.getMonth().plus(1);

        if (c.date.getDay() > 16)
            meseNum.plus(1);

        for (Volontario v : c.db.dbVolontarioHelper.getPersonList()) {
            for (int i = 0; i < 15; i++)
                try {
                    v.setAvailability(c.date, new Date(String.format("%d/%d/2025", 
                                                r.nextInt(1, meseNum.maxLength()), 
                                                meseNum.getValue())), true);
                } catch (Exception e) {
                   continue;
                }
        }
    }

    public static void setupPhase(Controller controller) {
        controller.interpreter("setmax 5"); // Correct setup command
        controller.interpreter("add -L Universita \"Descrizione universitaria\" \"Via Branze 38\""); // Correct setup command
        controller.interpreter("done"); // Correct setup command
    }
}
