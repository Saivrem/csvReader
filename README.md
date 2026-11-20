# csvReader

Version **1.9.1**.

A streaming CSV reader with configurable delimiter, enclosure and escape characters.
Read each row as a `List<String>`, or optionally map rows to annotated Java objects.
Both readers implement `AutoCloseable` and close their input stream.

## Build and test

Use JDK 17 and the checked-in Gradle wrapper:

```sh
./gradlew clean test build
```

The library JAR is written to `build/libs/`. The wrapper uses Gradle 7.6.

## Read rows

```java
import exceptions.CsvReadingException;
import reader.CsvReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReadRows {
    public static void main(String[] args) throws IOException, CsvReadingException {
        try (CsvReader reader = new CsvReader(
                Files.newInputStream(Path.of("input.csv")), ',', '"', '\\')) {
            while (reader.hasNext()) {
                List<String> row = reader.readRow();
                System.out.println(row);
            }
        }
    }
}
```

Constructor arguments are the input stream, delimiter, enclosure and escape,
in that order. Enclosure and escape are configurable characters, not necessarily
quotation marks, and either can be `null` to disable it. For example, use
`new CsvReader(stream, ';', '|', '!')` for semicolon-separated fields enclosed by
`|` and escaped with `!`. Enclosed fields can span physical lines.

`hasNext()` checks the input stream and buffers the next byte when necessary.
Repeated checks do not skip data. It may block or throw `IOException`; it does not
use `InputStream.available()` to determine EOF. Empty input returns `false`.
After the final row, a trailing LF or CRLF does not create an extra EOF row.
Calling `readRow()` after EOF returns an empty list.

Process rows incrementally when reading large files rather than retaining the
whole document in memory.

## Optional object mapping

`ObjectCsvReader<T>` uses the same row parser and maps each row into a new object.
Annotate the DTO class with `@CsvRow` and each declared field with `@CsvMapping`.
The DTO needs an accessible no-argument constructor. Use a public top-level class
or a public static nested class, as in this example.

For a file with a header:

```csv
id,name,status,tags
1,Alice,ACTIVE,reader;editor
2,Bob,inactive,reader
```

```java
import annotation.CsvMapping;
import annotation.CsvRow;
import exceptions.CsvReadingException;
import reader.ObjectCsvReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReadObjects {
    public enum Status { ACTIVE, INACTIVE }

    @CsvRow
    public static class Person {
        @CsvMapping(name = "id")
        private int id;

        @CsvMapping(name = "name")
        private String name;

        @CsvMapping(name = "status")
        private Status status;

        @CsvMapping(name = "tags", collection = true, separator = ";")
        private List<String> tags;
    }

    public static void main(String[] args) throws IOException, CsvReadingException {
        try (ObjectCsvReader<Person> reader = new ObjectCsvReader<>(
                Files.newInputStream(Path.of("people.csv")),
                ',', '"', '\\', true, Person.class)) {
            while (reader.hasNext()) {
                Person person = reader.readNext();
                System.out.println(person.name + ": " + person.tags);
            }
        }
    }
}
```

The final constructor arguments select whether the first row is a header and
specify the DTO class. With `true`, the constructor reads the header and fields
are matched by `name`. Header names are matched exactly. Each data row must have
the same number of columns as the header.

For a file **without a header**, pass `false` and map by zero-based position:

```java
@CsvRow
public static class Person {
    @CsvMapping(ordinal = 0)
    private int id;

    @CsvMapping(ordinal = 1)
    private String name;
}
```

Choose `name` or `ordinal` for each field; do not set both. The current header mode
uses names, while the mode without a header uses ordinals.

### Supported conversions

- `String`, `int`/`Integer`, `long`/`Long`, `double`/`Double`, `boolean`/`Boolean`.
- Enum constants, with case-insensitive matching as a fallback.
- `List<T>` and `Set<T>` with a concrete supported element class, using
  `collection = true`. Lists are created as `ArrayList`, sets as `HashSet`.

Values are trimmed before conversion, including strings and collection elements.
Boolean conversion follows `Boolean.parseBoolean`: only `true`, ignoring case,
produces `true`. Numeric and enum values must be valid for their target types.

Collection `separator` defaults to `;` and is a Java regular expression passed to
`String.split`. Regex metacharacters need escaping; trailing empty elements are
discarded. Mapping configuration and conversion failures are reported as runtime
exceptions; stream failures are reported as `IOException`.

## Current limitations

The reader currently casts individual input bytes to characters; it does not
accept a charset or decode multibyte encodings. Non-ASCII text, including UTF-8
and UTF-16 input, is not reliably supported.

The parser exposes `CsvReadingException` subclasses for parsing errors, including
invalid enclosure placement and the configured line-length limit. Its current
validation does not constitute strict CSV-specification validation. Object mapping
is optional and does not replace the underlying row parser.
