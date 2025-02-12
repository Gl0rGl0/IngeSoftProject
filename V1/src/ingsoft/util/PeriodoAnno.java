package ingsoft.util;

public class PeriodoAnno {
    private Date inizioPeriodo;
    private Date finePeriodo;
    
    public PeriodoAnno(Date inizioPeriodo, Date finePeriodo){
        this.inizioPeriodo = new Date(inizioPeriodo.gg, inizioPeriodo.mm, -1);
        this.inizioPeriodo = new Date(finePeriodo.gg, finePeriodo.mm, -1);
    }

    public Date inizio(){
        return this.inizioPeriodo;
    }
    
    public Date fine(){
        return this.finePeriodo;
    }
}
