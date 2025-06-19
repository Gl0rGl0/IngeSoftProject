package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

import java.time.Month;
import java.util.Comparator;
import java.util.List;

public class MakePlanCommand extends AbstractCommand {

    public MakePlanCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MAKEPLAN;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (!isExecutable()) {
            return Payload.warn(
                    "Action possible only on the 16th of the month and not on holidays!",
                    "Execution not allowed by schedule");
        }
        if (controller == null) {
            return Payload.error(
                    "Internal error: controller is null.",
                    "Null controller");
        }
        if (controller.date == null || Model.getInstance() == null) {
            return Payload.error(
                    "Internal error: dependencies missing.",
                    "Date or db null");
        }
        if (controller.isVolunteerCollectionOpen()) {
            return Payload.warn(
                    "Cannot make plan while collection is open.",
                    "Collection still open");
        }

        List<TipoVisita> types = Model.getInstance().dbTipoVisiteHelper.getItems();
        types.removeIf(t -> !t.isUsable());
        types.sort(Comparator.comparingInt(t -> t.getInitTime().getMinutes()));

        Month next = controller.date.clone().getMonth().plus(1);
        int nextMonthValue = next.getValue();
        int year = controller.date.getYear();
        int daysInMonth = next.maxLength();
        int assignedCount = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            Date current;
            try {
                current = new Date(day, nextMonthValue, year);
                assignedCount += processVisitsForDate(current, types);
            } catch (Exception ignored) {
            }
        }

        return Payload.info(
                "Plan created: " + assignedCount + " visits assigned.",
                "Assigned " + assignedCount + " visits");
    }

    private int processVisitsForDate(Date date, List<TipoVisita> types) {
        int count = 0;
        for (TipoVisita tv : types) {
            if (tv == null) continue;
            if (tv.getStatus() != StatusItem.ACTIVE) continue;
            if (!tv.getDays().contains(date.dayOfTheWeek())) continue;
            if (Model.getInstance().dbDatesHelper.getPrecludedDates().contains(date)) continue;
            if (!Date.between(tv.getInitDay(), date, tv.getFinishDay())) continue;
            count += assignVisitForDate(tv, date);
        }
        return count;
    }

    private int assignVisitForDate(TipoVisita tv, Date date) {
        int count = 0;
        for (String uid : tv.getVolontariUIDs()) {
            Volontario v = Model.getInstance().dbVolontarioHelper.getPersona(uid);
            if (v == null) continue;
            if (!v.isAvailable(date.getDay())) continue;
            if (Model.getInstance().dbVisiteHelper.volontarioHaConflitto(v, date, tv)) continue;
            Visita newVisit;
            try {
                newVisit = new Visita(tv, date, uid);
                Model.getInstance().dbVisiteHelper.addVisita(newVisit);
            } catch (Exception e) {
                continue;
            }
            count++;
        }
        return count;
    }
}
