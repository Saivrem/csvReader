package exceptions;

import reader.ExceptionLogDTO;

public class BrokenStructureException extends CsvReadingException {
    protected BrokenStructureException(ExceptionLogDTO log) {
        super(log);
    }
}
