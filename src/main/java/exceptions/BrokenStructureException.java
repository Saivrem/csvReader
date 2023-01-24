package exceptions;

import reader.ExceptionLog;

public class BrokenStructureException extends CsvReadingException {
    protected BrokenStructureException(ExceptionLog log) {
        super(log);
    }
}
