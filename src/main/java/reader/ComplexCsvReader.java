package reader;

import enums.Cause;
import exceptions.CsvReadingException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class ComplexCsvReader extends CsvReader {

    private final char escape, enclosure;

    private boolean enclosedField = false;
    private boolean escapeActive = false;

    public ComplexCsvReader(InputStreamReader reader, Character delimiter, Character escape, Character enclosure) {
        super(reader, delimiter);
        this.escape = escape;
        this.enclosure = enclosure;
    }

    @Override
    public LinkedList<String> readRow() throws CsvReadingException, IOException {

        if (!enclosedField) {
            prepareFields();
        }
        currentCharacter = (char) reader.read();

        while (currentCharacter != '\n') {
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

            if (!ready()) {
                if (currentCharacter == enclosure) {
                    processEnclosureSymbol();
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
            cell.append(currentCharacter);
            processDelimiter();
        }

        validateStructure();

        return row;
    }

    @Override
    protected void processDelimiter() {
        if (!enclosedField) {
            row.add(cell.toString());
            cell = new StringBuilder();
        } else {
            cell.append(currentCharacter);
        }
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
        return previousCharacter == escape && escapeActive;
    }

}
