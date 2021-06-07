package exceptions;

public class LineIsTooLongException extends CsvReadingException {

    public static double LINE_LIMIT_SIZE = 2097152;

    protected LineIsTooLongException(int row) {
        super(row);
        customMessage.append(String.format("Line is larger than %.2f Mb\n", LINE_LIMIT_SIZE / 1024 / 1024));
    }
}
