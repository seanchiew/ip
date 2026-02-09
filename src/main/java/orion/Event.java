package orion;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
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
        String fromTimePart = DateTimeUtil.formatTimeForStorage(fromTime);
        String toTimePart = DateTimeUtil.formatTimeForStorage(toTime);

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
        String fromDisplay = DateTimeUtil.formatForDisplay(fromDate, fromTime);
        String toDisplay = DateTimeUtil.formatForDisplay(toDate, toTime);
        return "[E]" + super.toString() + " (from: " + fromDisplay + " to: " + toDisplay + ")";
    }
}
