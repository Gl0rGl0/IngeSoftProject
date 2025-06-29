package V5.Ingsoft.model.seeder;

import java.util.ArrayList;
import java.util.List;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.util.StringUtils;

public class PlaceSeeder implements Seedable{

    @Override
    public void seed(Controller c) {
        List<String[]> places = new ArrayList<>();

        places.add(new String[]{"Parco delle Meraviglie", "Un parco straordinario con natura rigogliosa e sentieri pittoreschi.", "45.12:10.34"});
        places.add(new String[]{"Riserva Naturale Aurora", "Un'area protetta dove la natura si risveglia con colori unici.", "46.23:11.45"});
        places.add(new String[]{"Lago di Serenita", "Un lago tranquillo che incanta per la sua bellezza e calma.", "44.56:9.87"});
        places.add(new String[]{"Foresta Incantata", "Una fitta foresta ricca di miti e leggende antiche.", "47.89:12.34"});
        places.add(new String[]{"Monti del Silenzio", "Montagne maestose dove la quiete e la bellezza si fondono.", "48.01:13.45"});
        places.add(new String[]{"Deserto dei Sogni", "Un paesaggio surreale tra dune e oasi nascoste.", "33.33:44.44"});
        places.add(new String[]{"Vulcani d'Argento", "Un territorio affascinante dominato da antichi vulcani.", "50.55:14.22"});
        places.add(new String[]{"Giardino dei Fiori", "Un'oasi di colori con fiori esotici e profumati.", "42.67:8.90"});
        places.add(new String[]{"Grotte del Tempo", "Caverne misteriose che custodiscono segreti millenari.", "41.78:7.65"});
        places.add(new String[]{"Isola del Mistero", "Un'isola isolata avvolta nel mistero e nella leggenda.", "39.90:5.43"});
        places.add(new String[]{"Parco Grotta Cascata del Varone", "Un incantevole parco naturale con una spettacolare cascata sotterranea.", "45.885:10.821"});
        places.add(new String[]{"Pinacoteca Tosio-Martinengo di Brescia", "Sede museale ospitata nel palazzo Martinengo da Barco che espone opere pittoriche dal Trecento all'Ottocento.", "45.539:10.220"});
        places.add(new String[]{"Luogo Test", "Descrizione luogo test", "12.34:56.78"});
    
        places.forEach(v -> c.interpreter("add -L " + StringUtils.arrayToStringClean(v)));
    }
    
}
