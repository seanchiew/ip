package orion;

/**
 * Represents a to-do task.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     * Returns a string representation prefixed with {@code [T]}.
     *
     * @return String representation of this todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
