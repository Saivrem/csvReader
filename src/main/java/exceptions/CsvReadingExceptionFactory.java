package exceptions;

import enums.Cause;

public class CsvReadingExceptionFactory {

    private static CsvReadingExceptionFactory instance;

    private CsvReadingExceptionFactory() {
    }

    public static CsvReadingExceptionFactory getInstance() {
        if (instance == null) {
            instance = new CsvReadingExceptionFactory();
        }
        return instance;
    }

    public CsvReadingException getException(Cause cause, int row) {
        CsvReadingException exception;
        switch (cause) {
            case STRUCTURE:
                exception = new BrokenStructureException(row);
                break;
            case ESCAPE:
                exception = new EscapeCharacterException(row);
                break;
            case COLUMNS:
                exception = new ColumnNumberException(row);
                break;
            case LARGE_LINE:
                exception = new LineIsTooLongException(row);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + cause);
        }
        return exception;
    }
}
