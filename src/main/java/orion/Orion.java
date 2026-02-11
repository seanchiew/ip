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
    private static final String EMPTY_COMMAND_MESSAGE = "Please enter a command.";

    // Command words
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark";
    private static final String CMD_UNMARK = "unmark";
    private static final String CMD_FIND = "find";
    private static final String CMD_TODO = "todo";
    private static final String CMD_DEADLINE = "deadline";
    private static final String CMD_EVENT = "event";
    private static final String CMD_DELETE = "delete";

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
        String normalizedInput = normalizeInput(input);
        if (normalizedInput.isEmpty()) {
            return ui.formatError(EMPTY_COMMAND_MESSAGE);
        }

        try {
            Parser.ParsedCommand command = parser.parse(normalizedInput);
            return processCommand(command);
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

    private static String normalizeInput(String input) {
        return (input == null) ? "" : input.trim();
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

    private String processCommand(Parser.ParsedCommand command) throws OrionException {
        String commandWord = command.getCommandWord();
        String arguments = command.getArguments();

        if (CMD_BYE.equals(commandWord)) {
            isExit = true;
            return ui.formatBye();
        }

        return executeCommand(commandWord, arguments);
    }

    private String executeCommand(String commandWord, String arguments) throws OrionException {
        switch (commandWord) {
        case CMD_LIST:
            emphasizeNonNullTasks(); // assertion
            return ui.formatList(tasks);

        case CMD_MARK:
            return handleMark(arguments, true);

        case CMD_UNMARK:
            return handleMark(arguments, false);

        case CMD_FIND:
            return handleFind(arguments);

        case CMD_TODO:
        case CMD_DEADLINE:
        case CMD_EVENT:
            return handleAddTask(commandWord, arguments);

        case CMD_DELETE:
            return handleDelete(arguments);

        default:
            throw new OrionException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private String handleMark(String arguments, boolean markDone) throws OrionException {
        String keyword = markDone ? CMD_MARK : CMD_UNMARK;
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

        int duplicateIndex = tasks.indexOfDuplicate(newTask);
        if (duplicateIndex != -1) {
            Task existing = tasks.get(duplicateIndex);
            return ui.formatDuplicate(existing, duplicateIndex + 1);
        }

        tasks.add(newTask);
        saveTasks();
        return ui.formatAdd(newTask, tasks.size());
    }

    private String handleDelete(String arguments) throws OrionException {
        int index = parser.parseTaskIndex(arguments, CMD_DELETE, tasks.size());
        Task removed = tasks.remove(index);
        saveTasks();
        return ui.formatDelete(removed, tasks.size());
    }

    private void saveTasks() throws OrionException {
        storage.save(tasks.asUnmodifiableList());
    }

    private void emphasizeNonNullTasks() {
        assert tasks != null : "TaskList must be initialized";
    }
}
