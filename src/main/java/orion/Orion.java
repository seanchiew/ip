package orion;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Core logic for the Orion application.
 * Provides a getResponse() API for GUI and a run() wrapper for CLI.
 */
public class Orion {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I don't know what that means. Try: todo, deadline, event, list, mark, unmark, delete, bye";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    private boolean isExit;

    /**
     * Constructs an {@code Orion} application using the default storage path.
     * The storage path can be overridden via {@code -Dorion.dataFile=...}.
     */
    public Orion() {
        this.storage = new Storage();
        this.parser = new Parser();
        this.ui = new Ui();
        this.tasks = loadTasks(storage);
        this.isExit = false;
    }

    /**
     * Returns the welcome message.
     *
     * @return Welcome message string.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcome();
    }

    /**
     * Indicates whether the app should exit (after processing a command).
     *
     * @return True if exit was requested.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Handles a single user input and returns Orion's response as a string.
     *
     * @param input User input string.
     * @return Response string to display.
     */
    public String getResponse(String input) {
        String trimmed = (input == null) ? "" : input.trim();

        if (trimmed.isEmpty()) {
            return ui.formatError("Please enter a command.");
        }
        
        try {
            Parser.ParsedCommand command = parser.parse(input);
            String commandWord = command.getCommandWord();
            String arguments = command.getArguments();

            if ("bye".equals(commandWord)) {
                isExit = true;
                return ui.formatBye();
            }

            return executeCommand(commandWord, arguments);
        } catch (OrionException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Runs the Orion CLI application (optional since GUI is present).
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(getWelcomeMessage());

        while (!isExit) {
            String input = scanner.nextLine();
            System.out.print(getResponse(input));
        }

        scanner.close();
    }

    /**
     * Program entry point (CLI).
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Orion().run();
    }

    private static TaskList loadTasks(Storage storage) {
        try {
            ArrayList<Task> loaded = storage.load();
            return new TaskList(loaded);
        } catch (OrionException e) {
            // If loading fails, start with empty task list.
            return new TaskList();
        }
    }

    private String executeCommand(String commandWord, String arguments) throws OrionException {
        switch (commandWord) {
        case "list":
            return ui.formatList(tasks);

        case "mark":
            return handleMark(arguments, true);

        case "unmark":
            return handleMark(arguments, false);

        case "find":
            return handleFind(arguments);

        case "todo":
        case "deadline":
        case "event":
            return handleAddTask(commandWord, arguments);

        case "delete":
            return handleDelete(arguments);

        default:
            throw new OrionException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private String handleMark(String arguments, boolean markDone) throws OrionException {
        String keyword = markDone ? "mark" : "unmark";
        int index = parser.parseTaskIndex(arguments, keyword, tasks.size());

        Task updated = markDone ? tasks.markDone(index) : tasks.markUndone(index);
        saveTasks();

        return ui.formatMark(updated, markDone);
    }

    private String handleFind(String arguments) throws OrionException {
        String keyword = Parser.parseFindKeyword(arguments);
        return ui.formatFindResults(tasks.find(keyword));
    }

    private String handleAddTask(String commandWord, String arguments) throws OrionException {
        Task newTask = parser.parseTask(commandWord, arguments);
        tasks.add(newTask);
        saveTasks();
        return ui.formatAdd(newTask, tasks.size());
    }

    private String handleDelete(String arguments) throws OrionException {
        int index = parser.parseTaskIndex(arguments, "delete", tasks.size());
        Task removed = tasks.remove(index);
        saveTasks();
        return ui.formatDelete(removed, tasks.size());
    }

    private void saveTasks() throws OrionException {
        storage.save(tasks.asUnmodifiableList());
    }
}
