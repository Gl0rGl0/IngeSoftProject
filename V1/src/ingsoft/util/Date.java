package ingsoft.util;

public class Date {
    int gg;
    int mm;
    int aa;

    public Date(int gg, int mm, int aa){
        this.gg = gg;
        this.mm = mm;
        this.aa = aa;
    }

    @Override
    public String toString(){
        return String.format("%d/%d/%d", gg, mm, aa);
    }
}
