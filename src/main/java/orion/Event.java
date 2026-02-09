package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private final LocalDate fromDate;
    private final LocalTime fromTime; // null if time not provided
    private final LocalDate toDate;
    private final LocalTime toTime;   // null if time not provided

    /**
     * Constructs an {@code Event} with the specified description and start/end date/time.
     *
     * @param description Description of the event task.
     * @param fromDate Start date of the event.
     * @param fromTime Start time of the event, or {@code null} if not provided.
     * @param toDate End date of the event.
     * @param toTime End time of the event, or {@code null} if not provided.
     */
    public Event(String description, LocalDate fromDate, LocalTime fromTime,
                 LocalDate toDate, LocalTime toTime) {
        super(description);
        assert fromDate != null : "Event fromDate must not be null";
        assert toDate != null : "Event toDate must not be null";
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    @Override
    public String toDataString() {
        String fromTimePart = (fromTime == null) ? "-" : fromTime.format(STORAGE_TIME_FORMAT);
        String toTimePart = (toTime == null) ? "-" : toTime.format(STORAGE_TIME_FORMAT);

        return "E | " + getDoneFlag() + " | " + getDescription()
                + " | " + fromDate + " | " + fromTimePart
                + " | " + toDate + " | " + toTimePart;
    }

    /**
     * {@inheritDoc}
     * Returns a string representation including the event time period.
     *
     * @return String representation of this event task.
     */
    @Override
    public String toString() {
        String fromDisplay = formatForDisplay(fromDate, fromTime);
        String toDisplay = formatForDisplay(toDate, toTime);
        return "[E]" + super.toString() + " (from: " + fromDisplay + " to: " + toDisplay + ")";
    }

    private static String formatForDisplay(LocalDate date, LocalTime time) {
        String datePart = date.format(DISPLAY_DATE_FORMAT);
        if (time == null) {
            return datePart;
        }
        return datePart + " " + time.format(DISPLAY_TIME_FORMAT);
    }
}
