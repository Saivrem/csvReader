package exceptions;

public class ColumnNumberException extends CsvReadingException {
    protected ColumnNumberException(int row) {
        super(row);
    }
}
