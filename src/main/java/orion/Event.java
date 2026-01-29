package orion;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Constructs an {@code Event} task with the specified description and time period.
     *
     * @param description Description of the task.
     * @param from Start time information provided by the user.
     * @param to End time information provided by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     * Returns a string representation including the event time period.
     *
     * @return String representation of this event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
