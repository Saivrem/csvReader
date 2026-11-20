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
        loadAndAssertMultiline(expectedPath, actualPath, DELIMITER, ENCLOSURE, ESCAPE);
    }

    protected void loadAndAssertMultiline(String expectedPath, String actualPath,
                                         Character delimiter, Character enclosure, Character escape) {
        List<List<String>> expected = loadListOfExpectedResults(expectedPath);
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(actualPath)), delimiter, enclosure, escape)) {
            List<List<String>> actual = readAllRows(reader);
            Assertions.assertEquals(expected, actual);
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
    }

    protected void loadAndAssertDirectReads(String expectedPath, String actualPath) {
        List<List<String>> expected = loadListOfExpectedResults(expectedPath);
        List<List<String>> actual = new ArrayList<>();
        List<String> exhaustedRow = List.of();
        if (expected == null) Assertions.fail("No expected results provided");
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(actualPath)), DELIMITER, ENCLOSURE, ESCAPE)) {
            for (int i = 0; i < expected.size(); i++) {
                actual.add(reader.readRow());
            }
            Assertions.assertEquals(expected, actual);
            Assertions.assertEquals(exhaustedRow, reader.readRow());
            Assertions.assertFalse(reader.hasNext());
            Assertions.assertEquals(exhaustedRow, reader.readRow());
            Assertions.assertFalse(reader.hasNext());
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
    }

    protected void loadAndAssertWithZeroAvailable(String expectedPath, String actualPath) {
        List<List<String>> expected = loadListOfExpectedResults(expectedPath);
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(actualPath)) {
            @Override
            public int available() {
                return 0;
            }
        }, DELIMITER, null, null)) {
            List<List<String>> actual = readAllRows(reader);
            Assertions.assertEquals(expected, actual);
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
    }

    protected <T> void loadAndAssertMultipleMappings(String expectedPath, String actualPath,
                                                    Class<T> typeReference, boolean firstLineHeader) {
        List<T> actual = new ArrayList<>();
        try (FileInputStream expectedStream = new FileInputStream(resolvePath(expectedPath));
             ObjectCsvReader<T> reader = new ObjectCsvReader<>(new FileInputStream(resolvePath(actualPath)),
                     DELIMITER, ENCLOSURE, ESCAPE, firstLineHeader, typeReference)) {
            List<T> expected = objectMapper.readValue(expectedStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, typeReference));
            while (reader.hasNext()) {
                Assertions.assertTrue(reader.hasNext());
                actual.add(reader.readNext());
            }
            Assertions.assertFalse(reader.hasNext());
            Assertions.assertEquals(expected, actual);
        } catch (IOException | CsvReadingException e) {
            Assertions.fail(e.getMessage());
        }
    }

    private List<String> loadPlainCsvReaderActualResult(String resource) {
        List<String> strings = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new FileInputStream(resolvePath(resource)), DELIMITER, ENCLOSURE, ESCAPE)) {
            while (reader.hasNext()) {
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

    private List<List<String>> readAllRows(CsvReader reader) throws IOException, CsvReadingException {
        List<List<String>> rows = new ArrayList<>();
        while (reader.hasNext()) {
            Assertions.assertTrue(reader.hasNext());
            rows.add(reader.readRow());
        }
        Assertions.assertFalse(reader.hasNext());
        return rows;
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
