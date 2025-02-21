package ingsoft.util;

public class PeriodoAnno {
    private final Date inizioPeriodo;
    private final Date finePeriodo;
    
    public PeriodoAnno(Date inizioPeriodo, Date finePeriodo){
        this.inizioPeriodo = inizioPeriodo;
        this.finePeriodo = finePeriodo;
    }

    public PeriodoAnno(String in){
        String[] use = in.split("-");

        this.inizioPeriodo = new Date(use[0]);
        this.finePeriodo = new Date(use[1]);
    }

    public Date inizio(){
        return this.inizioPeriodo;
    }
    
    public Date fine(){
        return this.finePeriodo;
    }

    @Override
    public String toString(){
        return inizioPeriodo + "-" + finePeriodo;
    }
}
