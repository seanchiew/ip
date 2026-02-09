package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Shared date/time formatting helpers for tasks that store/display dates and times.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private DateTimeUtil() {
        // Utility class; no instances.
    }

    /**
     * Formats date/time for UI display. If time is null, returns only the date.
     */
    static String formatForDisplay(LocalDate date, LocalTime time) {
        String datePart = date.format(DISPLAY_DATE_FORMAT);
        return (time == null) ? datePart : datePart + " " + time.format(DISPLAY_TIME_FORMAT);
    }

    /**
     * Formats time for storage. If time is null, returns "-".
     */
    static String formatTimeForStorage(LocalTime time) {
        return (time == null) ? "-" : time.format(STORAGE_TIME_FORMAT);
    }
}
