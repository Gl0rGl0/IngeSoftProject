package V5.Ingsoft.model.seeder;

import java.util.ArrayList;
import java.util.List;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.util.StringUtils;

public class TypeVisitSeeder implements Seedable{

    @Override
    public void seed(Controller c) {
        List<String[]> visitTypes = new ArrayList<>();

        visitTypes.add(new String[]{"Tour della foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/25", "15/06/25", "08:30", "120", "true", "5", "20", "LuMa"});
        visitTypes.add(new String[]{"Avventura sul fiume", "Un emozionante percorso lungo il fiume in piena natura.", "23.45:67.89", "05/06/25", "20/06/25", "09:00", "90", "false", "6", "18", "MeGi"});
        visitTypes.add(new String[]{"Esplorazione Montagna", "Scopri le vette e i panorami mozzafiato.", "34.56:78.90", "10/06/25", "25/06/25", "07:45", "150", "true", "8", "25", "VeSa"});
        visitTypes.add(new String[]{"Scoperta del Bosco", "Percorso guidato attraverso un bosco secolare.", "45.67:89.01", "12/06/25", "27/06/25", "10:15", "110", "false", "4", "15", "DoLu"});
        visitTypes.add(new String[]{"Giro dei Laghi", "Visita ai laghi più suggestivi della zona.", "56.78:90.12", "15/06/25", "30/06/25", "08:00", "100", "true", "7", "22", "MaMe"});
        visitTypes.add(new String[]{"Viaggio nel Deserto", "Avventura tra dune e oasi inaspettate.", "67.89:01.23", "20/06/25", "05/07/25", "14:00", "80", "false", "5", "12", "GiVe"});
        visitTypes.add(new String[]{"Percorso dei Vulcani", "Esplora le meraviglie geologiche dei vulcani.", "78.90:12.34", "25/06/25", "10/07/25", "09:30", "130", "true", "6", "20", "SaDo"});
        visitTypes.add(new String[]{"Escursione al Crepuscolo", "Un viaggio suggestivo al tramonto tra paesaggi incantati.", "89.01:23.45", "28/06/25", "12/07/25", "18:00", "70", "false", "5", "15", "LuMe"});
        visitTypes.add(new String[]{"Passeggiata degli Antichi", "Rivivi la storia attraverso un percorso archeologico.", "90.12:34.56", "01/07/25", "16/07/25", "10:00", "95", "true", "8", "30", "MaGi"});
        visitTypes.add(new String[]{"Avventura Urbana", "Scopri l'arte e la cultura nascosta in città.", "12.23:45.67", "05/07/25", "20/07/25", "11:00", "85", "false", "4", "16", "GiDo"});
        visitTypes.add(new String[]{"Tour della Valle", "Percorso attraverso una valle rigogliosa e storica.", "23.34:56.78", "08/07/25", "23/07/25", "07:30", "105", "true", "6", "24", "LuVe"});
        visitTypes.add(new String[]{"Scoperta del Lago", "Una rilassante passeggiata attorno a un lago cristallino.", "34.45:67.89", "10/07/25", "25/07/25", "08:45", "90", "false", "5", "18", "MaSa"});
        visitTypes.add(new String[]{"Esplorazione dei Campi", "Un viaggio tra le vaste campagne e i loro segreti.", "45.56:78.90", "12/07/25", "27/07/25", "09:15", "120", "true", "7", "20", "MeDo"});
        visitTypes.add(new String[]{"Scoperta del Sentiero", "Cammina su un antico sentiero naturale e misterioso.", "56.67:89.01", "15/07/25", "30/07/25", "10:30", "100", "false", "4", "15", "MeSa"});
        visitTypes.add(new String[]{"Viaggio delle Meraviglie", "Un tour esclusivo tra le meraviglie nascoste della regione.", "67.78:90.12", "18/07/25", "02/08/25", "08:00", "110", "true", "8", "25", "LuDo"});
        visitTypes.add(new String[]{"Alla scoperta della cascata", "Un percorso guidato per scoprire le meraviglie naturali del parco.", "10.8:39.31", "15/05/25", "21/12/25", "9:00", "90", "false", "10", "20", "SaDo"});
        visitTypes.add(new String[]{"Passeggiata naturalistica", "Una camminata rilassante immersa nella natura.", "1.1:3.3", "9/06/25", "29/08/25", "16:30", "30", "true", "5", "15", "MeVeSa"});
        visitTypes.add(new String[]{"Alla scoperta del Moretto", "Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.", "45.539:10.220", "15/01/25", "28/09/25", "14:00", "120", "true", "8", "30", "LuDo"});
        visitTypes.add(new String[]{"Visita Test", "Descrizione Test", "12.1:33.3", "27/02/25", "14/09/25", "14:00", "90", "true", "10", "30", "LuMaMe"});
    
        visitTypes.forEach(v -> c.interpreter("add -T " + StringUtils.arrayToStringClean(v)));
    }
}
