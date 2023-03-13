package alvin.slutprojekt.resources;

/**
 * An exception thrown when a game resource was requested but not found.
 * @see Resources
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
