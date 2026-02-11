package orion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving tasks to disk.
 */
public class Storage {
    private static final String DEFAULT_DATA_FILE = "data/orion.txt";

    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String NO_TIME_MARKER = "-";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    private static final String ERROR_LOAD_PREFIX = "Failed to load tasks: ";
    private static final String ERROR_SAVE_PREFIX = "Failed to save tasks: ";
    private static final String ERROR_CORRUPTED_PREFIX = "Saved data is corrupted: ";

    private final Path dataPath;

    /**
     * Constructs a {@code Storage} that reads/writes to the data file path.
     * Uses {@code orion.dataFile} system property if provided, else defaults to {@code data/orion.txt}.
     */
    public Storage() {
        String filePath = System.getProperty("orion.dataFile", DEFAULT_DATA_FILE);
        this.dataPath = Paths.get(filePath);
        assert dataPath != null : "Resolved data path must not be null";
    }

    /**
     * Loads tasks from disk.
     *
     * @return Tasks loaded from the data file. Returns an empty list if the file does not exist.
     * @throws OrionException If the file exists but cannot be read or is corrupted.
     */
    public ArrayList<Task> load() throws OrionException {
        if (Files.notExists(dataPath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
            ArrayList<Task> loadedTasks = new ArrayList<>();

            for (String line : lines) {
                if (isBlank(line)) {
                    continue;
                }
                loadedTasks.add(parseLine(line));
            }

            return loadedTasks;
        } catch (IOException e) {
            throw new OrionException(ERROR_LOAD_PREFIX + e.getMessage());
        }
    }

    /**
     * Saves tasks to disk.
     *
     * @param tasks Tasks to save.
     * @throws OrionException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws OrionException {
        assert tasks != null : "save(): tasks must not be null";
        for (Task task : tasks) {
            assert task != null : "save(): tasks must not contain null elements";
        }

        try {
            ensureParentDirExists();

            List<String> serializedLines = new ArrayList<>();
            for (Task task : tasks) {
                serializedLines.add(task.toDataString());
            }

            Files.write(dataPath, serializedLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new OrionException(ERROR_SAVE_PREFIX + e.getMessage());
        }
    }

    private void ensureParentDirExists() throws IOException {
        Path parent = dataPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Task parseLine(String line) throws OrionException {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX);

        // Common minimum: TYPE | DONE | DESC
        requireMinParts(parts, 3, line);

        String type = parts[0].trim();
        int doneFlag = parseDoneFlag(parts[1].trim(), line);
        String description = parts[2].trim();

        Task task = switch (type) {
            case TYPE_TODO -> parseTodo(description);
            case TYPE_DEADLINE -> parseDeadline(parts, description, line);
            case TYPE_EVENT -> parseEvent(parts, description, line);
            default -> throw corrupted(line);
        };

        if (doneFlag == 1) {
            task.markDone();
        }
        return task;
    }

    private static Task parseTodo(String description) throws OrionException {
        // Task constructor already asserts non-null description; here we mainly guard against bad save format.
        if (description.isEmpty()) {
            throw corrupted("T | ... (empty description)");
        }
        return new Todo(description);
    }

    private static Task parseDeadline(String[] parts, String description, String rawLine) throws OrionException {
        // Expected: D | done | desc | date | timeOrDash
        requireMinParts(parts, 5, rawLine);

        LocalDate byDate = parseStoredDate(parts[3], rawLine);
        LocalTime byTime = parseStoredTimeOrNull(parts[4], rawLine);
        return new Deadline(description, byDate, byTime);
    }

    private static Task parseEvent(String[] parts, String description, String rawLine) throws OrionException {
        // Expected: E | done | desc | fromDate | fromTimeOrDash | toDate | toTimeOrDash
        requireMinParts(parts, 7, rawLine);

        LocalDate fromDate = parseStoredDate(parts[3], rawLine);
        LocalTime fromTime = parseStoredTimeOrNull(parts[4], rawLine);
        LocalDate toDate = parseStoredDate(parts[5], rawLine);
        LocalTime toTime = parseStoredTimeOrNull(parts[6], rawLine);

        return new Event(description, fromDate, fromTime, toDate, toTime);
    }

    private static void requireMinParts(String[] parts, int min, String rawLine) throws OrionException {
        if (parts.length < min) {
            throw corrupted(rawLine);
        }
    }

    private static OrionException corrupted(String rawLine) {
        return new OrionException(ERROR_CORRUPTED_PREFIX + rawLine);
    }

    private static int parseDoneFlag(String raw, String rawLine) throws OrionException {
        if ("0".equals(raw)) {
            return 0;
        }
        if ("1".equals(raw)) {
            return 1;
        }
        throw new OrionException(ERROR_CORRUPTED_PREFIX + "invalid done flag in line: " + rawLine);
    }

    private static LocalDate parseStoredDate(String raw, String rawLine) throws OrionException {
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw corrupted(rawLine);
        }
    }

    private static LocalTime parseStoredTimeOrNull(String raw, String rawLine) throws OrionException {
        String trimmed = raw.trim();
        if (NO_TIME_MARKER.equals(trimmed)) {
            return null;
        }
        try {
            return LocalTime.parse(trimmed);
        } catch (DateTimeParseException e) {
            throw corrupted(rawLine);
        }
    }
}
