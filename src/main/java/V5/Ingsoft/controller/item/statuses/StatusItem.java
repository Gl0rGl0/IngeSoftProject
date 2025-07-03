package V5.Ingsoft.controller.item.statuses;

public enum StatusItem implements Statuses {
    PENDING_ADD,
    ACTIVE,
    PENDING_REMOVE,
    DISABLED;

    @Override
    public String Name() {
        return name();
    }
}
