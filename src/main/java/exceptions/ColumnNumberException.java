package exceptions;

import reader.ExceptionLogDTO;

public class ColumnNumberException extends CsvReadingException {
    protected ColumnNumberException(ExceptionLogDTO log) {
        super(log);
        customMessage.append(log.getRow()).append("\n");
    }

}
