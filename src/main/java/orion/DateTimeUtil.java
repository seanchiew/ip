package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Shared date/time formatting helpers for tasks that store/display dates and times.
 */
public final class DateTimeUtil {
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String DISPLAY_DATE_PATTERN = "MMM dd yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String NO_TIME_TOKEN = "-";

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(DISPLAY_DATE_PATTERN, LOCALE);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(TIME_PATTERN, LOCALE);

    private DateTimeUtil() {
        // Utility class, no instances.
    }

    /**
     * Formats date/time for UI display.
     *
     * @param date Non-null date to display.
     * @param time Optional time to display; if null, returns date only.
     * @return Formatted display string (e.g "Oct 15 2019" or "Oct 15 2019 18:00").
     */
    static String formatForDisplay(LocalDate date, LocalTime time) {
        assert date != null : "formatForDisplay(): date must not be null";

        String datePart = date.format(DISPLAY_DATE_FORMAT);
        return (time == null) ? datePart : datePart + " " + time.format(TIME_FORMAT);
    }

    /**
     * Formats time for storage.
     *
     * @param time Optional time; if null, returns {@value #NO_TIME_TOKEN}.
     * @return Time string in storage format (e.g., "18:00") or "-" if absent.
     */
    static String formatTimeForStorage(LocalTime time) {
        return (time == null) ? NO_TIME_TOKEN : time.format(TIME_FORMAT);
    }
}
