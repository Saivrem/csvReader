package exceptions;

import enums.Cause;
import reader.ExceptionLog;

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

    public CsvReadingException getException(Cause cause, ExceptionLog log) {
        return switch (cause) {
            case STRUCTURE -> new BrokenStructureException(log);
            case ENCLOSURE -> new EnclosureCharacterException(log);
            case COLUMNS -> new ColumnNumberException(log);
            case LARGE_LINE -> new LineIsTooLongException(log);
        };
    }
}
