package V5.Ingsoft.model.seeder;

import java.util.List;
import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;

public class AssignSeeder implements Seedable{

    @Override
    public void seed(Controller c) {
        Model m = Model.getInstance();

        List<Luogo> places = m.dbLuoghiHelper.getItems();
        int dimP = places.size();
        
        List<TipoVisita> visitTypes = m.dbTipoVisiteHelper.getItems();
        int dimV = visitTypes.size();

        List<Volontario> volunt = m.dbVolontarioHelper.getItems();
        int dimVol = volunt.size();

        String prompt;

        for (int i = 0; i < dimP; i++){
            prompt = "assign -L \"" + places.get(i).getName() + "\" \"" + visitTypes.get(i).getTitle() + "\"";
            c.interpreter(prompt);
        }

        for (int i = 0; i < (dimV - dimP); i++){
            prompt = "assign -L \"" + places.get(i).getName() + "\" \"" + visitTypes.get(i + dimP).getTitle() + "\"";
            c.interpreter(prompt);
        }

        int i = 0;
        for (TipoVisita vt : visitTypes) {
            prompt = "assign -V \"" + vt.getTitle() + "\" " + volunt.get((i++)%dimVol).getUsername();
            c.interpreter(prompt);
        }
    }
    
}
