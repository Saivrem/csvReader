# A simple how-to
```java
import exceptions.BrokenCsvStructureException;

import java.io.*;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        //First - BufferedReader with CSV, second - delimiter, third - escape (nullable)
        try (CsvReader reader = new CsvReader(
                new BufferedReader(new FileReader("/home/user/test.csv")),
                ',',   // delimiter
                null   // escape
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
