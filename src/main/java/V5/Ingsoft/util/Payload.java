package V5.Ingsoft.util;

import V5.Ingsoft.controller.commands.ListInterface;

public class Payload<T> {
    public static enum Status  { ERROR, WARN, INFO, DEBUG }

    private Status status;
    private T data;
    private String logMessage;
    private ListInterface command;

    private Payload(Status status, T data) {
        this.status = status;
        this.data   = data;
    }

    private Payload(Status status, T data, String logMessage) {
        this(status, data);
        this.logMessage = logMessage;
    }

    /** Ora i factory methods sono generici.
     *  Il compilatore inferrerà il tipo T da quello che passi a data. */
    public static <T> Payload<T> error(T data, String logMsg) {
        return new Payload<>(Status.ERROR, data, logMsg);
    }

    public static <T> Payload<T> warn(T data, String logMsg) {
        return new Payload<>(Status.WARN, data, logMsg);
    }

    public static <T> Payload<T> info(T data, String logMsg) {
        return new Payload<>(Status.INFO, data, logMsg);
    }

    public static <T> Payload<T> debug(T data, String logMsg) {
        return new Payload<>(Status.DEBUG, data, logMsg);
    }

    public Status getStatus()     { return status; }
    public T      getData()       { return data; }
    public String getLogMessage() { return logMessage; }

    public void   setCommand(ListInterface c) { this.command = c; }
    public ListInterface getCommand()         { return this.command; }

    @Override
    public String toString() {
        return "Payload[status=" + status + ", userMessage='" + data.toString() + "', dataType='" + data.getClass() + "']";
    }
}
