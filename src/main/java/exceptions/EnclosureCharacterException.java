package exceptions;

import reader.ExceptionLog;

public class EnclosureCharacterException extends CsvReadingException {
    protected EnclosureCharacterException(ExceptionLog log) {
        super(log);
    }
}
