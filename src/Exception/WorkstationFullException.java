package src.Exception;

public class WorkstationFullException extends RuntimeException {
    public WorkstationFullException() {
        super("Workstation is full and cannot accept more items");
    }

    public WorkstationFullException(String message) {
        super(message);
    }
}
