package V5.Ingsoft.controller.item.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;

import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

public abstract class Deletable implements Informable{
    protected StatusItem si = StatusItem.ACTIVE;
    protected Date insertionDate;
    protected Date deletionDate;

    public void checkStatus(Date d) {
        switch (si) {
            case PENDING_ADD -> {
                if (insertionDate == null) {
                    AssertionControl.logMessage("Error while loading insertion date while adding, using today", Payload.Status.WARN, getClass().getSimpleName());
                    this.insertionDate = d;
                    return;
                }

                if (Date.monthsDifference(insertionDate, d) >= 2)
                    this.si = StatusItem.ACTIVE;   
            }
            case PENDING_REMOVE -> {
                if (deletionDate == null) {
                    AssertionControl.logMessage("Error while loading insertion date while adding, using 2 month from now", Payload.Status.WARN, getClass().getSimpleName());
                    this.deletionDate = d.clone().addMonth(2);
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

    public StatusItem getStatus() {
        return this.si;
    }

    @JsonIgnore
    public boolean isUsable(){
        return si == StatusItem.ACTIVE;
    }

    public void setStatus(StatusItem si){
        this.si = si;
    }
}
