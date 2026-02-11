package orion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a list of tasks and provides operations to modify it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Constructs an empty {@code TaskList}. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a {@code TaskList} containing a copy of the given tasks.
     *
     * @param tasks Tasks to initialize with.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";

        this.tasks = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            assert task != null : "Initial task list must not contain null tasks";
            this.tasks.add(task);
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given index.
     *
     * @param index 0-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        return getTaskAt(index, "get()");
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "add(): task must not be null";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index 0-based task index.
     * @return Removed task.
     */
    public Task remove(int index) {
        assertIndexInBounds(index, "remove()");
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index 0-based task index.
     * @return Updated task.
     */
    public Task markDone(int index) {
        Task task = getTaskAt(index, "markDone()");
        task.markDone();
        return task;
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index 0-based task index.
     * @return Updated task.
     */
    public Task markUndone(int index) {
        Task task = getTaskAt(index, "markUndone()");
        task.markUndone();
        return task;
    }

    /**
     * Returns the index of the first existing task that is a duplicate of {@code candidate}.
     *
     * @param candidate Task to check.
     * @return 0-based index of duplicate if found, else -1.
     */
    public int indexOfDuplicate(Task candidate) {
        assert candidate != null : "indexOfDuplicate(): candidate must not be null";

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isSameTask(candidate)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns an unmodifiable view of the underlying task list.
     *
     * @return Unmodifiable list of tasks.
     */
    public List<Task> asUnmodifiableList() {
        assert tasks != null : "Internal task list must not be null";
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return List of matching tasks, in the same order as the task list.
     */
    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.matches(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    // ---- Helpers ----

    private Task getTaskAt(int index, String caller) {
        assertIndexInBounds(index, caller);
        Task task = tasks.get(index);
        assert task != null : caller + ": task must not be null at index " + index;
        return task;
    }

    private void assertIndexInBounds(int index, String caller) {
        assert index >= 0 && index < tasks.size()
                : caller + ": index out of bounds: " + index + " (size=" + tasks.size() + ")";
    }
}
