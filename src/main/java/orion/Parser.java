package orion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Parses user input into commands and task objects.
 */
public class Parser {

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

    private static final class DateTimeParts {
        private final LocalDate date;
        private final LocalTime time; // null if not provided

        private DateTimeParts(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
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
        if (userInput == null || userInput.trim().isEmpty()) {
            throw new OrionException("Please enter a command.");
        }

        String[] parts = userInput.trim().split("\\s+", 2);
        String commandWord = parts[0].trim();
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
        switch (commandWord) {
        case "todo":
            return parseTodo(arguments);

        case "deadline":
            return parseDeadline(arguments);

        case "event":
            return parseEvent(arguments);

        default:
            throw new OrionException("I don't know what that means.");
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
        if (taskCount == 0) {
            throw new OrionException("There are no tasks to " + keyword + ". Add a task first.");
        }
        if (arguments.isEmpty()) {
            throw new OrionException("Usage: " + keyword + " <taskNumber>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new OrionException("Task number must be an integer. Usage: "
                    + keyword + " <taskNumber>");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OrionException("Task number must be between 1 and " + taskCount + ".");
        }

        return taskNumber - 1;
    }

    private static Task parseTodo(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException("A todo needs a description. Usage: todo <description>");
        }
        return new Todo(arguments);
    }

    private static Task parseDeadline(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException("Usage: deadline <description> /by <date>");
        }

        String[] parts = arguments.split("\\s+/by\\s+", 2);
        if (parts.length < 2) {
            throw new OrionException("A deadline needs '/by'. "
                    + "Usage: deadline <description> /by yyyy-MM-dd [HHmm|HH:mm]");
        }

        String description = parts[0].trim();
        String byRaw = parts[1].trim();

        if (description.isEmpty()) {
            throw new OrionException("Deadline description cannot be empty. "
                    + "Usage: deadline <description> /by yyyy-MM-dd [HHmm|HH:mm]");
        }
        if (byRaw.isEmpty()) {
            throw new OrionException("Deadline date/time cannot be empty. "
                    + "Usage: deadline <description> /by yyyy-MM-dd [HHmm|HH:mm]");
        }

        DateTimeParts by = parseUserDateTime(byRaw,
                "Usage: deadline <description> /by yyyy-MM-dd [HHmm|HH:mm] (e.g. 2019-10-15 1800)");
        return new Deadline(description, by.date, by.time);
    }

    private static Task parseEvent(String arguments) throws OrionException {
        if (arguments.isEmpty()) {
            throw new OrionException("Usage: event <description> /from <start> /to <end>");
        }

        String[] fromSplit = arguments.split("\\s+/from\\s+", 2);
        if (fromSplit.length < 2) {
            throw new OrionException("An event needs '/from'. "
                    + "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] "
                    + "/to yyyy-MM-dd [HHmm|HH:mm]");
        }

        String description = fromSplit[0].trim();
        String[] toSplit = fromSplit[1].split("\\s+/to\\s+", 2);
        if (toSplit.length < 2) {
            throw new OrionException("An event needs '/to'. "
                    + "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] "
                    + "/to yyyy-MM-dd [HHmm|HH:mm]");
        }

        String fromRaw = toSplit[0].trim();
        String toRaw = toSplit[1].trim();

        if (description.isEmpty()) {
            throw new OrionException("Event description cannot be empty. "
                    + "Usage: event <description> /from ... /to ...");
        }
        if (fromRaw.isEmpty() || toRaw.isEmpty()) {
            throw new OrionException("Event date/time cannot be empty. "
                    + "Usage: event <description> /from ... /to ...");
        }

        DateTimeParts from = parseUserDateTime(fromRaw,
                "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]");
        DateTimeParts to = parseUserDateTime(toRaw,
                "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]");

        return new Event(description, from.date, from.time, to.date, to.time);
    }

    private static DateTimeParts parseUserDateTime(String raw, String usage) throws OrionException {
        String normalized = raw.trim().replace('T', ' ');
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
        if (token.matches("\\d{4}")) {
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
}
