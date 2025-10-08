package reader;

import exceptions.CsvReadingException;
import exceptions.CsvReadingExceptionFactory;
import exceptions.LineIsTooLongException;
import helpers.Cause;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static helpers.Constants.CR;
import static helpers.Constants.LF;

public class CsvReader implements AutoCloseable {

    private final CsvReadingExceptionFactory EXCEPTION_FACTORY = CsvReadingExceptionFactory.getInstance();
    private final InputStream READER;
    private final Character DELIMITER;
    private final Character ESCAPE;
    private final Character ENCLOSURE;

    private final boolean SAME_ENC_ESC;

    protected int rowsProcessed = 1;
    private int overflowProtection = 0;
    private int symmetryCheck = 0;
    private Character windowsBufferChar;
    private Character previousCharacter;
    private Character currentCharacter;
    private Character nextCharacter;
    private StringBuilder cell;
    private List<String> row;

    private boolean enclosedField = false;
    private boolean escapeActive = false;

    /**
     * Constructor
     *
     * @param READER    InputStream object of text to read
     * @param delimiter delimiting character
     * @param enclosure text enclosure, could be null
     * @param escape    escape char, could be null
     */
    public CsvReader(InputStream READER, Character delimiter, Character enclosure, Character escape) {
        this.DELIMITER = delimiter;
        this.READER = READER;
        this.ENCLOSURE = enclosure;
        this.ESCAPE = escape;
        this.SAME_ENC_ESC = enclosure == escape;
    }

    public List<String> readRow() throws CsvReadingException, IOException {
        if (!enclosedField) {
            prepareFields();
        }

        currentCharacter = windowsBufferChar == null ? (char) READER.read() : windowsBufferChar;

        while (true) {
            nextCharacter = (char) READER.read();

            if (notEnclosedCRLF()) {
                // Saves next char that is already read into buffer so it won't be nullified in next iteration
                windowsBufferChar = nextCharacter;
                break;
            }

            if (isCR(currentCharacter)) {
                // CR Symbol is skipped if not in enclosed field
                charStep();
                continue;
            }

            routeCurrentChar();
            overflowValidation();
            charStep();

            boolean isEOF = !ready();
            if (isLF(currentCharacter) || isEOF) {
                // EOF with no special states
                if (isEOF && isRegularCharacter(currentCharacter)) {
                    cell.append(currentCharacter);
                }

                // State change in case it is LF
                if (previousCharacter == ENCLOSURE && isLF(currentCharacter)) {
                    enclosedField = false;
                }
                if (currentCharacter == ENCLOSURE && enclosedField) {
                    enclosedField = false;
                }

                // Pure EOF case I suppose
                break;
            }
        }

        if (enclosedField && currentCharacter != ENCLOSURE) {
            cell.append(currentCharacter);
            charStep();
            readRow();
        }

        if (previousCharacter == DELIMITER || !cell.isEmpty()) {
            appendCell();
        }

        try {
            validateStructure();
        } catch (CsvReadingException e) {
            e.getCustomMessage();
        }

        return row;
    }

    private boolean isRegularCharacter(Character character) {
        return character != ESCAPE && character != DELIMITER && (character != ENCLOSURE || !enclosedField);
    }

    private boolean notEnclosedCRLF() {
        return isCR(previousCharacter) && isLF(currentCharacter) && !enclosedField;
    }

    private boolean isCR(Character character) {
        return CR == character;
    }

    private boolean isLF(Character character) {
        return LF == character;
    }

    private void routeCurrentChar() throws CsvReadingException, IOException {
        if (currentCharacter == DELIMITER) {
            processDelimiter();
        } else if (currentCharacter == ENCLOSURE) {
            processEnclosureSymbol();
        } else if (currentCharacter == ESCAPE) {
            processEscapeSymbol();
        } else {
            cell.append(currentCharacter);
        }
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
            if (SAME_ENC_ESC && !escapeActive && checkEscape()) {
                return;
            }
            if (escapeActive) {
                processEscapeSymbol();
            } else if (nextCharacter == DELIMITER || nextCharacter == LF || nextCharacter == CR) {
                enclosedField = false;
            } else if (!ready()) {
                appendCell();
            } else {
                throw EXCEPTION_FACTORY.getException(Cause.ENCLOSURE, getLog());
            }
        } else {
            if (previousCharacter == null || previousCharacter == DELIMITER) {
                enclosedField = true;
            } else {
                throw EXCEPTION_FACTORY.getException(Cause.ENCLOSURE, getLog());
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
            throw EXCEPTION_FACTORY.getException(Cause.LARGE_LINE, getLog());
        }
    }

    private void validateStructure() throws CsvReadingException {
        int rowSize = row.size();
        if (symmetryCheck == 0) {
            symmetryCheck = rowSize;
        } else if (symmetryCheck != rowSize) {
            throw EXCEPTION_FACTORY.getException(Cause.COLUMNS, getLog());
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
        return READER.available() > 0;
    }

    @Override
    public void close() throws IOException {
        READER.close();
    }
}
