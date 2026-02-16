package orion;

import java.util.List;

/**
 * Formats responses shown to the user.
 * This class does not read input or print output; it only returns Strings.
 */
public class Ui {
    private static final String INDENT = "    ";
    private static final String TASK_INDENT = "      ";
    private static final String LINE = INDENT + "_______________________________________________________";
    private static final String LS = System.lineSeparator();

    private static final String WELCOME_TITLE = "Hello! I'm Orion";
    private static final String WELCOME_PROMPT = "What can I do for you?";
    private static final String BYE_MESSAGE = "Bye. Hope to see you again soon!";

    private static final String LIST_HEADER = "Here are the tasks in your list:";
    private static final String FIND_HEADER = "Here are the matching tasks in your list:";

    private static final String ADD_HEADER = "Got it. I've added this task:";
    private static final String DELETE_HEADER = "Noted. I've removed this task:";
    private static final String COUNT_PREFIX = "Now you have ";
    private static final String COUNT_SUFFIX = " tasks in the list.";

    private static final String MARK_DONE_MESSAGE = "Nice! I've marked this task as done:";
    private static final String MARK_UNDONE_MESSAGE = "OK, I've marked this task as not done yet:";

    private static final String DUPLICATE_HEADER = "That task already exists in your list (not added):";

    /**
     * Returns the greeting message.
     *
     * @return Welcome message string.
     */
    public String formatWelcome() {
        return framed(
                INDENT + WELCOME_TITLE,
                INDENT + WELCOME_PROMPT
        );
    }

    /**
     * Returns the exit message.
     *
     * @return Bye message string.
     */
    public String formatBye() {
        return framed(INDENT + BYE_MESSAGE);
    }

    /**
     * Returns a formatted error message.
     *
     * @param message Error message.
     * @return Error message string.
     */
    public String formatError(String message) {
        assert message != null : "formatError(): message must not be null";
        return framed(INDENT + message);
    }

    /**
     * Returns the formatted task list.
     *
     * @param tasks TaskList to display.
     * @return Task list string.
     */
    public String formatList(TaskList tasks) {
        assert tasks != null : "formatList(): tasks must not be null";

        StringBuilder body = new StringBuilder();
        body.append(INDENT).append(LIST_HEADER).append(LS);

        for (int i = 0; i < tasks.size(); i++) {
            body.append(formatNumberedTaskLine(i + 1, tasks.get(i)));
        }

        return framed(body.toString());
    }

    /**
     * Returns a formatted success message after adding a task.
     *
     * @param task Added task.
     * @param size New list size.
     * @return Add success message string.
     */
    public String formatAdd(Task task, int size) {
        assert task != null : "formatAdd(): task must not be null";
        assert size >= 0 : "formatAdd(): size must be non-negative";

        return framed(
                INDENT + ADD_HEADER,
                TASK_INDENT + task,
                INDENT + COUNT_PREFIX + size + COUNT_SUFFIX
        );
    }

    /**
     * Returns a formatted message after marking/unmarking a task.
     *
     * @param task Updated task.
     * @param isMark True if marking done, false if unmarking.
     * @return Mark/unmark message string.
     */
    public String formatMark(Task task, boolean isMark) {
        assert task != null : "formatMark(): task must not be null";

        String header = isMark ? MARK_DONE_MESSAGE : MARK_UNDONE_MESSAGE;
        return framed(
                INDENT + header,
                TASK_INDENT + task
        );
    }

    /**
     * Returns a formatted message after deleting a task.
     *
     * @param task Removed task.
     * @param size New list size.
     * @return Delete message string.
     */
    public String formatDelete(Task task, int size) {
        assert task != null : "formatDelete(): task must not be null";
        assert size >= 0 : "formatDelete(): size must be non-negative";

        return framed(
                INDENT + DELETE_HEADER,
                TASK_INDENT + task,
                INDENT + COUNT_PREFIX + size + COUNT_SUFFIX
        );
    }

    /**
     * Returns the formatted find results.
     *
     * @param matches Matching tasks.
     * @return Find results string.
     */
    public String formatFindResults(List<Task> matches) {
        assert matches != null : "formatFindResults(): matches must not be null";

        StringBuilder body = new StringBuilder();
        body.append(INDENT).append(FIND_HEADER).append(LS);

        for (int i = 0; i < matches.size(); i++) {
            body.append(formatNumberedTaskLine(i + 1, matches.get(i)));
        }

        return framed(body.toString());
    }

    /**
     * Returns a formatted message when the user tries to add a duplicate task.
     *
     * @param existing Existing task in the list.
     * @param taskNumber 1-based index of the existing task.
     * @return Duplicate warning message string.
     */
    public String formatDuplicate(Task existing, int taskNumber) {
        assert existing != null : "formatDuplicate(): existing task must not be null";
        assert taskNumber >= 1 : "formatDuplicate(): taskNumber must be >= 1";

        return framed(
                INDENT + DUPLICATE_HEADER,
                TASK_INDENT + taskNumber + ". " + existing
        );
    }

    /**
     * Wraps the given lines in Orion's UI frame (top/bottom LINE).
     * Each line is appended with a line separator.
     */
    private static String framed(String... lines) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);

        for (String line : lines) {
            if (line == null) {
                continue;
            }
            sb.append(line).append(LS);
        }

        sb.append(LINE).append(LS);
        return sb.toString();
    }

    private static String framed(String body) {
        assert body != null : "framed(body): body must not be null";

        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LS);

        sb.append(body);
        if (!body.endsWith(LS)) {
            sb.append(LS);
        }

        sb.append(LINE).append(LS);
        return sb.toString();
    }

    private static String formatNumberedTaskLine(int oneBasedIndex, Task task) {
        assert oneBasedIndex >= 1 : "formatNumberedTaskLine(): index must be >= 1";
        assert task != null : "formatNumberedTaskLine(): task must not be null";

        return INDENT + oneBasedIndex + ". " + task + LS;
    }
}
