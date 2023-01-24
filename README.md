# Read me
#### Version 1.0; Basic csv row parser

```java
import exceptions.CsvReadingException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    /**
     * These params could be customized;
     */
    private final static Character DELIMITER = ',';
    private final static Character ENCLOSURE = '"';
    private final static Character ESCAPE = '\\';

    public static void main(String[] args) {
        try (CsvReader reader = new CsvReader(
                new InputStream("pathToFile"),
                DELIMITER,
                ENCLOSURE,
                EXCAPE
        )) {
            List<List<String>> document = new ArrayList<>();
            while (reader.ready()) {
                document.add(reader.readRow());
            }
            //Now you have all rows in one List;
            //Be aware of OOM, better to process rows 
            //one by one;
        } catch (IOException | CsvReadingException exception) {
            exception.printStackTrace();
        }
    }
}
```