package main;

import exceptions.BrokenCsvStructureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class CsvReaderTest {

    private static final ArrayList<String> inputPositiveCases = new ArrayList<>();
    private static final ArrayList<String> inputNegativeCases = new ArrayList<>();
    private static final HashMap<String, LinkedList<String>> positiveResults = new HashMap<>();

    static {
        //positive
        inputPositiveCases.add("a,b,c");
        inputPositiveCases.add("a,\"b\",c");
        inputPositiveCases.add("\"a\",\"b\",\"c\"");
        inputPositiveCases.add("\"a\",\"b\nb\",\"c\"");
        inputPositiveCases.add("\"a,\",\"b\n\n,\n\nb\",\"c\"");
        inputPositiveCases.add("\"a,\",\"b\nb\",\"c\t,\n\"");
        inputPositiveCases.add("a,\"b\"\"b\",c");
        inputPositiveCases.add("a,\"\"\"bb\"\"\",c");

        positiveResults.put("a,b,c", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("a,\"b\",c", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("\"a\",\"b\",\"c\"", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("\"a\",\"b\nb\",\"c\"", new LinkedList<>(Arrays.asList("a", "b\nb", "c")));
        positiveResults.put("\"a,\",\"b\n\n,\n\nb\",\"c\"", new LinkedList<>(Arrays.asList("a,", "b\n\n,\n\nb", "c")));
        positiveResults.put("\"a,\",\"b\nb\",\"c\t,\n\"", new LinkedList<>(Arrays.asList("a,", "b\nb", "c\t,\n")));
        positiveResults.put("a,\"b\"\"b\",c", new LinkedList<>(Arrays.asList("a", "b\"b", "c")));
        positiveResults.put("a,\"\"\"bb\"\"\",c", new LinkedList<>(Arrays.asList("a", "\"bb\"", "c")));

        //negative
        inputNegativeCases.add("a,\"\"b\"\",c");
        inputNegativeCases.add("a,\"\"b,c");
        inputNegativeCases.add("a,b\"\",c");
        inputNegativeCases.add("a,\"b,c");
    }


    @Test
    public void positiveTestReadLine() {

        for (String str : inputPositiveCases) {

            try (CsvReader csvReader = new CsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', '\"')
            ) {
                LinkedList<String> strings = csvReader.readLine();
                LinkedList<String> expected = positiveResults.get(str);
                if (strings.size() != 3) {
                    Assert.fail("wrong elements count");
                }

                Assert.assertEquals(strings, expected);
                System.out.printf("String: %s\n", str.replaceAll("\n", "\\\\n"));
                System.out.printf("Actual -> %s - %s <- Expected\n", strings.toString().replaceAll("\n", "\\\\n"), expected.toString().replaceAll("\n", "\\\\n"));

            } catch (BrokenCsvStructureException | IOException | NullPointerException e) {
                if (e instanceof BrokenCsvStructureException) {
                    String message = ((BrokenCsvStructureException) e).getCustomMessage();
                    Assert.fail(message);
                } else {
                    e.printStackTrace();
                    Assert.fail("Exception was thrown");
                }
            }
        }
    }

    @Test
    public void negativeTestReadLine() {
        for (String str : inputNegativeCases) {
            try (
                    CsvReader csvReader = new CsvReader(
                            new BufferedReader(
                                    new StringReader(str)
                            ), ',', '\"')
            ) {
                LinkedList<String> strings = csvReader.readLine();
                Assert.fail("No exception thrown");
            } catch (BrokenCsvStructureException | IOException e) {
                if (!(e instanceof BrokenCsvStructureException)) {
                    Assert.fail("Wrong Exception");
                } else {
                    System.out.println(((BrokenCsvStructureException) e).getCustomMessage());
                }
            }
        }
    }

    @Test
    public void testClose() {
        CsvReader csvReader = new CsvReader(
                new BufferedReader(new StringReader("test,string")),
                ',',
                '\"');
        csvReader.close();
        try {
            System.out.println(csvReader.ready());
        } catch (IOException ioException) {
            Assert.assertEquals(ioException.getMessage(), "Stream closed");
        }
    }
}