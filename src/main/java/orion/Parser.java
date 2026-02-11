package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Parses user input into commands and task objects.
 */
public class Parser {

    private static final String MESSAGE_EMPTY_COMMAND = "Please enter a command.";
    private static final String MESSAGE_UNKNOWN_COMMAND = "I don't know what that means.";

    private static final String FIND_USAGE = "Usage: find <keyword>";
    private static final String DEADLINE_USAGE =
            "Usage: deadline <description> /by yyyy-MM-dd [HHmm|HH:mm]";
    private static final String DEADLINE_USAGE_EXAMPLE =
            DEADLINE_USAGE + " (e.g. 2019-10-15 1800)";
    private static final String EVENT_USAGE =
            "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]";

    private static final String SPLIT_BY = "\\s+/by\\s+";
    private static final String SPLIT_FROM = "\\s+/from\\s+";
    private static final String SPLIT_TO = "\\s+/to\\s+";

    private static final String TIME_HHMM_REGEX = "\\d{4}";

    /**
     * Represents a parsed user command (command word + arguments).
     */
    public static class ParsedCommand {
        private final String commandWord;
        private final String arguments;

        /**
         * Constructs a {@code ParsedCommand} with the given word and arguments.
         *
         * @param commandWord Command word.
         * @param arguments Remaining arguments (may be empty).
         */
        public ParsedCommand(String commandWord, String arguments) {
            this.commandWord = commandWord;
            this.arguments = arguments;
        }

        public String getCommandWord() {
            return commandWord;
        }

        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Holds a parsed date, and an optional time (nullable).
     */
    private record DateTimeParts(LocalDate date, LocalTime time) {
        private DateTimeParts {
            assert date != null : "DateTimeParts.date must not be null";
        }
    }

    /**
     * Splits the given user input into command word and argument string.
     *
     * @param userInput Raw user input.
     * @return Parsed command.
     * @throws OrionException If the input is empty.
     */
    public ParsedCommand parse(String userInput) throws OrionException {
        String trimmed = normalize(userInput);

        if (trimmed.isEmpty()) {
            throw new OrionException(MESSAGE_EMPTY_COMMAND);
        }

        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0]; // already non-empty due to trimmed check above
        String arguments = (parts.length == 2) ? parts[1].trim() : "";

        return new ParsedCommand(commandWord, arguments);
    }

    /**
     * Parses a task creation command into a {@code Task}.
     *
     * @param commandWord Task command word: {@code todo}, {@code deadline}, or {@code event}.
     * @param arguments Arguments string following the command word.
     * @return Constructed task.
     * @throws OrionException If the input is invalid.
     */
    public Task parseTask(String commandWord, String arguments) throws OrionException {
        assert commandWord != null : "parseTask(): commandWord must not be null";

        String args = normalize(arguments);

        switch (commandWord) {
        case "todo":
            return parseTodo(args);

        case "deadline":
            return parseDeadline(args);

        case "event":
            return parseEvent(args);

        default:
            throw new OrionException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses a 1-based task number from user input and returns a 0-based index.
     *
     * @param arguments Raw task number input.
     * @param keyword Command keyword for error messages.
     * @param taskCount Current number of tasks.
     * @return 0-based task index.
     * @throws OrionException If the task number is missing or invalid.
     */
    public int parseTaskIndex(String arguments, String keyword, int taskCount) throws OrionException {
        assert keyword != null : "parseTaskIndex(): keyword must not be null";
        assert taskCount >= 0 : "parseTaskIndex(): taskCount must be >= 0";

        if (taskCount == 0) {
            throw new OrionException("There are no tasks to " + keyword + ". Add a task first.");
        }

        String trimmed = normalize(arguments);
        if (trimmed.isEmpty()) {
            throw new OrionException("Usage: " + keyword + " <taskNumber>");
        }

        int taskNumber = parsePositiveInt(trimmed,
                "Task number must be an integer. Usage: " + keyword + " <taskNumber>");

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OrionException("Task number must be between 1 and " + taskCount + ".");
        }

        return taskNumber - 1; // convert to 0-based
    }

    /**
     * Parses the keyword for the {@code find} command.
     *
     * @param arguments Raw arguments after {@code find}.
     * @return Trimmed keyword.
     * @throws OrionException If the keyword is missing.
     */
    public static String parseFindKeyword(String arguments) throws OrionException {
        String keyword = normalize(arguments);
        if (keyword.isEmpty()) {
            throw new OrionException(FIND_USAGE);
        }
        return keyword;
    }

    // ---------------- Task parsers ----------------

    private static Task parseTodo(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException("A todo needs a description. Usage: todo <description>");
        }
        return new Todo(arguments);
    }

