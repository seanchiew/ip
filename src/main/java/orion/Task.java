package orion;

import java.util.Objects;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    private static final String TYPE_CODE = "T";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String DONE_FLAG = "1";
    private static final String NOT_DONE_FLAG = "0";

    private final String description;
    private final String normalizedDescription; // cached for duplicate checks/search
    private boolean isDone;

    /**
     * Constructs a {@code Task} with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.normalizedDescription = normalizeDescription(description);
        this.isDone = false;
    }

    /** 
     * Marks this task as done. 
     */
    public void markDone() {
        this.isDone = true;
    }

    /** 
     * Marks this task as not done. 
     */
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
     * Returns {@code true} if this task's description contains the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the keyword is found in the description.
     */
    public boolean matches(String keyword) {
        assert description != null : "Task description must not be null";
        String needle = keyword == null ? "" : keyword.trim().toLowerCase();
        if (needle.isEmpty()) {
            return false;
        }
        return description.toLowerCase().contains(needle);
    }

    /**
     * Returns the done flag as {@code "1"} if done, else {@code "0"}.
     *
     * @return Done flag string.
     */
    protected String getDoneFlag() {
        return isDone ? DONE_FLAG : NOT_DONE_FLAG;
    }

    /**
     * Returns {@code true} if {@code other} represents the same "logical" task as this one.
     * This ignores completion status and compares only the identifying fields.
     *
     * @param other Another task.
     * @return True if both tasks are considered duplicates.
     */
    public boolean isSameTask(Task other) {
        if (other == null) {
            return false;
        }
        // "Duplicate" requires same concrete task type (Todo vs Deadline vs Event).
        if (this.getClass() != other.getClass()) {
            return false;
        }
        // Compare normalized descriptions to avoid duplicates caused by casing / extra spaces.
        return this.normalizedDescription.equals(other.normalizedDescription);
    }

    /**
     * Normalizes task descriptions for duplicate detection.
     */
    protected static String normalizeDescription(String raw) {
        assert raw != null : "normalizeDescription(): raw must not be null";
        return raw.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /**
     * Returns a string representation of this task suitable for saving to disk.
     *
     * @return Data string of this task.
     */
    public String toDataString() {
        return TYPE_CODE + FIELD_SEPARATOR + getDoneFlag() + FIELD_SEPARATOR + getDescription();
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
