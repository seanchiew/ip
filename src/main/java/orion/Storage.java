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
    private final Path dataPath;

    /**
     * Constructs a {@code Storage} that reads/writes to the data file path.
     * Uses {@code orion.dataFile} system property if provided, else defaults to {@code data/orion.txt}.
     */
    public Storage() {
        String filePath = System.getProperty("orion.dataFile", DEFAULT_DATA_FILE);
        this.dataPath = Paths.get(filePath);
    }

    /**
     * Loads tasks from disk.
     *
     * @return Tasks loaded from the data file. Returns an empty list if the file does not exist.
     * @throws OrionException If the file exists but cannot be read or is corrupted.
     */
    public ArrayList<Task> load() throws OrionException {
        if (!Files.exists(dataPath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
            ArrayList<Task> tasks = new ArrayList<>();

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                tasks.add(parseLine(line));
            }
            return tasks;
        } catch (IOException e) {
            throw new OrionException("Failed to load tasks: " + e.getMessage());
        }
    }

    /**
     * Saves tasks to disk.
     *
     * @param tasks Tasks to save.
     * @throws OrionException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws OrionException {
        try {
            Path parent = dataPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toDataString());
            }

            Files.write(dataPath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new OrionException("Failed to save tasks: " + e.getMessage());
        }
    }

    private static Task parseLine(String line) throws OrionException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new OrionException("Saved data is corrupted: " + line);
        }

        String type = parts[0];
        int doneFlag = parseDoneFlag(parts[1]);
        String description = parts[2];

        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;

        case "D":
            // Expected: D | done | desc | date | timeOrDash
            if (parts.length < 5) {
                throw new OrionException("Saved data is corrupted: " + line);
            }
            LocalDate byDate = parseStoredDate(parts[3], line);
            LocalTime byTime = parseStoredTimeOrNull(parts[4], line);
            task = new Deadline(description, byDate, byTime);
            break;

        case "E":
            // Expected: E | done | desc | fromDate | fromTimeOrDash | toDate | toTimeOrDash
            if (parts.length < 7) {
                throw new OrionException("Saved data is corrupted: " + line);
            }
            LocalDate fromDate = parseStoredDate(parts[3], line);
            LocalTime fromTime = parseStoredTimeOrNull(parts[4], line);
            LocalDate toDate = parseStoredDate(parts[5], line);
            LocalTime toTime = parseStoredTimeOrNull(parts[6], line);
            task = new Event(description, fromDate, fromTime, toDate, toTime);
            break;

        default:
            throw new OrionException("Saved data is corrupted: " + line);
        }

        if (doneFlag == 1) {
            task.markDone();
        }
        return task;
    }

    private static int parseDoneFlag(String raw) throws OrionException {
        if ("0".equals(raw)) {
            return 0;
        }
        if ("1".equals(raw)) {
            return 1;
        }
        throw new OrionException("Saved data is corrupted: invalid done flag " + raw);
    }

    private static LocalDate parseStoredDate(String raw, String line) throws OrionException {
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new OrionException("Saved data is corrupted: " + line);
        }
    }

    private static LocalTime parseStoredTimeOrNull(String raw, String line) throws OrionException {
        String trimmed = raw.trim();
        if ("-".equals(trimmed)) {
            return null;
        }
        try {
            return LocalTime.parse(trimmed);
        } catch (DateTimeParseException e) {
            throw new OrionException("Saved data is corrupted: " + line);
        }
    }

}
