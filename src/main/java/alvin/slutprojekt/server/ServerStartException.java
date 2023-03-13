package alvin.slutprojekt.server;

/**
 * An exception thrown if the server fails to start.
 */
public class ServerStartException extends Exception {
    public ServerStartException(String message) {
        super(message);
    }

    public ServerStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
