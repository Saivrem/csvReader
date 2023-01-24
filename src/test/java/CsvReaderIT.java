

import org.junit.jupiter.api.Test;

public class CsvReaderIT extends BaseIT {

    @Test
    public void shouldCorrectlyReadSimpleCsv() {
        loadAndAssert("happyPath/expected.txt", "happyPath/input.csv");
    }

    @Test
    public void shouldProcessTextEnclosure() {
        loadAndAssert("textEnclosure/expected.txt", "textEnclosure/input.csv");
    }
}
