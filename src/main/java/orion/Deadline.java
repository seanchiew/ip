package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specific date/time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

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
        this.byDate = byDate;
        this.byTime = byTime;
    }

    @Override
    public String toDataString() {
        String timePart = (byTime == null) ? "-" : byTime.format(STORAGE_TIME_FORMAT);
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
        return "[D]" + super.toString() + " (by: " + formatForDisplay(byDate, byTime) + ")";
    }

    private static String formatForDisplay(LocalDate date, LocalTime time) {
        String datePart = date.format(DISPLAY_DATE_FORMAT);
        if (time == null) {
            return datePart;
        }
        return datePart + " " + time.format(DISPLAY_TIME_FORMAT);
    }
}
