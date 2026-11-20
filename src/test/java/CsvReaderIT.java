

import org.junit.jupiter.api.Test;

public class CsvReaderIT extends BaseIT {

    @Test
    public void shouldCorrectlyReadSimpleCsv() {
        loadAndAssert("base_parser/happyPath/expected.json", "base_parser/happyPath/input.csv");
    }

    @Test
    public void shouldProcessTextEnclosure() {
        loadAndAssert("base_parser/happyPath/textEnclosure/simpleExpected.json", "base_parser/happyPath/textEnclosure/simpleInput.csv");
        loadAndAssert("base_parser/happyPath/textEnclosure/allEnclosedExpected.json", "base_parser/happyPath/textEnclosure/allEnclosedInput.csv");
        loadAndAssert("base_parser/happyPath/textEnclosure/lineSeparatedExpected.json", "base_parser/happyPath/textEnclosure/lineSeparatedInput.csv");
        loadAndAssert("base_parser/happyPath/textEnclosure/emptyStringExpected.json", "base_parser/happyPath/textEnclosure/emptyStringInput.csv");
    }

    @Test
    public void shouldProcessEscapeCharacter() {
        loadAndAssert("base_parser/happyPath/escapeCharacter/simpleExpected.json", "base_parser/happyPath/escapeCharacter/simpleInput.csv");
        loadAndAssert("base_parser/happyPath/escapeCharacter/enclosedAndEscapedExpected.json", "base_parser/happyPath/escapeCharacter/enclosedAndEscapedInput.csv");
    }

    @Test
    public void shouldProcessMultiline() {
        loadAndAssertMultiline("base_parser/multiline/simpleExpected.json", "base_parser/multiline/simpleInput.csv");
        loadAndAssertMultiline("base_parser/multiline/escapedExpected.json", "base_parser/multiline/escapedInput.csv");
    }
}
