package orion;

import java.util.Scanner;

/**
 * Handles interactions with the user via standard input and output.
 */
public class Ui {
    private static final String INDENT = "    ";
    private static final String TASK_INDENT = "      ";
    private static final String LINE = INDENT + "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Constructs a {@code Ui} that reads user input from the given scanner.
     *
     * @param scanner Scanner to read input from.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads the next line of user input.
     *
     * @return Trimmed user input.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the greeting message. */
    public void showWelcome() {
        showLine();
        showIndented("Hello! I'm Orion");
        showIndented("What can I do for you?");
        showLine();
    }

    /** Prints the exit message. */
    public void showBye() {
        showLine();
        showIndented("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Prints an error message.
     *
     * @param message Error message to print.
     */
    public void showError(String message) {
        showLine();
        showIndented(message);
        showLine();
    }

    /**
     * Prints the list of tasks.
     *
     * @param tasks Task list to display.
     */
    public void showList(TaskList tasks) {
        showLine();
        showIndented("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(INDENT + (i + 1) + ". " + tasks.get(i));
        }
        showLine();
    }

    /**
     * Prints a success message after adding a task.
     *
     * @param task Task that was added.
     * @param size New size of the task list.
     */
    public void showAdd(Task task, int size) {
        showLine();
        showIndented("Got it. I've added this task:");
        System.out.println(TASK_INDENT + task);
        showIndented("Now you have " + size + " tasks in the list.");
        showLine();
    }

    /**
     * Prints a message after marking or unmarking a task.
     *
     * @param task Task that was updated.
     * @param isMark True if marking done, false if unmarking.
     */
    public void showMark(Task task, boolean isMark) {
        showLine();
        if (isMark) {
            showIndented("Nice! I've marked this task as done:");
        } else {
            showIndented("OK, I've marked this task as not done yet:");
        }
        System.out.println(TASK_INDENT + task);
        showLine();
    }

    /**
     * Prints a message after deleting a task.
     *
     * @param task Task that was removed.
     * @param size New size of the task list.
     */
    public void showDelete(Task task, int size) {
        showLine();
        showIndented("Noted. I've removed this task:");
        System.out.println(TASK_INDENT + task);
        showIndented("Now you have " + size + " tasks in the list.");
        showLine();
    }

    /** 
     * Closes the underlying scanner. 
     */
    public void close() {
        scanner.close();
    }

    private void showLine() {
        System.out.println(LINE);
    }

    private void showIndented(String message) {
        System.out.println(INDENT + message);
    }
}
