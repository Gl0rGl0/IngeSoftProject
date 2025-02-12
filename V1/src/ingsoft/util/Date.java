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

    public Date(String d){
        String[] sub = d.split("/");
        this.gg = Integer.parseInt(sub[0]);
        this.mm = Integer.parseInt(sub[1]);
        this.aa = Integer.parseInt(sub[2]);
    }

    @Override
    public String toString(){
        return String.format("%d/%d/%d", gg, mm, aa);
    }
}
