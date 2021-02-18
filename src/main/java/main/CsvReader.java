package main;

import exceptions.BrokenCsvStructureException;

import java.io.*;
import java.util.LinkedList;

public class CsvReader implements AutoCloseable {

    /**
     * Initial settings
     */

    private final Character delimiter;
    private final Character escapeChar;
    private int symmetryCheck = 0;

    /**
     * Read Line global variables;
     */
    private Character currentChar;
    private LinkedList<String> cells;
    private StringBuilder cell;
    private Character nextChar;
    private Character previousChar;
    boolean escapedField;

    boolean stepForward = false;


    // TODO Make this optional, need to Extend io.Reader and Wrap it with BufferedReader On demand.
    private final BufferedReader reader;

    public CsvReader(BufferedReader fileReader, Character delimiter, Character escapeChar) {
        this.delimiter = delimiter;
        this.escapeChar = escapeChar;
        this.reader = fileReader;
    }

    /**
     * Reads one row from CSV file and returns it as LinkedList of Strings
     * @return LinkedList&gt;String&lt;
     * @throws BrokenCsvStructureException custom exception, see exceptions.BrokenCsvStrictureException
     * @throws IOException I\O Exception
     */
    public LinkedList<String> readLine() throws BrokenCsvStructureException, IOException {

        String line = reader.readLine();

        if (escapedField && line == null) {
            return null;
        }

        if (!escapedField) {
            cells = new LinkedList<>();
            cell = new StringBuilder();
        }

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
            if (reader.ready()) {
                cell.append("\n");
                LinkedList<String> marker = readLine();
                if (marker == null) {
                    cells.add(cell.toString().replaceAll("\n", ""));
                    throw new BrokenCsvStructureException(
                            line,
                            line.lastIndexOf(escapeChar),
                            symmetryCheck,
                            cells.size(),
                            cells.toString(),
                            BrokenCsvStructureException.Cause.STRUCTURE
                    );
                }
            } else {
                throw new BrokenCsvStructureException(line,
                        line.lastIndexOf(escapeChar),
                        symmetryCheck,
                        cells.size(),
                        cells.toString(),
                        BrokenCsvStructureException.Cause.ESCAPE);
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
                throw new BrokenCsvStructureException(line,
                        i,
                        symmetryCheck,
                        cells.size(),
                        cells.toString(),
                        BrokenCsvStructureException.Cause.ESCAPE);
            }
        } else {
            if (nextChar != null) {
                if (nextChar.equals(escapeChar)) {
                    cell.append(currentChar);
                    stepForward = true;
                } else if (nextChar.equals(delimiter)) {
                    escapedField = false;
                } else {
                    throw new BrokenCsvStructureException(
                            line,
                            i,
                            symmetryCheck,
                            cells.size(),
                            cells.toString(),
                            BrokenCsvStructureException.Cause.ESCAPE
                    );
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
            throw new BrokenCsvStructureException(line,
                    null,
                    symmetryCheck,
                    cells.size(),
                    cells.toString(),
                    BrokenCsvStructureException.Cause.COLUMNS
            );
        }
    }

    /**
     * Returns Ready status of reader object
     *
     * @return reader.ready()
     * @throws IOException original method is doing the same.
     */
    public boolean ready() throws IOException {
        return reader.ready();
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
