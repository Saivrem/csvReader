package exceptions;

import reader.ExceptionLog;

public class ColumnNumberException extends CsvReadingException {
    protected ColumnNumberException(ExceptionLog log) {
        super(log);
        customMessage.append(log.row()).append("\n");
    }

}
