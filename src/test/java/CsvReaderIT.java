

import org.junit.jupiter.api.Test;

public class CsvReaderIT extends BaseIT {

    @Test
    public void shouldCorrectlyReadSimpleCsv() {
        loadAndAssert("happyPath/expected.json", "happyPath/input.csv");
    }

    @Test
    public void shouldProcessTextEnclosure() {
        loadAndAssert("textEnclosure/simpleExpected.json", "textEnclosure/simpleInput.csv");
        loadAndAssert("textEnclosure/allEnclosedExpected.json", "textEnclosure/allEnclosedInput.csv");
        loadAndAssert("textEnclosure/lineSeparatedExpected.json", "textEnclosure/lineSeparatedInput.csv");
    }

    @Test
    public void shouldProcessEscapeCharacter() {
        loadAndAssert("escapeCharacter/simpleExpected.json", "escapeCharacter/simpleInput.csv");
        loadAndAssert("escapeCharacter/enclosedAndEscapedExpected.json", "escapeCharacter/enclosedAndEscapedInput.csv");
    }
}
