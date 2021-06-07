#Read me
####Version 1.5 (OOP_Rework_WIP);

MVP for future major upgrade;

By now only Simple reader implemented;

All functionality is new and not covered with test cases, 
use on your own risk.

Legacy classes are still available, yet deprecated;

New example:

```java
import exceptions.CsvReadingException;
import reader.SimpleCsvReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws Exception {
        Path simpleCsvFile = Paths.get("/home/user/file.csv");
        try (SimpleCsvReader reader = new SimpleCsvReader(new InputStreamReader(new FileInputStream(simpleCsvFile.toFile())), ',')) {
            while (reader.ready()) {
                LinkedList<String> strings = reader.readRow();
                for (String str : strings) {
                    System.out.println(str);
                }
            }
        } catch (IOException | CsvReadingException e) {
            e.printStackTrace();
        }
    }
}
```

####Version 1.2 Legacy classes

Old classes appended with "Legacy" prefix
and marked as "Deprecated".

Old example:

```java
//Deprecated since 07.06.2021, 
//Will be disabled with version 2.0 (OOP_Rework_Finished)
//New classes will be described soon
import exceptions.LegacyBrokenCsvStructureException;

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
        } catch (IOException | LegacyBrokenCsvStructureException exception) {
            exception.printStackTrace();
        }
    }
}
```