package V5.Ingsoft.controller.item.interfaces;

public abstract class Storageble {
    public final String UID;//

    public Storageble(String uid) throws Exception {
        this.UID = uid;

        if (uid == null) throw new Exception("Null UID");
    }

    public String getUID() { return this.UID; }
}
