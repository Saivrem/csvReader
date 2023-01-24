

import org.junit.jupiter.api.Test;

public class CsvReaderIT extends BaseIT {

    @Test
    public void shouldCorrectlyReadSimpleCsv() {
        loadAndAssert("happyPath/expected.txt", "happyPath/input.csv");
    }

    @Test
    public void shouldProcessTextEnclosure() {
        loadAndAssert("textEnclosure/simpleExpected.txt", "textEnclosure/simpleInput.csv");
        loadAndAssert("textEnclosure/allEnclosedExpected.txt", "textEnclosure/allEnclosedInput.csv");
    }
}
