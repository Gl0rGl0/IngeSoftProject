package V5.Ingsoft.util;

public class Payload {
    public enum Status { OK, ERROR }
    public enum Level  { ERROR, WARN, INFO, DEBUG }

    private Status status;
    private Object data;
    private String logMessage;
    private Level level;

    private Payload(Status status, Object data, String logMessage, Level level) {
        this.status = status;
        this.data = data;
        this.logMessage  = logMessage;
        this.level       = level;
    }

    public static Payload error(Object data, String logMsg) {
        return new Payload(Status.ERROR, data, logMsg, Level.ERROR);
    }

    public static Payload warn(Object data, String logMsg) {
        return new Payload(Status.ERROR, data, logMsg, Level.WARN);
    }

    public static Payload info(Object data, String logMsg) {
        return new Payload(Status.OK, data, logMsg, Level.INFO);
    }

    public static Payload debug(Object data, String logMsg) {
        return new Payload(Status.OK, data, logMsg, Level.DEBUG);
    }

    public Status getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Payload[status=" + status + ", userMessage='" + data.toString() + "']";
    }
}