    private static Task parseDeadline(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException(DEADLINE_USAGE);
        }

        String[] parts = splitOnce(arguments, SPLIT_BY);
        if (parts.length < 2) {
            throw new OrionException("A deadline needs '/by'. " + DEADLINE_USAGE);
        }

        String description = parts[0].trim();
        String byRaw = parts[1].trim();

        if (description.isEmpty()) {
            throw new OrionException("Deadline description cannot be empty. " + DEADLINE_USAGE);
        }
        if (byRaw.isEmpty()) {
            throw new OrionException("Deadline date/time cannot be empty. " + DEADLINE_USAGE);
        }

        DateTimeParts by = parseUserDateTime(byRaw, DEADLINE_USAGE_EXAMPLE);
        return new Deadline(description, by.date(), by.time());
    }

    private static Task parseEvent(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException(EVENT_USAGE);
        }

        String[] fromSplit = splitOnce(arguments, SPLIT_FROM);
        if (fromSplit.length < 2) {
            throw new OrionException("An event needs '/from'. " + EVENT_USAGE);
        }

        String description = fromSplit[0].trim();
        String[] toSplit = splitOnce(fromSplit[1], SPLIT_TO);
        if (toSplit.length < 2) {
            throw new OrionException("An event needs '/to'. " + EVENT_USAGE);
        }

        String fromRaw = toSplit[0].trim();
        String toRaw = toSplit[1].trim();

        if (description.isEmpty()) {
            throw new OrionException("Event description cannot be empty. " + EVENT_USAGE);
        }
        if (fromRaw.isEmpty() || toRaw.isEmpty()) {
            throw new OrionException("Event date/time cannot be empty. " + EVENT_USAGE);
        }

        DateTimeParts from = parseUserDateTime(fromRaw, EVENT_USAGE);
        DateTimeParts to = parseUserDateTime(toRaw, EVENT_USAGE);

        return new Event(description, from.date(), from.time(), to.date(), to.time());
    }

    // ---------------- Date/time parsing helpers ----------------

    private static DateTimeParts parseUserDateTime(String raw, String usage) throws OrionException {
        assert usage != null : "parseUserDateTime(): usage must not be null";

        String normalized = normalize(raw).replace('T', ' ');
        if (normalized.isEmpty()) {
            throw new OrionException("Invalid date/time. " + usage);
        }

        String[] tokens = normalized.split("\\s+");
        if (tokens.length == 1) {
            return new DateTimeParts(parseUserDate(tokens[0], usage), null);
        }
        if (tokens.length == 2) {
            LocalDate date = parseUserDate(tokens[0], usage);
            LocalTime time = parseUserTime(tokens[1], usage);
            return new DateTimeParts(date, time);
        }

        throw new OrionException("Invalid date/time. " + usage);
    }

    private static LocalDate parseUserDate(String token, String usage) throws OrionException {
        try {
            return LocalDate.parse(token); // yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new OrionException("Invalid date. " + usage);
        }
    }

    private static LocalTime parseUserTime(String token, String usage) throws OrionException {
        if (token.matches(TIME_HHMM_REGEX)) {
            int hour = Integer.parseInt(token.substring(0, 2));
            int minute = Integer.parseInt(token.substring(2, 4));

            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                throw new OrionException("Invalid time. " + usage);
            }
            return LocalTime.of(hour, minute);
        }

        try {
            return LocalTime.parse(token); // HH:mm 
        } catch (DateTimeParseException e) {
            throw new OrionException("Invalid time. " + usage);
        }
    }

    // ---------------- Small utilities ----------------

    /**
     * Normalises nullable input to a trimmed string.
     */
    private static String normalize(String raw) {
        return (raw == null) ? "" : raw.trim();
    }

    /**
     * Splits input once using the given regex, with limit 2.
     */
    private static String[] splitOnce(String input, String regex) {
        assert input != null : "splitOnce(): input must not be null";
        assert regex != null : "splitOnce(): regex must not be null";
        return input.split(regex, 2);
    }

    /**
     * Parses a positive integer or throws an OrionException with the given message.
     */
    private static int parsePositiveInt(String raw, String errorMessage) throws OrionException {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new OrionException(errorMessage);
        }
    }
}
