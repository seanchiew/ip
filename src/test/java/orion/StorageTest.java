package orion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageTest {

    @Test
    public void saveThenLoad_roundTrip_preservesTaskData(@TempDir Path tempDir) throws Exception {
        // Use a temp data file so the test doesn't touch your real data/orion.txt
        String previous = System.getProperty("orion.dataFile");
        System.setProperty("orion.dataFile", tempDir.resolve("orion.txt").toString());

        try {
            Storage storage = new Storage();

            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(new Todo("read book"));

            Deadline d = new Deadline("return book",
                    LocalDate.parse("2019-10-15"),
                    LocalTime.of(18, 0));
            d.markDone();
            tasks.add(d);

            tasks.add(new Event("project meeting",
                    LocalDate.parse("2019-10-16"), null,
                    LocalDate.parse("2019-10-16"), null));

            storage.save(tasks);
            ArrayList<Task> loaded = storage.load();

            assertEquals(tasks.size(), loaded.size());

            for (int i = 0; i < tasks.size(); i++) {
                assertEquals(tasks.get(i).toDataString(), loaded.get(i).toDataString());
            }
        } finally {
            // Restore property to avoid affecting other tests/runs
            if (previous == null) {
                System.clearProperty("orion.dataFile");
            } else {
                System.setProperty("orion.dataFile", previous);
            }
        }
    }

    @Test
    public void load_corruptedData_throwsOrionException(@TempDir Path tempDir) throws Exception {
        String previous = System.getProperty("orion.dataFile");
        System.setProperty("orion.dataFile", tempDir.resolve("orion.txt").toString());

        try {
            // Invalid date "2019-99-99" should be treated as corrupted
            Files.writeString(tempDir.resolve("orion.txt"),
                    "D | 0 | return book | 2019-99-99 | -\n");

            Storage storage = new Storage();

            assertThrows(OrionException.class, storage::load);
        } finally {
            if (previous == null) {
                System.clearProperty("orion.dataFile");
            } else {
                System.setProperty("orion.dataFile", previous);
            }
        }
    }
}
