package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.model.helper.DBDatesHelper;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class PrecludeCommand extends AbstractCommand {

    public PrecludeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.PRECLUDE;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (options == null || options.length < 1 || args == null || args.length < 1) {
            return Payload.error(
                    "Error using 'preclude'. Missing option or arguments.",
                    "Options or args missing in PRECLUDE command.");
        }

        char option = options[0].charAt(0);
        return switch (option) {
            case 'a' -> addPrecludedDate(args[0]);
            case 'r' -> removePrecludedDate(args[0]);
            case 'b' -> bulkEdit(args);
            default  -> Payload.error(
                    "Option not recognized for 'preclude'.",
                    "Received invalid option: " + option);
        };
    }
    
    private Payload<?> checkDate(String date){
        Date d;
        try {
            d = new Date(date);
        } catch (Exception e) {
            return Payload.error(
                "Invalid date format.",
                "Failed to parse date in PRECLUDE command: " + date);
        }
            
        if (Date.monthsDifference(controller.date, d) > 2) {
            return Payload.error(
                "Date too distant. Allowed only current month or next two.",
                "Precluded date too distant: " + d);
        }

        return Payload.info(d, "success");
    }

    private Payload<String> bulkEdit(String[] args) {
        DBDatesHelper db = Model.getInstance().dbDatesHelper;
        db.clear();
        
        Date d;
        Date checkDate = controller.date;
        int success = 0;

        for (String string : args) {
            try {
                d = new Date(string);
            } catch (Exception e) { continue; }
                
            if (Date.monthsDifference(checkDate, d) > 2) {
                continue;
            }

            if (db.addItem(d)) {
                success++;
            }
        }

        String out;

        if(success == 0){
            out = String.format("Couldn't add any precluded date(s)");
            return Payload.warn(out, out);
        }
        
        out = String.format("Successfully added %s precluded date(s)", success); 
        return Payload.info(out, out);
    }

    private Payload<?> addPrecludedDate(String date) {
        Payload<?> o = checkDate(date);
        if(o.getStatus() != Status.INFO)
            return o;
        
        Date d = (Date) o.getData();

        if (Model.getInstance().dbDatesHelper.addItem(d)) {
            return Payload.info(
                    "Successfully added precluded date.",
                    "Added precluded date: " + d);
        } else {
            return Payload.warn(
                    "Failed to add precluded date.",
                    "Error adding precluded date: " + d);
        }
    }

    private Payload<?> removePrecludedDate(String date) {
        Payload<?> o = checkDate(date);
        if(o.getStatus() != Status.INFO)
            return o;
        
        Date d = (Date) o.getData();

        Model.getInstance().dbDatesHelper.removeItem(d);
        return Payload.info(
                "Successfully removed precluded date.",
                "Removed precluded date: " + d);
    }
}
