package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.StatusItem;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;

public abstract class Deletable {
    protected StatusItem si;
    protected Date insertionDate;   
    protected Date deletionDate;

    public void checkStatus(Date d){
        switch(si){
            case PENDING_ADD -> {
                if(insertionDate == null){
                    AssertionControl.logMessage("Error while loading insertion date while adding, using today", 2, getClass().getSimpleName());
                    this.insertionDate = d;
                    return;
                }

                if(Date.monthsDifference(insertionDate, d, 2))
                    this.si = StatusItem.ACTIVE;
            }
            case PENDING_REMOVE -> {
                if(deletionDate == null){
                    AssertionControl.logMessage("Error while loading insertion date while adding, using 2 month from now", 2, getClass().getSimpleName());
                    this.deletionDate = d.clone().addMonth(2);
                    this.deletionDate.setDay(16);
                    return;
                }

                if(deletionDate.equals(d))
                    this.si = StatusItem.DISABLED;
            }
            default -> {}
        }
    }

    public void setDeletionDate(Date d){
        this.deletionDate = d;
    }
    
    public Date getdeletionDate() {
        return this.deletionDate;
    }

    public StatusItem getStatus(){
        return this.si;
    }
}
