package V4.Ingsoft.util;

public class Payload {
    //Implement in future version

    private Status status;
    private Object data;

    public Payload(Status status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status s) {
        this.status = s;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object o) {
        this.data = o;
    }

    @Override
    public String toString() {
        return "Payload{" + "status=" + status + ", data=" + data + '}';
    }

    public enum Status {
        OK,
        ERROR
    }
}
