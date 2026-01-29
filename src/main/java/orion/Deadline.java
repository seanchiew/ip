package orion;

/**
 * Represents a task that must be completed before a specified deadline.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Constructs a {@code Deadline} task with the specified description and deadline.
     *
     * @param description Description of the task.
     * @param by Deadline information provided by the user.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     * Returns a string representation including the deadline information.
     *
     * @return String representation of this deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
