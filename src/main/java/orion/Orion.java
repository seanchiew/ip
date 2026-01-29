package orion;

import java.util.ArrayList;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Provides the entry point for the Orion command-line task application.
 * Reads commands from standard input and writes responses to standard output.
 */
public class Orion {
    private static final String INDENT = "    ";
    private static final String TASK_INDENT = "      ";
    private static final String LINE = INDENT + "____________________________________________________________";


    private static final class DateTimeParts {
        private final LocalDate date;
        private final LocalTime time; // null if not provided

        private DateTimeParts(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
        }
    }

    /**
     * Runs the Orion application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Storage storage = new Storage();
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            tasks = storage.load();
        } catch (OrionException e) {
            printError(e.getMessage());
        }

        // Greet
        printLine();
        printIndented("Hello! I'm Orion");
        printIndented("What can I do for you?");
        printLine();

        while (true) {
            String userInput = scanner.nextLine().trim();

            // Error: empty input
            if (userInput.isEmpty()) {
                printError("Please enter a command.");
                continue;
            }

            // Split into command + args (args may be empty)
            String[] parts = userInput.split("\\s+", 2);
            String cmd = parts[0].trim();
            String rest = (parts.length == 2) ? parts[1].trim() : "";

            // Exit condition
            if (cmd.equals("bye")) {
                break;
            }

            try {
                switch (cmd) {
                case "list":
                    handleList(tasks);
                    break;

                case "mark":
                    handleMarkUnmark(rest, tasks, true);
                    storage.save(tasks);
                    break;

                case "unmark":
                    handleMarkUnmark(rest, tasks, false);
                    storage.save(tasks);
                    break;

                // Task creation
                case "todo":
                case "deadline":
                case "event":
                    Task newTask = parseTask(cmd, rest);
                    tasks.add(newTask);
                    printAddSuccess(newTask, tasks.size());
                    storage.save(tasks);
                    break;
                
                // Task deletion
                case "delete":
                    handleDelete(rest, tasks);
                    storage.save(tasks);
                    break;

                default:
                    throw new OrionException("I don't know what that means. "
                            + "Try: todo, deadline, event, list, mark, unmark, delete, bye");
                }

            } catch (OrionException e) {
                printError(e.getMessage());
            }
        }

        // Exit
        printLine();
        printIndented("Bye. Hope to see you again soon!");
        printLine();
        scanner.close();
    }

    // -------------------- Command handlers --------------------

    // Handles list command
    private static void handleList(ArrayList<Task> tasks) {
        printLine();
        printIndented("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(INDENT + (i + 1) + ". " + tasks.get(i));
        }
        printLine();
    }

    // Handles mark/unmark command (rest should contain the task number)
    private static void handleMarkUnmark(String rest, ArrayList<Task> tasks, boolean isMark) throws OrionException {
        int index = parseTaskIndex(rest, isMark ? "mark" : "unmark", tasks.size());
        Task task = tasks.get(index);

        printLine();
        if (isMark) {
            task.markDone();
            printIndented("Nice! I've marked this task as done:");
        } else {
            task.markUndone();
            printIndented("OK, I've marked this task as not done yet:");
        }
        System.out.println(TASK_INDENT + task);
        printLine();
    }

    // Handles delete command
    private static void handleDelete(String rest, ArrayList<Task> tasks) throws OrionException {
        int index = parseTaskIndex(rest, "delete", tasks.size());
        Task removed = tasks.remove(index);

        printLine();
        printIndented("Noted. I've removed this task:");
        System.out.println(TASK_INDENT + removed);
        printIndented("Now you have " + tasks.size() + " tasks in the list.");
        printLine(); 
    }

    // -------------------- Parsing helpers --------------------

    // Returns 0-based index of task in tasks list based on rest arg (task number), 1-based from user
    private static int parseTaskIndex(String rest, String keyword, int taskCount) throws OrionException {
        if (taskCount == 0) {
            throw new OrionException("There are no tasks to " + keyword + ". Add a task first.");
        }

        if (rest.isEmpty()) {
            throw new OrionException("Usage: " + keyword + " <taskNumber>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new OrionException("Task number must be an integer. Usage: " + keyword + " <taskNumber>");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OrionException("Task number must be between 1 and " + taskCount + ".");
        }

        return taskNumber - 1; // convert to 0-based index
    }

    // Parses task creation commands
    private static Task parseTask(String cmd, String rest) throws OrionException {
        switch (cmd) {
        case "todo": {
            if (rest.isEmpty()) {
                throw new OrionException("A todo needs a description. Usage: todo <description>");
            }
            return new Todo(rest);
        }

        case "deadline": {
            if (rest.isEmpty()) {
                throw new OrionException("Usage: deadline <description> /by <time>");
            }

            String[] parts = rest.split("\\s+/by\\s+", 2);
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

        case "event": {
            if (rest.isEmpty()) {
                throw new OrionException("Usage: event <description> /from <start> /to <end>");
            }

            String[] fromSplit = rest.split("\\s+/from\\s+", 2);
            if (fromSplit.length < 2) {
                throw new OrionException("An event needs '/from'. "
                    + "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]");
            }

            String description = fromSplit[0].trim();
            String[] toSplit = fromSplit[1].split("\\s+/to\\s+", 2);
            if (toSplit.length < 2) {
                throw new OrionException("An event needs '/to'. "
                        + "Usage: event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]");
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

        default:
            throw new OrionException("I don't know what that means.");
        }
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
            // Accepts yyyy-MM-dd
            return LocalDate.parse(token);
        } catch (DateTimeParseException e) {
            throw new OrionException("Invalid date. " + usage);
        }
    }

    private static LocalTime parseUserTime(String token, String usage) throws OrionException {
        // Accept HHmm (e.g. 1800) OR HH:mm (e.g. 18:00)
        if (token.matches("\\d{4}")) {
            int hour = Integer.parseInt(token.substring(0, 2));
            int minute = Integer.parseInt(token.substring(2, 4));
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                throw new OrionException("Invalid time. " + usage);
            }
            return LocalTime.of(hour, minute);
        }

        try {
            return LocalTime.parse(token); // accepts HH:mm
        } catch (DateTimeParseException e) {
            throw new OrionException("Invalid time. " + usage);
        }
    }

    // -------------------- Output helpers --------------------

    private static void printAddSuccess(Task newTask, int size) {
        printLine();
        printIndented("Got it. I've added this task:");
        System.out.println(TASK_INDENT + newTask);
        printIndented("Now you have " + size + " tasks in the list.");
        printLine();
    }

    private static void printError(String message) {
        printLine();
        printIndented(message);
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }

    private static void printIndented(String msg) {
        System.out.println(INDENT + msg);
    }
}
