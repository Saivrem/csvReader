import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.CsvReadingException;
import org.junit.jupiter.api.Assertions;
import reader.CsvReader;
import reader.ObjectCsvReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    protected static char DELIMITER = ',';
    protected static char ESCAPE = '\\';
    protected static char ENCLOSURE = '"';

    protected void loadAndAssert(String expectedPath, String actualPath) {
        List<String> expected = loadExpectedResult(expectedPath);
        List<String> actual = loadPlainCsvReaderActualResult(actualPath);
        Assertions.assertEquals(expected, actual);
    }

    protected <T> void loadAndAssertWithMappings(String expectedPath, String actualPath, Class<T> typeReference, boolean firstLineHeader) {
        T expected = loadExpectedResultDto(expectedPath, typeReference);
        T actual = loadMappedActualResult(actualPath, typeReference, firstLineHeader);
        Assertions.assertEquals(expected, actual);
    }

    protected void loadAndAssertMultiline(String expectedPath, String actualPath) {
        List<List<String>> expected = loadListOfExpectedResults(expectedPath);
        List<List<String>> actual = loadListOfActualResults(actualPath);
        Assertions.assertEquals(expected, actual);
    }

    private List<String> loadPlainCsvReaderActualResult(String resource) {
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

    private <T> T loadMappedActualResult(String resource, Class<T> typeReference, boolean firstLineHeader) {
        try (ObjectCsvReader<T> reader = new ObjectCsvReader<>(new FileInputStream(resolvePath(resource)), DELIMITER, ENCLOSURE, ESCAPE, firstLineHeader, typeReference)) {
            return reader.readNext();
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private List<List<String>> loadListOfActualResults(String resource) {
        List<List<String>> miltiline = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(resource)), DELIMITER, ENCLOSURE, ESCAPE)) {
            while (reader.ready()) {
                miltiline.add(reader.readRow());
            }
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
        return miltiline;
    }

    private List<String> loadExpectedResult(String resource) {
        try {
            return objectMapper.readValue(new FileInputStream(resolvePath(resource)), new TypeReference<>() {
            });
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private <T> T loadExpectedResultDto(String resource, Class<T> typeRef) {
        try {
            return objectMapper.readValue(new FileInputStream(resolvePath(resource)), typeRef);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private List<List<String>> loadListOfExpectedResults(String resource) {
        try {
            return objectMapper.readValue(new FileInputStream(resolvePath(resource)), new TypeReference<>() {
            });
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private String resolvePath(String relative) {
        return "src/test/resources/" + relative;
    }
}
