package reader;

import enums.Cause;
import exceptions.CsvReadingException;
import exceptions.CsvReadingExceptionFactory;
import exceptions.LineIsTooLongException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements AutoCloseable {

    private final CsvReadingExceptionFactory factory = CsvReadingExceptionFactory.getInstance();
    private final InputStream reader;
    private int rowsProcessed = 1;
    private int overflowProtection = 0;
    private int symmetryCheck = 0;
    private final Character DELIMITER;
    private final Character ESCAPE;
    private final Character ENCLOSURE;
    private boolean enclosedField = false;
    private boolean escapeActive = false;
    private final boolean sameEncEsc;
    private Character windowsBufferChar;
    private Character previousCharacter;
    private Character currentCharacter;
    private Character nextCharacter;
    private StringBuilder cell;
    private List<String> row;

    /**
     * Constructor
     *
     * @param reader    InputStream object of text to read
     * @param delimiter delimiting character
     * @param enclosure text enclosure, could be null
     * @param escape    escape char, could be null
     */
    public CsvReader(InputStream reader, Character delimiter, Character enclosure, Character escape) {
        this.DELIMITER = delimiter;
        this.reader = reader;
        this.ENCLOSURE = enclosure;
        this.ESCAPE = escape;
        this.sameEncEsc = enclosure == escape;
    }

    public List<String> readRow() throws CsvReadingException, IOException {
        if (!enclosedField) {
            prepareFields();
        }

        currentCharacter = windowsBufferChar == null ? (char) reader.read() : windowsBufferChar;

        while (true) {
            nextCharacter = (char) reader.read();

            if (previousCharacter != null && previousCharacter == '\r' && currentCharacter == '\n' && !enclosedField) {
                windowsBufferChar = nextCharacter;
                break;
            }

            if (currentCharacter == '\r') {
                charStep();
                continue;
            }

            if (currentCharacter == DELIMITER) {
                processDelimiter();
            } else if (currentCharacter == ENCLOSURE) {
                processEnclosureSymbol();
            } else if (currentCharacter == ESCAPE) {
                processEscapeSymbol();
            } else {
                cell.append(currentCharacter);
            }

            overflowValidation();
            charStep();

            if (currentCharacter == '\n' || !ready()) {
                if (!ready()
                        && currentCharacter != ESCAPE
                        && currentCharacter != DELIMITER
                        && (currentCharacter != ENCLOSURE || !enclosedField)) {
                    cell.append(currentCharacter);
                }
                if (previousCharacter == ENCLOSURE && currentCharacter == '\n') {
                    enclosedField = false;
                }
                if (currentCharacter == ENCLOSURE && enclosedField) {
                    enclosedField = false;
                }
                break;
            }
        }

        if (enclosedField && currentCharacter != '"') {
            cell.append(currentCharacter);
            charStep();
            readRow();
        }

        if (previousCharacter == DELIMITER || cell.length() > 0) {
            appendCell();
        }

        try {
            validateStructure();
        } catch (CsvReadingException e) {
            e.getCustomMessage();
        }

        return row;
    }

    private void processDelimiter() {
        if (!enclosedField) {
            appendCell();
        } else {
            cell.append(currentCharacter);
        }
    }

    private void processEnclosureSymbol() throws CsvReadingException, IOException {
        if (enclosedField) {
            if (sameEncEsc && !escapeActive && checkEscape()) {
                return;
            }
            if (escapeActive) {
                processEscapeSymbol();
            } else if (nextCharacter == DELIMITER || nextCharacter == '\n' || nextCharacter == '\r') {
                enclosedField = false;
            } else if (!ready()) {
                appendCell();
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        } else {
            if (previousCharacter == null || previousCharacter == DELIMITER) {
                enclosedField = true;
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        }
    }

    private void processEscapeSymbol() {
        if (!escapeActive && (nextCharacter == ESCAPE || nextCharacter == ENCLOSURE)) {
            escapeActive = true;
        } else if (previousCharacter == ESCAPE && escapeActive) {
            escapeActive = false;
            cell.append(currentCharacter);
        }
    }

    private void overflowValidation() throws CsvReadingException {
        if (overflowProtection <= LineIsTooLongException.LINE_LIMIT_SIZE) {
            overflowProtection++;
        } else {
            throw factory.getException(Cause.LARGE_LINE, getLog());
        }
    }

    private void validateStructure() throws CsvReadingException {
        int rowSize = row.size();
        if (symmetryCheck == 0) {
            symmetryCheck = rowSize;
        } else if (symmetryCheck != rowSize) {
            throw factory.getException(Cause.COLUMNS, getLog());
        }
        rowsProcessed++;
    }

    private boolean checkEscape() {
        return escapeActive = (nextCharacter == ESCAPE && currentCharacter == ESCAPE);
    }

    private void charStep() {
        previousCharacter = currentCharacter;
        currentCharacter = nextCharacter;
    }

    private void appendCell() {
        row.add(cell.toString());
        cell = new StringBuilder();
    }

    private void prepareFields() {
        overflowProtection = 0;
        row = new ArrayList<>();
        cell = new StringBuilder();
        currentCharacter = null;
        previousCharacter = null;
        nextCharacter = null;
    }

    private ExceptionLog getLog() {
        return new ExceptionLog(rowsProcessed, overflowProtection, row);
    }

    public boolean ready() throws IOException {
        return reader.available() > 0;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
