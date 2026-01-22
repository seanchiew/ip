/**
 * Represents a task with a description and a done/not-done status
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new task with the given description.
     * New tasks start as "not done".
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markDone() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void markUndone() {
        this.isDone = false;
    }

    /** Returns "X" if done, otherwise " " */
    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the description text of this task. */
    protected String getDescription() {
        return description;
    }

    /** Base task representation, e.g. [X] read book */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getDescription();
    }
}
