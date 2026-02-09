package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a task that must be completed before a specific date/time.
 */
public class Deadline extends Task {
    private final LocalDate byDate;
    private final LocalTime byTime; // null if time not provided

    /**
     * Constructs a {@code Deadline} with the specified description and due date/time.
     *
     * @param description Description of the deadline task.
     * @param byDate Due date of the deadline task.
     * @param byTime Due time of the deadline task, or {@code null} if not provided.
     */
    public Deadline(String description, LocalDate byDate, LocalTime byTime) {
        super(description);
        assert byDate != null : "Deadline byDate must not be null";
        this.byDate = byDate;
        this.byTime = byTime;
    }

    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        Deadline d = (Deadline) other;
        return byDate.equals(d.byDate) && Objects.equals(byTime, d.byTime);
    }

    @Override
    public String toDataString() {
        String timePart = DateTimeUtil.formatTimeForStorage(byTime);
        return "D | " + getDoneFlag() + " | " + getDescription() + " | " + byDate + " | " + timePart;
    }

    /**
     * {@inheritDoc}
     * Returns a string representation including the deadline information.
     *
     * @return String representation of this deadline task.
     */
    @Override
    public String toString() {
        String byDisplay = DateTimeUtil.formatForDisplay(byDate, byTime);
        return "[D]" + super.toString() + " (by: " + byDisplay + ")";
    }
}
