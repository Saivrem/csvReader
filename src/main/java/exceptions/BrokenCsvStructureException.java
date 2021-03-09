package exceptions;

import enums.Cause;

/**
 * Custom exception, could be caused by different CSV issues;
 * Broken Escaped fields;
 * Broken document structure (number of columns is different);
 */
public class BrokenCsvStructureException extends Exception {

    private static final String ESCAPE_CHAR_PATTERN = "Escape character in unexpected place:\n%s\nCharacter Pos: %d";
    private static final String COL_NUM_PATTERN = "Expected columns number is %d\nActual is %d\n\nLine: %s\nCells: %s\n";
    private static final String STRUCTURE_PATTERN = "Structure is broken, escaped field is not closed properly\n" +
            "Line: %s\nLast index of delimiter: %d\nRow read: %s\n";

    String customMessage;

    /**
     *
     * @param line actual
     * @param charIndex index of broken character
     * @param symmetryCheck expected number of columns
     * @param rowSize actual number of columns in a row
     * @param row row as it is
     * @param cause enum cause
     */
    public BrokenCsvStructureException(String line, Integer charIndex, int symmetryCheck, int rowSize, String row, Cause cause) {
        switch (cause) {
            case ESCAPE:
                this.customMessage = String.format(ESCAPE_CHAR_PATTERN, line, charIndex);
                break;
            case COLUMNS:
                this.customMessage = String.format(COL_NUM_PATTERN, symmetryCheck, rowSize, line, row);
                break;
            case STRUCTURE:
                this.customMessage = String.format(STRUCTURE_PATTERN, line, charIndex, row);
                break;
        }
    }

    /**
     * Getter for custom message;
     * @return String with custom message;
     */
    public String getCustomMessage() {
        return customMessage;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getCustomMessage());
    }
}
