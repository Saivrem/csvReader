package exceptions;

import enums.Cause;
import reader.ExceptionLogDTO;

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

    public CsvReadingException getException(Cause cause, ExceptionLogDTO log) {
        CsvReadingException exception;
        switch (cause) {
            case STRUCTURE:
                exception = new BrokenStructureException(log);
                break;
            case ENCLOSURE:
                exception = new EnclosureCharacterException(log);
                break;
            case COLUMNS:
                exception = new ColumnNumberException(log);
                break;
            case LARGE_LINE:
                exception = new LineIsTooLongException(log);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + cause);
        }
        return exception;
    }
}
