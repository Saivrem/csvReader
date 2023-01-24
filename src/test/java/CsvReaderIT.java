

import org.junit.jupiter.api.Test;

public class CsvReaderIT extends BaseIT {

    @Test
    public void shouldCorrectlyReadSimpleCsv() {
        loadAndAssert("happyPath/expected.json", "happyPath/input.csv");
    }

    @Test
    public void shouldProcessTextEnclosure() {
        loadAndAssert("happyPath/textEnclosure/simpleExpected.json", "happyPath/textEnclosure/simpleInput.csv");
        loadAndAssert("happyPath/textEnclosure/allEnclosedExpected.json", "happyPath/textEnclosure/allEnclosedInput.csv");
        loadAndAssert("happyPath/textEnclosure/lineSeparatedExpected.json", "happyPath/textEnclosure/lineSeparatedInput.csv");
        loadAndAssert("happyPath/textEnclosure/emptyStringExpected.json", "happyPath/textEnclosure/emptyStringInput.csv");
    }

    @Test
    public void shouldProcessEscapeCharacter() {
        loadAndAssert("happyPath/escapeCharacter/simpleExpected.json", "happyPath/escapeCharacter/simpleInput.csv");
        loadAndAssert("happyPath/escapeCharacter/enclosedAndEscapedExpected.json", "happyPath/escapeCharacter/enclosedAndEscapedInput.csv");
    }

    @Test
    public void shouldProcessMultiline() {
        loadAndAssertMultiline("multiline/simpleExpected.json", "multiline/simpleInput.csv");
        loadAndAssertMultiline("multiline/escapedExpected.json", "multiline/escapedInput.csv");
    }
}
