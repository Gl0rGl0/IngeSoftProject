package ingsoft.util;

public class PeriodoAnno {
    Date inizioPeriodo;
    Date finePeriodo;
    
    public PeriodoAnno(Date inizioPeriodo, Date finePeriodo){
        this.inizioPeriodo = new Date(inizioPeriodo.gg, inizioPeriodo.mm, -1);
        this.inizioPeriodo = new Date(finePeriodo.gg, finePeriodo.mm, -1);
    }
}
