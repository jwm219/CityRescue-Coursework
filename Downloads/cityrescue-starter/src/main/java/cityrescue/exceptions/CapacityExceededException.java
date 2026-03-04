package cityrescue.exceptions;

/**
 * Thrown when an operation would exceed a configured capacity/limit.
 *
 * <p>This is an unchecked exception so the published {@code CityRescue}
 * interface does not need to declare it in method signatures.</p>
 */
public class CapacityExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CapacityExceededException(String message) {
        super(message);
    }
}
