# Read me
#### Version 1.5 (OOP_Rework_WIP);

MVP for future major upgrade;

Manual will be there

#### Version 1.2 Legacy classes

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