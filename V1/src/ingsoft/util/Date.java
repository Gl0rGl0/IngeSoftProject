package ingsoft.util;

public class Date {
    private final int gg;
    private final int mm;
    private final int aa;

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

    public Date getNotAnno(){
        return new Date(gg,mm,-1);
    }

    @Override
    public String toString(){
        if(aa != -1)
            return String.format("%d/%d/%d", gg, mm, aa);
        return String.format("%d/%d", gg, mm);
    }
}
