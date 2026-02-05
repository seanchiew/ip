package orion;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Core logic for the Orion application.
 * Provides a getResponse() API for GUI and a run() wrapper for CLI.
 */
public class Orion {
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
            Parser.ParsedCommand command = parser.parse(trimmed);
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
            String response = getResponse(input);
            System.out.print(response);
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

        case "mark": {
            int index = parser.parseTaskIndex(arguments, "mark", tasks.size());
            Task task = tasks.markDone(index);
            storage.save(tasks.asUnmodifiableList());
            return ui.formatMark(task, true);
        }

        case "unmark": {
            int index = parser.parseTaskIndex(arguments, "unmark", tasks.size());
            Task task = tasks.markUndone(index);
            storage.save(tasks.asUnmodifiableList());
            return ui.formatMark(task, false);
        }

        case "find": {
            String keyword = Parser.parseFindKeyword(arguments);
            return ui.formatFindResults(tasks.find(keyword));
        }

        case "todo":
        case "deadline":
        case "event": {
            Task newTask = parser.parseTask(commandWord, arguments);
            tasks.add(newTask);
            storage.save(tasks.asUnmodifiableList());
            return ui.formatAdd(newTask, tasks.size());
        }

        case "delete": {
            int index = parser.parseTaskIndex(arguments, "delete", tasks.size());
            Task removed = tasks.remove(index);
            storage.save(tasks.asUnmodifiableList());
            return ui.formatDelete(removed, tasks.size());
        }

        default:
            throw new OrionException("I don't know what that means. "
                    + "Try: todo, deadline, event, list, mark, unmark, delete, bye");
        }
    }
}
