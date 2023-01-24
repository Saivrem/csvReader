package reader;

import java.util.List;

public record ExceptionLog(int rowNumber, int character, List<String> row) {

}
