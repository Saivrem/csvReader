# csvReader

Version **1.9.2**. Read CSV rows as `List<String>` or optionally map them to Java objects.

## Read rows

```java
try (CsvReader reader = new CsvReader(
        Files.newInputStream(Path.of("input.csv")), ',', '"', '\\')) {
    while (reader.hasNext()) {
        List<String> row = reader.readRow();
        System.out.println(row);
    }
}
```

- `CsvReader(InputStream, delimiter, enclosure, escape)` accepts configurable characters;
  `enclosure` and `escape` may be `null`. Enclosed fields may span lines.
- `hasNext()` returns `false` at EOF. Repeated calls preserve the next row;
  reading from the stream may block or throw `IOException`.
- `readRow()` returns the next row, or an empty list after EOF, and may throw
  `IOException` or `CsvReadingException`.
- `close()` closes the input stream.

## Optional object mapping

Annotate the class with `@CsvRow` and each declared field with `@CsvMapping`.
Use a public class with an accessible no-argument constructor.

```csv
id,name,status,tags
1,Alice,ACTIVE,reader;editor
```

```java
@CsvRow
public class Person {
    public enum Status { ACTIVE, INACTIVE }

    @CsvMapping(name = "id")
    public int id;

    @CsvMapping(name = "name")
    public String name;

    @CsvMapping(name = "status")
    public Status status;

    @CsvMapping(name = "tags", collection = true, separator = ";")
    public List<String> tags;
}
```

```java
try (ObjectCsvReader<Person> reader = new ObjectCsvReader<>(
        Files.newInputStream(Path.of("people.csv")),
        ',', '"', '\\', true, Person.class)) {
    while (reader.hasNext()) {
        Person person = reader.readNext();
        System.out.println(person.name);
    }
}
```

- `ObjectCsvReader` adds `firstRowHeader` and the DTO class to the row-reader constructor.
  `readNext()` maps one row to a new object; call it while `hasNext()` is `true`.
- With `firstRowHeader = true`, the constructor reads the header and `name` matches
  a column exactly. Data rows must have the same number of columns as the header.
- Without a header, pass `false` and use zero-based positions, e.g.
  `@CsvMapping(ordinal = 0)`. Set either `name` or `ordinal` on each field.
- Supported values: `String`, `int`, `long`, `double`, `boolean`, their wrappers,
  and enums (case-insensitive). Values are trimmed before conversion;
  booleans follow `Boolean.parseBoolean`.
- `collection = true` maps `List<T>` or `Set<T>` with a supported element class.
  `separator` defaults to `;` and follows `String.split` regex rules.
- Mapping and conversion errors are runtime exceptions.

Imports: `reader.CsvReader`, `reader.ObjectCsvReader`, `annotation.CsvRow`,
`annotation.CsvMapping`, `exceptions.CsvReadingException`, plus the standard
`java.io`, `java.nio.file` and `java.util` types used above.

## Build

JDK 17:

```sh
./gradlew clean test build
```

JAR: `build/libs/`.

## License

Licensed under the [Apache License 2.0](LICENSE).
