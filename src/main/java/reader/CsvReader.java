package reader;

import enums.Cause;
import exceptions.CsvReadingException;
import exceptions.CsvReadingExceptionFactory;
import exceptions.LineIsTooLongException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public abstract class CsvReader implements AutoCloseable {

    //service fields
    protected int rowsProcessed = 1, overflowProtection = 0, symmetryCheck = 0;
    protected final InputStreamReader reader;
    protected final Character delimiter;
    protected Character previousCharacter, currentCharacter, nextCharacter;

    protected CsvReadingExceptionFactory factory = CsvReadingExceptionFactory.getInstance();

    protected StringBuilder cell;
    protected LinkedList<String> row;

    protected CsvReader(InputStreamReader reader, Character delimiter) {
        this.delimiter = delimiter;
        this.reader = reader;
    }

    public abstract LinkedList<String> readRow() throws CsvReadingException, IOException;

    protected abstract void processDelimiter();

    public boolean ready() throws IOException {
        return reader.ready();
    }

    protected void overflowValidation() throws CsvReadingException {
        if (overflowProtection <= LineIsTooLongException.LINE_LIMIT_SIZE) {
            overflowProtection++;
        } else {
            throw factory.getException(Cause.LARGE_LINE, rowsProcessed);
        }
    }

    protected void prepareFields() {
        overflowProtection = 0;
        row = new LinkedList<>();
        cell = new StringBuilder();
    }

    protected void validateStructure() throws CsvReadingException {
        int rowSize = row.size();
        if (symmetryCheck == 0) {
            symmetryCheck = rowSize;
        } else if (symmetryCheck != rowSize) {
            throw factory.getException(Cause.COLUMNS, rowsProcessed);
        }
        rowsProcessed++;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
