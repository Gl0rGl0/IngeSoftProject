package V5.Ingsoft.util;

import V5.Ingsoft.controller.commands.ListInterface;

public class Payload<T> {
    public enum Status { OK, ERROR }
    public enum Level  { ERROR, WARN, INFO, DEBUG }

    private Status status;
    private T data;
    private String logMessage;
    private Level level;
    private ListInterface command;

    private Payload(Status status, T data) {
        this.status = status;
        this.data   = data;
    }

    private Payload(Status status, T data, String logMessage, Level level) {
        this(status, data);
        this.logMessage = logMessage;
        this.level      = level;
    }

    /** Ora i factory methods sono generici.
     *  Il compilatore inferrerà il tipo T da quello che passi a data. */
    public static <T> Payload<T> error(T data, String logMsg) {
        return new Payload<>(Status.ERROR, data, logMsg, Level.ERROR);
    }

    public static <T> Payload<T> warn(T data, String logMsg) {
        return new Payload<>(Status.ERROR, data, logMsg, Level.WARN);
    }

    public static <T> Payload<T> info(T data, String logMsg) {
        return new Payload<>(Status.OK,    data, logMsg, Level.INFO);
    }

    public static <T> Payload<T> debug(T data, String logMsg) {
        return new Payload<>(Status.OK,    data, logMsg, Level.DEBUG);
    }

    public Status getStatus()     { return status; }
    public T      getData()       { return data; }
    public String getLogMessage() { return logMessage; }
    public Level  getLevel()      { return level; }

    public void   setCommand(ListInterface c) { this.command = c; }
    public ListInterface getCommand()         { return this.command; }

    @Override
    public String toString() {
        return "Payload[status=" + status + ", userMessage='" + data.toString() + "', dataType='" + data.getClass() + "']";
    }
}
