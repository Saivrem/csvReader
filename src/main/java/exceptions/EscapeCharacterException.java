package exceptions;

public class EscapeCharacterException extends CsvReadingException {
    protected EscapeCharacterException(int row) {
        super(row);
    }
}
