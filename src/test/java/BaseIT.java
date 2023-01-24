import exceptions.CsvReadingException;
import org.junit.jupiter.api.Assertions;
import reader.CsvReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseIT {

    protected static char DELIMITER = ',';
    protected static char ESCAPE = '\\';
    protected static char ENCLOSURE = '"';

    protected void loadAndAssert(String expectedPath, String actualPath) {
        List<String> expected = loadExpectedResult(expectedPath);
        List<String> actual = loadActualResult(actualPath);

        Assertions.assertEquals(expected, actual);
    }

    private List<String> loadActualResult(String resource) {
        List<String> strings = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(resource)), DELIMITER, ENCLOSURE, ESCAPE)) {
            while (reader.ready()) {
                strings = reader.readRow();
            }
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
        return strings;
    }

    private List<String> loadExpectedResult(String resource) {
        List<String> expected = new ArrayList<>();
        try (BufferedReader expectedReader = new BufferedReader(new FileReader(resolvePath(resource)))) {
            while (expectedReader.ready()) {
                expected.add(expectedReader.readLine());
            }
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return expected;
    }

    private String resolvePath(String relative) {
        return "src/test/resources/" + relative;
    }
}
