package V5.Ingsoft.model.seeder;

import java.time.Month;
import java.util.Random;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;

public class VolunteerAvailabilitySeeder implements Seedable{
    public static final int GGDISP = 15;

    @Override
    public void seed(Controller c) {
        Date controllerDate = c.date;
        Date d = controllerDate.clone().addMonth(1);
        
        if (d.getDay() > 16) d.addMonth(1);

        Month m = d.getMonth();

        int lenghtMonth = m.maxLength();
        int monthValue = m.getValue();
        int year = d.getYear();

        if(m.equals(Month.JANUARY)) year++;

        Random r = new Random();

        Date toSet;
        for (Volontario v : Model.getInstance().dbVolontarioHelper.getItems()) {
            for (int i = 0; i < GGDISP; i++)
                try {
                    toSet = new Date(r.nextInt(1, lenghtMonth), monthValue, year);
                    v.setAvailability(controllerDate, toSet, true);
                } catch (Exception ignored) { }
        }
    }
    
}
