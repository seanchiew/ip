package orion;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Constructs a {@code Task} with the specified description.
     *
     * @param description Description of the task.
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

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns the done flag as {@code "1"} if done, else {@code "0"}.
     *
     * @return Done flag string.
     */
    protected String getDoneFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns a string representation of this task suitable for saving to disk.
     *
     * @return Data string of this task.
     */
    public String toDataString() {
        return "T | " + getDoneFlag() + " | " + getDescription();
    }

    /**
     * Returns a string representation of this task for display in the UI.
     *
     * @return String representation of this task.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + statusIcon + "] " + description;
    }
}
