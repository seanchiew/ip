package orion;

/**
 * Represents an error caused by invalid user input or an invalid command format.
 */
public class OrionException extends Exception {
    
    /**
     * Constructs an {@code OrionException} with the specified detail message.
     *
     * @param message Detail message describing the error.
     */
    public OrionException(String message) {
        super(message);
    }
}
