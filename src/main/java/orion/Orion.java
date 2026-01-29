package orion;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides the entry point for the Orion command-line task application.
 * Reads commands from standard input and writes responses to standard output.
 */
public class Orion {
    private static final String INDENT = "    ";
    private static final String TASK_INDENT = "      ";
    private static final String LINE = INDENT + "____________________________________________________________";

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
                        + "Usage: deadline <description> /by <time>");
            }

            String description = parts[0].trim();
            String by = parts[1].trim();

            if (description.isEmpty()) {
                throw new OrionException("Deadline description cannot be empty. "
                        + "Usage: deadline <description> /by <time>");
            }
            if (by.isEmpty()) {
                throw new OrionException("Deadline time cannot be empty. "
                        + "Usage: deadline <description> /by <time>");
            }

            return new Deadline(description, by);
        }

        case "event": {
            if (rest.isEmpty()) {
                throw new OrionException("Usage: event <description> /from <start> /to <end>");
            }

            String[] fromSplit = rest.split("\\s+/from\\s+", 2);
            if (fromSplit.length < 2) {
                throw new OrionException("An event needs '/from'. "
                        + "Usage: event <description> /from <start> /to <end>");
            }

            String description = fromSplit[0].trim();
            String[] toSplit = fromSplit[1].split("\\s+/to\\s+", 2);
            if (toSplit.length < 2) {
                throw new OrionException("An event needs '/to'. "
                        + "Usage: event <description> /from <start> /to <end>");
            }

            String from = toSplit[0].trim();
            String to = toSplit[1].trim();

            if (description.isEmpty()) {
                throw new OrionException("Event description cannot be empty. "
                        + "Usage: event <description> /from <start> /to <end>");
            }
            if (from.isEmpty() || to.isEmpty()) {
                throw new OrionException("Event time cannot be empty. "
                        + "Usage: event <description> /from <start> /to <end>");
            }

            return new Event(description, from, to);
        }

        default:
            throw new OrionException("I don't know what that means.");
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
