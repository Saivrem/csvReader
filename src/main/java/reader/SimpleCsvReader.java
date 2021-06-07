package reader;

import exceptions.CsvReadingException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class SimpleCsvReader extends CsvReader {

    public SimpleCsvReader(InputStreamReader reader, Character delimiter) {
        super(reader, delimiter);
    }

    @Override
    public LinkedList<String> readRow() throws CsvReadingException, IOException {
        prepareFields();

        while ((currentCharacter = (char) reader.read()) != '\n') {
            if (currentCharacter == '\r') {
                continue;
            }
            if (currentCharacter != delimiter) {
                cell.append(currentCharacter);
            } else {
                processDelimiter();
            }

            overflowValidation();
            previousCharacter = currentCharacter;
            if (!ready()) {
                break;
            }
        }

        if (previousCharacter == delimiter || cell.length() > 0) {
            processDelimiter();
        }

        validateStructure();

        return row;
    }

    @Override
    protected void processDelimiter() {
        row.add(cell.toString());
        cell = new StringBuilder();
    }

}
