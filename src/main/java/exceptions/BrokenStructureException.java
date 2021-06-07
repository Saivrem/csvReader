package exceptions;

public class BrokenStructureException extends CsvReadingException {
    protected BrokenStructureException(int row) {
        super(row);
    }
}
