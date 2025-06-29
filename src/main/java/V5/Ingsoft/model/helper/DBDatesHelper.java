package V5.Ingsoft.model.helper;

import V5.Ingsoft.util.Date;

import java.time.Month;
import java.util.ArrayList;

public class DBDatesHelper extends DBListHelper<Date> {
    public DBDatesHelper() {
        super(Date.PATH, Date.class);
    }

    public DBDatesHelper(ArrayList<Date> list) {
        super(Date.PATH, Date.class, list);
    }

    public void refreshPrecludedDate(Date date) {
        Month monthPassato = date.clone().getMonth().minus(1);

        cachedItems.removeIf(d -> d.getMonth() == monthPassato);
    }
}
