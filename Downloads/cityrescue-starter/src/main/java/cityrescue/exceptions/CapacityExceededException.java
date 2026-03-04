package cityrescue.exceptions;

//exception for when station is full
public class CapacityExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CapacityExceededException(String message) {
        super(message);
    }
}
