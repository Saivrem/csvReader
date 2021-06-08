package reader;

import java.util.List;

public class ExceptionLogDTO {
    private final int rowNumber, character;
    private final List<String> row;

    public ExceptionLogDTO(int rowNumber, int character, List<String> row) {
        this.rowNumber = rowNumber;
        this.character = character;
        this.row = row;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getCharacter() {
        return character;
    }

    public List<String> getRow() {
        return row;
    }
}
