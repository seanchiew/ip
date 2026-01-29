package orion;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides the entry point for the Orion command-line task application.
 * Reads commands from standard input and writes responses to standard output.
 */
public class Orion {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    /**
     * Constructs an {@code Orion} application using the default storage path.
     * The storage path can be overridden via {@code -Dorion.dataFile=...}.
     */
    public Orion() {
        this.storage = new Storage();
        this.parser = new Parser();
        this.ui = new Ui(new Scanner(System.in));
        this.tasks = loadTasks(storage, ui);
    }

    /**
     * Runs the Orion application.
     */
    public void run() {
        ui.showWelcome();

        while (true) {
            String userInput = ui.readCommand();

            try {
                Parser.ParsedCommand command = parser.parse(userInput);
                String commandWord = command.getCommandWord();
                String arguments = command.getArguments();

                if (commandWord.equals("bye")) {
                    break;
                }

                handleCommand(commandWord, arguments);
            } catch (OrionException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showBye();
        ui.close();
    }

    /**
     * Program entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Orion().run();
    }

    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            ArrayList<Task> loaded = storage.load();
            return new TaskList(loaded);
        } catch (OrionException e) {
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    private void handleCommand(String commandWord, String arguments) throws OrionException {
        switch (commandWord) {
        case "list":
            ui.showList(tasks);
            break;

        case "mark": {
            int index = parser.parseTaskIndex(arguments, "mark", tasks.size());
            Task task = tasks.markDone(index);
            ui.showMark(task, true);
            storage.save(tasks.asUnmodifiableList());
            break;
        }

        case "unmark": {
            int index = parser.parseTaskIndex(arguments, "unmark", tasks.size());
            Task task = tasks.markUndone(index);
            ui.showMark(task, false);
            storage.save(tasks.asUnmodifiableList());
            break;
        }

        case "find": {
            String keyword = Parser.parseFindKeyword(arguments);
            ui.showFindResults(tasks.find(keyword));
            break;
        }

        case "todo":
        case "deadline":
        case "event": {
            Task newTask = parser.parseTask(commandWord, arguments);
            tasks.add(newTask);
            ui.showAdd(newTask, tasks.size());
            storage.save(tasks.asUnmodifiableList());
            break;
        }

        case "delete": {
            int index = parser.parseTaskIndex(arguments, "delete", tasks.size());
            Task removed = tasks.remove(index);
            ui.showDelete(removed, tasks.size());
            storage.save(tasks.asUnmodifiableList());
            break;
        }

        default:
            throw new OrionException("I don't know what that means. "
                    + "Try: todo, deadline, event, list, mark, unmark, delete, bye");
        }
    }
}
