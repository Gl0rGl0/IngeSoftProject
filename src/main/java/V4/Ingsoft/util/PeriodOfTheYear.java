package V4.Ingsoft.util;

public class PeriodOfTheYear {
    private final Date initDay;
    private final Date finishDay;

    public PeriodOfTheYear(Date initDay, Date finishDay) {
        this.initDay = initDay;
        this.finishDay = finishDay;
    }

    public PeriodOfTheYear(String in) throws Exception {
        String[] use = in.split("-");

        this.initDay = new Date(use[0]);
        this.finishDay = new Date(use[1]);
    }

    public Date getInitDay() {
        return this.initDay;
    }

    public Date getFinishDay() {
        return this.finishDay;
    }

    @Override
    public String toString() {
        return initDay + "-" + finishDay;
    }
}
