package reader;

import enums.Cause;
import exceptions.CsvReadingException;
import exceptions.CsvReadingExceptionFactory;
import exceptions.LineIsTooLongException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class CsvReader implements AutoCloseable {

    private final CsvReadingExceptionFactory factory = CsvReadingExceptionFactory.getInstance();

    //service fields
    private final InputStreamReader reader;
    private int rowsProcessed = 1, overflowProtection = 0, symmetryCheck = 0;
    private final Character delimiter, escape, enclosure;

    //logic gates
    private boolean enclosedField = false;
    private boolean escapeActive = false;
    private final boolean sameEncEsc;

    //State fields
    private Character previousCharacter, currentCharacter, nextCharacter;
    private StringBuilder cell;
    private LinkedList<String> row;

    /**
     * Constructor
     *
     * @param reader    InputStreamReader object of text to read
     * @param delimiter delimiting character
     * @param enclosure text enclosure, could be null
     * @param escape    escape char, could be null
     */
    public CsvReader(InputStreamReader reader, Character delimiter, Character enclosure, Character escape) {
        this.delimiter = delimiter;
        this.reader = reader;
        this.enclosure = enclosure;
        this.escape = escape;
        this.sameEncEsc = enclosure == escape;
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
                processEscapeSymbol();
            } else {
                cell.append(currentCharacter);
            }

            overflowValidation();
            charStep();

            if (currentCharacter == '\n' || !ready()) {
                if (previousCharacter == enclosure && currentCharacter == '\n') {
                    enclosedField = false;
                }
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
            } else if (nextCharacter == delimiter || nextCharacter == '\n') {
                enclosedField = false;
            } else if (!ready()) {
                appendCell();
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        } else {
            if (previousCharacter == null || previousCharacter == delimiter) {
                enclosedField = true;
            } else {
                throw factory.getException(Cause.ENCLOSURE, getLog());
            }
        }
    }

    private void processEscapeSymbol() {
        if (!escapeActive && (nextCharacter == escape || nextCharacter == enclosure)) {
            escapeActive = true;
        } else if (previousCharacter == escape && escapeActive) {
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
        return escapeActive = (nextCharacter == escape && currentCharacter == escape);
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
        row = new LinkedList<>();
        cell = new StringBuilder();
        currentCharacter = null;
        previousCharacter = null;
        nextCharacter = null;
    }

    private ExceptionLogDTO getLog() {
        return new ExceptionLogDTO(rowsProcessed, overflowProtection, row);
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
