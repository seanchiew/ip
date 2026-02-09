package orion;

import java.util.List;

/**
 * Formats responses shown to the user.
 * This class does not read input or print output; it only returns Strings.
 */
public class Ui {
    private static final String INDENT = "    ";
    private static final String TASK_INDENT = "      ";
    private static final String LINE = INDENT
            + "_______________________________________________________";
    private static final String LS = System.lineSeparator();

    /**
     * Returns the greeting message.
     *
     * @return Welcome message string.
     */
    public String formatWelcome() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Hello! I'm Orion").append(LS);
        sb.append(INDENT).append("What can I do for you?").append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns the exit message.
     *
     * @return Bye message string.
     */
    public String formatBye() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Bye. Hope to see you again soon!").append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns a formatted error message.
     *
     * @param message Error message.
     * @return Error message string.
     */
    public String formatError(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append(message).append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns the formatted task list.
     *
     * @param tasks TaskList to display.
     * @return Task list string.
     */
    public String formatList(TaskList tasks) {
        assert tasks != null : "formatList(): tasks must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Here are the tasks in your list:").append(LS);

        for (int i = 0; i < tasks.size(); i++) {
            sb.append(INDENT).append(i + 1).append(". ").append(tasks.get(i)).append(LS);
        }

        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns a formatted success message after adding a task.
     *
     * @param task Added task.
     * @param size New list size.
     * @return Add success message string.
     */
    public String formatAdd(Task task, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Got it. I've added this task:").append(LS);
        sb.append(TASK_INDENT).append(task).append(LS);
        sb.append(INDENT).append("Now you have ").append(size)
                .append(" tasks in the list.").append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns a formatted message after marking/unmarking a task.
     *
     * @param task Updated task.
     * @param isMark True if marking done, false if unmarking.
     * @return Mark/unmark message string.
     */
    public String formatMark(Task task, boolean isMark) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append(isMark
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:").append(LS);
        sb.append(TASK_INDENT).append(task).append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns a formatted message after deleting a task.
     *
     * @param task Removed task.
     * @param size New list size.
     * @return Delete message string.
     */
    public String formatDelete(Task task, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Noted. I've removed this task:").append(LS);
        sb.append(TASK_INDENT).append(task).append(LS);
        sb.append(INDENT).append("Now you have ").append(size)
                .append(" tasks in the list.").append(LS);
        sb.append(LINE).append(LS);
        return sb.toString();
    }

    /**
     * Returns the formatted find results.
     *
     * @param matches Matching tasks.
     * @return Find results string.
     */
    public String formatFindResults(List<Task> matches) {
        assert matches != null : "formatFindResults(): matches must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);
        sb.append(INDENT).append("Here are the matching tasks in your list:").append(LS);

        for (int i = 0; i < matches.size(); i++) {
            sb.append(INDENT).append(i + 1).append(". ").append(matches.get(i)).append(LS);
        }

        sb.append(LINE).append(LS);
        return sb.toString();
    }
}
