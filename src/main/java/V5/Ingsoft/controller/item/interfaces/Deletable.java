package V5.Ingsoft.controller.item.interfaces;

import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.controller.item.statuses.Statuses;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Deletable extends Storageble implements Informable {

    private final int monthChange = 2;
    protected Statuses si = StatusItem.ACTIVE;
    protected Date insertionDate;
    protected Date deletionDate;

    public Deletable(String uid) throws Exception {
        super(uid);
    }

    public void checkStatus(Date d) {
        switch (si) {
            case StatusItem.PENDING_ADD -> {
                if (insertionDate == null) {
                    AssertionControl.logMessage("Error while loading insertion date while adding, using today", Payload.Status.WARN, getClass().getSimpleName());
                    this.insertionDate = d;
                    return;
                }

                if (Date.monthsDifference(insertionDate, d) >= monthChange)
                    this.si = StatusItem.ACTIVE;
            }
            case StatusItem.PENDING_REMOVE -> {
                if (deletionDate == null) {
                    AssertionControl.logMessage("Error while loading insertion date while adding, using 2 month from now", Payload.Status.WARN, getClass().getSimpleName());
                    this.deletionDate = d.clone().addMonth(monthChange);
                    this.deletionDate.setDay(16);
                    return;
                }

                if (deletionDate.equals(d))
                    this.si = StatusItem.DISABLED;
            }
            default -> {
            }
        }
    }

    public void setDeletionDate(Date d) {
        this.deletionDate = d;
        si = StatusItem.PENDING_REMOVE;
    }

    public Date getdeletionDate() {
        return this.deletionDate;
    }

    public Date getUsableDate() {
        return this.insertionDate.clone().addMonth(monthChange);
    }

    public Statuses getStatus() {
        return this.si;
    }

    public void setStatus(StatusItem si) {
        this.si = si;
    }

    @JsonIgnore
    public boolean isUsable() {
        return si == StatusItem.ACTIVE;
    }
}
