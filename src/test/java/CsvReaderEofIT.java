import org.junit.jupiter.api.Test;

public class CsvReaderEofIT extends BaseIT {

    @Test
    public void shouldRecognizeEmptyInput() {
        loadAndAssertMultiline("base_parser/eof/emptyExpected.json", "base_parser/eof/emptyInput.csv");
        loadAndAssertDirectReads("base_parser/eof/emptyExpected.json", "base_parser/eof/emptyInput.csv");
    }

    @Test
    public void shouldPreserveRowsWithLf() {
        loadAndAssertMultiline("base_parser/eof/lfExpected.json", "base_parser/eof/lfInput.csv");
        loadAndAssertMultiline("base_parser/eof/lfWithoutFinalNewlineExpected.json", "base_parser/eof/lfWithoutFinalNewlineInput.csv");
    }

    @Test
    public void shouldPreserveRowsWithCrlf() {
        loadAndAssertMultiline("base_parser/eof/crlfExpected.json", "base_parser/eof/crlfInput.csv");
        loadAndAssertMultiline("base_parser/eof/crlfWithoutFinalNewlineExpected.json", "base_parser/eof/crlfWithoutFinalNewlineInput.csv");
    }

    @Test
    public void shouldReadWithoutCallingHasNextFirst() {
        loadAndAssertDirectReads("base_parser/eof/singleRowExpected.json", "base_parser/eof/singleRowInput.csv");
    }

    @Test
    public void shouldNotUseAvailableToDetermineEof() {
        loadAndAssertWithZeroAvailable("base_parser/eof/singleRowExpected.json", "base_parser/eof/singleRowInput.csv");
    }

    @Test
    public void shouldPreserveCustomEnclosureAndEscapeResults() {
        loadAndAssertMultiline(
                "base_parser/eof/customSymbolsExpected.json",
                "base_parser/eof/customSymbolsInput.csv",
                ',', '|', '!'
        );
    }

    @Test
    public void shouldPreserveEnclosedFieldAtEof() {
        loadAndAssertMultiline("base_parser/eof/enclosedFieldExpected.json", "base_parser/eof/enclosedFieldInput.csv");
    }

    @Test
    public void shouldPreserveEmptyEnclosedRowResult() {
        loadAndAssertMultiline("base_parser/eof/emptyEnclosedFieldExpected.json", "base_parser/eof/emptyEnclosedFieldInput.csv");
    }

    @Test
    public void shouldPreserveRowsWithMixedLineEndings() {
        loadAndAssertMultiline("base_parser/eof/mixedLineEndingsExpected.json", "base_parser/eof/mixedLineEndingsInput.csv");
    }
}
