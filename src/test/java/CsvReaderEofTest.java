import org.junit.jupiter.api.Test;
import reader.CsvReader;
import reader.ObjectCsvReader;
import model.NameDTO;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderEofTest {
    @Test
    void shouldRecognizeEmptyInputAndRepeatedReadsAfterEof() throws Exception {
        try (CsvReader reader = reader("")) {
            assertFalse(reader.hasNext());
            assertEquals(List.of(), reader.readRow());
            assertFalse(reader.hasNext());
            assertEquals(List.of(), reader.readRow());
        }
    }

    @Test
    void shouldPreserveRowsAcrossRepeatedChecksAndLineEndings() throws Exception {
        for (String ending : List.of("\n", "\r\n")) {
            for (String suffix : List.of("", ending)) {
                try (CsvReader reader = reader("a,b" + ending + "c,d" + suffix)) {
                    assertTrue(reader.hasNext());
                    assertTrue(reader.hasNext());
                    assertEquals(List.of("a", "b"), reader.readRow());
                    assertTrue(reader.hasNext());
                    assertTrue(reader.hasNext());
                    assertEquals(List.of("c", "d"), reader.readRow());
                    assertFalse(reader.hasNext());
                    assertFalse(reader.hasNext());
                }
            }
        }
    }

    @Test
    void shouldReadWithoutCallingHasNextFirst() throws Exception {
        try (CsvReader reader = reader("a,b")) {
            assertEquals(List.of("a", "b"), reader.readRow());
            assertEquals(List.of(), reader.readRow());
        }
    }

    @Test
    void shouldNotUseAvailableToDetermineEof() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("a,b".getBytes(StandardCharsets.US_ASCII)) {
            @Override
            public int available() {
                return 0;
            }
        };
        try (CsvReader reader = new CsvReader(stream, ',', null, null)) {
            assertTrue(reader.hasNext());
            assertEquals(List.of("a", "b"), reader.readRow());
            assertFalse(reader.hasNext());
        }
    }

    @Test
    void shouldPreserveCustomEnclosureAndEscapeResults() throws Exception {
        try (CsvReader reader = new CsvReader(
                new ByteArrayInputStream("|a,b!|c|,d".getBytes(StandardCharsets.US_ASCII)), ',', '|', '!')) {
            assertTrue(reader.hasNext());
            assertEquals(List.of("a,b|c", "d"), reader.readRow());
            assertFalse(reader.hasNext());
        }
    }

    @Test
    void shouldPreserveEnclosedFieldAtEof() throws Exception {
        try (CsvReader reader = reader("\"a")) {
            assertEquals(List.of("a"), reader.readRow());
            assertFalse(reader.hasNext());
        }
    }

    @Test
    void shouldPreserveEmptyEnclosedRowResult() throws Exception {
        try (CsvReader reader = reader("\"\"\n")) {
            assertEquals(List.of(), reader.readRow());
            assertFalse(reader.hasNext());
        }
    }

    @Test
    void shouldReadMappedRowsUntilEof() throws Exception {
        String input = "id,name,sex,age\n1,Alice,F,20\n2,Bob,M,30\n";
        try (ObjectCsvReader<NameDTO> reader = new ObjectCsvReader<>(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII)),
                ',', '"', '\\', true, NameDTO.class)) {
            assertTrue(reader.hasNext());
            assertTrue(reader.hasNext());
            assertEquals("Alice", reader.readNext().getName());
            assertTrue(reader.hasNext());
            assertEquals("Bob", reader.readNext().getName());
            assertFalse(reader.hasNext());
        }
    }

    @Test
    void shouldRecognizeHeaderOnlyInput() throws Exception {
        try (ObjectCsvReader<NameDTO> reader = new ObjectCsvReader<>(
                new ByteArrayInputStream("id,name,sex,age\n".getBytes(StandardCharsets.US_ASCII)),
                ',', '"', '\\', true, NameDTO.class)) {
            assertFalse(reader.hasNext());
        }
    }

    private CsvReader reader(String input) {
        return new CsvReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII)), ',', '"', '\\');
    }
}
