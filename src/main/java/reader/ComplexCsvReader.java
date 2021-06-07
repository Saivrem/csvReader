package reader;

import exceptions.CsvReadingException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class ComplexCsvReader extends CsvReader {

    private final char escape, enclosure;

    protected ComplexCsvReader(InputStreamReader reader, Character delimiter, Character escape, Character enclosure) {
        super(reader, delimiter);
        this.escape = escape;
        this.enclosure = enclosure;
    }

    @Override
    public LinkedList<String> readRow() throws CsvReadingException, IOException {
        prepareFields();
        return null;
    }

    @Override
    protected void processDelimiter() {

    }

    private void processEnclosure() {
    }

    private boolean isEscaped() {
        return false;
    }

}
