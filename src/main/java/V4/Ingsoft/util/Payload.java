package V4.Ingsoft.util;

public class Payload {
    //Implement in future version
    
    private enum Status {
        OK,
        ERROR
    }
    
    private Status status;
    private Object data;
    
    public Payload(Status status, Object data) {
        this.status = status;
        this.data = data;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public Object getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return "Payload{" + "status=" + status + ", data=" + data + '}';
    }
}
