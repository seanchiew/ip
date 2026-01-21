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

    /** Returns a user-friendly representation, e.g [X] read book */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + statusIcon + "] " + description;
    }
}
