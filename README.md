# A simple how-to
```java
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World");
        BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\converted.json"));
        CsvReader reader = new CsvReader(
                new BufferedReader(new FileReader("D:\\test.csv")), ',', null
        );
        LinkedList<String> header = null;
        boolean isHeader = true;
        writer.write("{\n\t\"data\":[\n");
        while (reader.ready()) {
            LinkedList<String> strings = reader.readLine();
            if (isHeader) {
                isHeader = false;
                header = strings;
            }
            writer.write("\t\t{\n");
            for (int i = 0, stringsSize = strings.size(); i < stringsSize; i++) {
                if (i < stringsSize - 1) {
                    writer.write(String.format("\t\t\t\"%s\":\"%s\",\n", header.get(i), strings.get(i)));
                } else {
                    writer.write(String.format("\t\t\t\"%s\":\"%s\"\n", header.get(i), strings.get(i)));
                }
            }
            if (reader.ready()) {
                writer.write("\t\t},\n");
            } else {
                writer.write("\t\t}\n");
            }
        }
        writer.write("\t]\n}");
        writer.flush();
        writer.close();
        reader.close();
    }
}
```
