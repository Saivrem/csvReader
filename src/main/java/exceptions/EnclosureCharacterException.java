package exceptions;

import reader.ExceptionLogDTO;

public class EnclosureCharacterException extends CsvReadingException {
    protected EnclosureCharacterException(ExceptionLogDTO log) {
        super(log);
    }
}
