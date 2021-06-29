package reader;

import enums.Cause;
import exceptions.CsvReadingException;
import exceptions.CsvReadingExceptionFactory;
import exceptions.LineIsTooLongException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class CsvReader implements AutoCloseable {

    //service fields
    protected int rowsProcessed = 1, overflowProtection = 0, symmetryCheck = 0;
    protected final InputStreamReader reader;
    protected final Character delimiter;
    protected Character previousCharacter, currentCharacter, nextCharacter;

    private final Character escape, enclosure;

    private boolean enclosedField = false;
    private boolean escapeActive = false;

    protected CsvReadingExceptionFactory factory = CsvReadingExceptionFactory.getInstance();

    protected StringBuilder cell;
    protected LinkedList<String> row;

    public CsvReader(InputStreamReader reader, Character delimiter, Character enclosure, Character escape) {
        this.delimiter = delimiter;
        this.reader = reader;
        this.enclosure = enclosure;
        this.escape = escape;
    }

    public LinkedList<String> readRow() throws CsvReadingException, IOException {
        if (!enclosedField) {
            prepareFields();
        }
        currentCharacter = (char) reader.read();

        while (true) {
            nextCharacter = (char) reader.read();
            if (currentCharacter == '\r') {
                charStep();
                continue;
            }

            if (currentCharacter == delimiter) {
                processDelimiter();
            } else if (currentCharacter == enclosure) {
                processEnclosureSymbol();
            } else if (currentCharacter == escape) {
                calculateEscape();
            } else {
                cell.append(currentCharacter);
            }

            overflowValidation();
            charStep();

            if (currentCharacter == '\n' || !ready()) {
                if (currentCharacter == enclosure && enclosedField) {
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

        if (previousCharacter == delimiter || cell.length() > 0) {
            processDelimiter();
        }

        try {
            validateStructure();
        } catch (CsvReadingException e) {
            e.getCustomMessage();
        }

        return row;
    }

    protected void processDelimiter() {
        if (!enclosedField) {
            row.add(cell.toString());
            cell = new StringBuilder();
        } else {
            cell.append(currentCharacter);
        }
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    protected void overflowValidation() throws CsvReadingException {
        if (overflowProtection <= LineIsTooLongException.LINE_LIMIT_SIZE) {
            overflowProtection++;
        } else {
            throw factory.getException(Cause.LARGE_LINE, getLog());
        }
    }

    protected void prepareFields() {
        overflowProtection = 0;
        row = new LinkedList<>();
        cell = new StringBuilder();
        currentCharacter = null;
        previousCharacter = null;
        nextCharacter = null;
    }

    protected void validateStructure() throws CsvReadingException {
        int rowSize = row.size();
        if (symmetryCheck == 0) {
            symmetryCheck = rowSize;
        } else if (symmetryCheck != rowSize) {
            throw factory.getException(Cause.COLUMNS, getLog());
        }
        rowsProcessed++;
    }

    private void processEnclosureSymbol() throws CsvReadingException, IOException {
        if (!enclosedField) {
            if (previousCharacter == null || previousCharacter == delimiter) {
                enclosedField = true;
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        } else {
            if (isEscaped()) {
                cell.append(currentCharacter);
                escapeActive = false;
            } else if (nextCharacter == delimiter || nextCharacter == '\n') {
                enclosedField = false;
            } else if (!ready()) {
                enclosedField = false;
                processDelimiter();
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        }
    }

    private void charStep() {
        previousCharacter = currentCharacter;
        currentCharacter = nextCharacter;
    }

    private void calculateEscape() {
        if (nextCharacter == escape || nextCharacter == enclosure) {
            escapeActive = true;
        } else if (previousCharacter == escape) {
            escapeActive = false;
            cell.append(currentCharacter);
        }
    }

    private boolean isEscaped() {
        boolean result;
        if (enclosure == escape) {
            result = nextCharacter == enclosure && escapeActive;
        } else {
            result = previousCharacter == escape && escapeActive;
        }
        return result;
    }

    protected ExceptionLogDTO getLog() {
        return new ExceptionLogDTO(rowsProcessed, overflowProtection, row);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
