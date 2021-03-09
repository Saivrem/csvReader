package main;

import enums.Cause;
import exceptions.BrokenCsvStructureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Utility class, works similarly to Reader but with CSV files.
 */
public class CsvReader implements AutoCloseable {

    /**
     * FileReader
     */
    // TODO Make this optional, need to Extend io.Reader and Wrap it with BufferedReader On demand.
    private final BufferedReader fileReader;
    /**
     * Delimiter
     */
    private final Character delimiter;
    /**
     * Escape
     */
    private final Character escapeChar;

    // Below will be readLine() global variables;
    /**
     * Row
     */
    private LinkedList<String> cells;
    /**
     * Cell
     */
    private StringBuilder cell;

    //Characters
    private Character currentChar;
    private Character nextChar;
    private Character previousChar;

    //logical flags
    private boolean escapedField = false;
    private boolean stepForward = false;

    /**
     * Number of cells in a header, user to ensure all rows have same number of cells;
     * Otherwise exception will be thrown.
     */
    protected int symmetryCheck = 0;


    /**
     * Constructor
     *
     * @param fileReader BufferedReader Object
     * @param delimiter  Character or char for delimiting symbol
     * @param escapeChar Character or char for escape symbol
     */
    public CsvReader(BufferedReader fileReader, Character delimiter, Character escapeChar) {
        this.delimiter = delimiter;
        this.escapeChar = escapeChar;
        this.fileReader = fileReader;
    }

    /**
     * Reads one row from CSV file and returns it as LinkedList of Strings
     *
     * @return LinkedList&lt;String&gt;
     * @throws BrokenCsvStructureException custom exception, see exceptions.BrokenCsvStrictureException
     * @throws IOException                 I\O Exception
     */
    public LinkedList<String> readLine() throws BrokenCsvStructureException, IOException {

        String line = fileReader.readLine();

        if (!escapedField) {
            cells = new LinkedList<>();
            cell = new StringBuilder();
        } else if (line == null) {
            cells.add(cell.toString());
            throwException(null, 0, Cause.STRUCTURE);
        }

        assert line != null;
        char[] array = line.toCharArray();

        for (int i = 0; i < array.length; i++) {

            currentChar = array[i];
            nextChar = i < array.length - 1 ? array[i + 1] : null;
            previousChar = i > 0 ? array[i - 1] : null;

            if (currentChar == delimiter) {
                processDelimiter();
            } else if (escapeChar != null && currentChar == escapeChar) {
                processEscapeCharacter(i, line);
                if (stepForward) {
                    stepForward = false;
                    i++;
                }
            } else {
                cell.append(currentChar);
            }
        }

        if (escapedField) {
            if (fileReader.ready()) {
                cell.append("\n");
                LinkedList<String> marker = readLine();
                if (marker == null) {
                    cells.add(cell.toString().replaceAll("\n", ""));
                    throwException(line, line.lastIndexOf(escapeChar), Cause.STRUCTURE);
                }
            } else {
                throwException(line, line.lastIndexOf(escapeChar), Cause.ESCAPE);
            }
        } else if ((previousChar != null && previousChar.equals(delimiter)) ||
                (cell.toString().length() > 0 && !(currentChar.equals(escapeChar)))) {
            cells.add(cell.toString());
        }

        validateStructure(line);

        return cells;
    }

    /**
     * Processor for delimiting character;
     */
    private void processDelimiter() {
        if (!escapedField) {
            cells.add(cell.toString());
            cell = new StringBuilder();
        } else {
            cell.append(currentChar);
        }
    }

    /**
     * As the name says, processor for escape character;
     *
     * @param i    number of character
     * @param line full string, used in exception
     * @throws BrokenCsvStructureException Custom exception, represents the broken structure
     *                                     line which produced it and the character number in
     *                                     this line
     */
    private void processEscapeCharacter(int i, String line) throws BrokenCsvStructureException {
        if (!escapedField) {
            if (i == 0 || (previousChar.equals(delimiter))) {
                escapedField = true;
            } else {
                throwException(line, i, Cause.ESCAPE);
            }
        } else {
            if (nextChar != null) {
                if (nextChar.equals(escapeChar)) {
                    cell.append(currentChar);
                    stepForward = true;
                } else if (nextChar.equals(delimiter)) {
                    escapedField = false;
                } else {
                    throwException(line, i, Cause.ESCAPE);
                }
            } else {
                cells.add(cell.toString());
                escapedField = false;
            }
        }
    }

    /**
     * Checking if current row has the same columns number with the previous, this prevents the possibility
     * to broke structure of the document
     *
     * @param line only used in Exception method;
     * @throws BrokenCsvStructureException Custom exception with info what line produced the fail, alas
     *                                     I have no Idea how to point the actual place in the string which is broken.
     */
    private void validateStructure(String line) throws BrokenCsvStructureException {
        if (symmetryCheck == 0) {
            symmetryCheck = cells.size();
        } else if (symmetryCheck != cells.size()) {
            throwException(line, null, Cause.COLUMNS);
        }
    }

    /**
     * Returns Ready status of reader object
     *
     * @return reader.ready()
     * @throws IOException original method is doing the same.
     */
    public boolean ready() throws IOException {
        return fileReader.ready();
    }

    /**
     * Support for autocloseable interface;
     */
    @Override
    public void close() {
        try {
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Single endpoint to exception throwing
     * @param line line caused exception
     * @param charIndex index of invalid character if possible to detect
     * @param cause enum of Cause
     * @throws BrokenCsvStructureException this method is supposed to throw one
     */
    private void throwException(String line, Integer charIndex, Cause cause) throws BrokenCsvStructureException {
        escapedField = false;
        stepForward = false;
        throw new BrokenCsvStructureException(line,
                charIndex,
                symmetryCheck, cells.size(),
                cells.toString(),
                cause);
    }
}