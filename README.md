# A simple how-to
```java
import exceptions.BrokenCsvStructureException;

import java.io.*;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        try (CsvReader reader = new CsvReader(
                       // File to read
                new BufferedReader(new FileReader("/home/user/test.csv")),
                ',',   // Delimiter
                null   // Escape
        )) {
            while (reader.ready()) {
                LinkedList<String> row = reader.readLine();
                //Do some code here
                System.out.println(row.toString());
            }
        } catch (IOException | BrokenCsvStructureException exception) {
            exception.printStackTrace();
        }
    }
}
```
