package ingsoft.util;

public class PeriodoAnno {
    private Date inizioPeriodo;
    private Date finePeriodo;
    
    public PeriodoAnno(Date inizioPeriodo, Date finePeriodo){
        this.inizioPeriodo = inizioPeriodo.getNotAnno();
        this.inizioPeriodo = finePeriodo.getNotAnno();
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
