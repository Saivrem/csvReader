package exceptions;

import reader.ExceptionLog;

/**
 * @deprecated Retained for compatibility with the legacy reader.
 * The current parser does not throw this exception; it remains available through
 * {@link CsvReadingExceptionFactory} for existing callers.
 */
@Deprecated(since = "1.9.2", forRemoval = false)
public class BrokenStructureException extends CsvReadingException {
    protected BrokenStructureException(ExceptionLog log) {
        super(log);
    }
}
