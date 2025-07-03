package V5.Ingsoft.model.seeder;

import java.util.Random;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.util.Date;

public class DateSeeder implements Seedable{
    public final int PRECLUDED_Q = 4;

    @Override
    public void seed(Controller c) {
        Date d = c.date.clone().addMonth(2);

        int monthSize = d.getMonth().maxLength();

        int monthValue = d.getMonth().getValue();
        int yearValue = d.getYear();

        Random r = new Random();

        for(int i = 0; i < PRECLUDED_Q; i++){
            c.interpreter(String.format("preclude -a %s/%s/%s",
                          r.nextInt(1, monthSize), monthValue, yearValue));
        }
    }
    
}
