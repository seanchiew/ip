package orion;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlineTest {

    @Test
    public void toDataString_timeProvided_storesTime() {
        Deadline d = new Deadline("return book",
                LocalDate.parse("2019-10-15"),
                LocalTime.of(18, 0));

        assertEquals("D | 0 | return book | 2019-10-15 | 18:00", d.toDataString());
    }

    @Test
    public void toDataString_timeMissing_storesDash() {
        Deadline d = new Deadline("return book",
                LocalDate.parse("2019-10-15"),
                null);

        assertEquals("D | 0 | return book | 2019-10-15 | -", d.toDataString());
    }
}
