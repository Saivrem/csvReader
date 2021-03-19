package main;

import exceptions.BrokenCsvStructureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class CsvReaderTest {

    @Test
    public void positiveTestReadLineWithEscape() {
        ArrayList<String> inputPositiveCases = new ArrayList<>();
        HashMap<String, LinkedList<String>> positiveResults = new HashMap<>();

        inputPositiveCases.add("a,b,c");
        inputPositiveCases.add("a,\"b\",c");
        inputPositiveCases.add("\"a\",\"b\",\"c\"");
        inputPositiveCases.add("\"a\",\"b\nb\",\"c\"");
        inputPositiveCases.add("\"a,\",\"b\n\n,\n\nb\",\"c\"");
        inputPositiveCases.add("\"a,\",\"b\nb\",\"c\t,\n\"");
        inputPositiveCases.add("a,\"b\"\"b\",c");
        inputPositiveCases.add("a,\"\"\"bb\"\"\",c");
        inputPositiveCases.add("\"a\",\"\",");
        inputPositiveCases.add("\"a\n\",,");

        positiveResults.put("a,b,c", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("a,\"b\",c", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("\"a\",\"b\",\"c\"", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("\"a\",\"b\nb\",\"c\"", new LinkedList<>(Arrays.asList("a", "b\nb", "c")));
        positiveResults.put("\"a,\",\"b\n\n,\n\nb\",\"c\"", new LinkedList<>(Arrays.asList("a,", "b\n\n,\n\nb", "c")));
        positiveResults.put("\"a,\",\"b\nb\",\"c\t,\n\"", new LinkedList<>(Arrays.asList("a,", "b\nb", "c\t,\n")));
        positiveResults.put("a,\"b\"\"b\",c", new LinkedList<>(Arrays.asList("a", "b\"b", "c")));
        positiveResults.put("a,\"\"\"bb\"\"\",c", new LinkedList<>(Arrays.asList("a", "\"bb\"", "c")));
        positiveResults.put("\"a\",\"\",", new LinkedList<>(Arrays.asList("a", "", "")));
        positiveResults.put("\"a\n\",,", new LinkedList<>(Arrays.asList("a\n", "", "")));

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
    public void negativeTestReadLineWithEscape() {
        ArrayList<String> inputNegativeCases = new ArrayList<>();
        inputNegativeCases.add("a,\"\"b\"\",c");
        inputNegativeCases.add("a,\"\"b,c");
        inputNegativeCases.add("a,b\"\",c");
        inputNegativeCases.add("a,\"b,c");
        inputNegativeCases.add("a,\"b,c\n");
        for (String str : inputNegativeCases) {
            try (
                    CsvReader csvReader = new CsvReader(
                            new BufferedReader(
                                    new StringReader(str)
                            ), ',', '\"')
            ) {
                LinkedList<String> strings = csvReader.readLine();
                Assert.fail("No exception thrown\n" + strings.toString());
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
    public void positiveTestReadLineWOEscape() {
        ArrayList<String> inputPositiveCases = new ArrayList<>();
        HashMap<String, LinkedList<String>> positiveResults = new HashMap<>();

        inputPositiveCases.add("a,b,c");
        inputPositiveCases.add("a,,");
        inputPositiveCases.add("a,b\",c");
        inputPositiveCases.add("\"a,b,c");
        inputPositiveCases.add("\"a\",\"b\",\"c\"");

        positiveResults.put("a,b,c", new LinkedList<>(Arrays.asList("a", "b", "c")));
        positiveResults.put("a,,", new LinkedList<>(Arrays.asList("a", "", "")));
        positiveResults.put("a,b\",c", new LinkedList<>(Arrays.asList("a", "b\"", "c")));
        positiveResults.put("\"a,b,c", new LinkedList<>(Arrays.asList("\"a", "b", "c")));
        positiveResults.put("\"a\",\"b\",\"c\"", new LinkedList<>(Arrays.asList("\"a\"", "\"b\"", "\"c\"")));

        for (String str : inputPositiveCases) {

            try (CsvReader csvReader = new CsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', null)
            ) {
                LinkedList<String> strings = csvReader.readLine();
                LinkedList<String> expected = positiveResults.get(str);
                if (strings.size() != 3) {
                    Assert.fail("wrong elements count");
                }

                Assert.assertEquals(strings, expected);

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
    public void negativeTestReadLineWOEscape() {
        ArrayList<String> inputNegativeCases = new ArrayList<>();
        inputNegativeCases.add("a,b,b,c");
        for (String str : inputNegativeCases) {
            try (CsvReader csvReader = new CsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', null)
            ) {
                csvReader.symmetryCheck = 3;
                LinkedList<String> strings = csvReader.readLine();
                Assert.fail("No Exception thrown\n" + strings.toString());
            } catch (BrokenCsvStructureException | IOException e) {
                if (!(e instanceof BrokenCsvStructureException)) {
                    Assert.fail("Wrong exception");
                } else {
                    System.out.println(((BrokenCsvStructureException) e).getCustomMessage());
                }
            }
        }
    }

    @Test
    public void testContinuousExceptionThrow() {
        ArrayList<String> listWithExceptions = new ArrayList<>();
        ArrayList<LinkedList<String>> correctRows = new ArrayList<>();
        listWithExceptions.add("\"first\",\"second\"broken,third");
        listWithExceptions.add("\"correctFirst\",\"correctSec ond,\",\"correct Third\"");
        correctRows.add(new LinkedList<>(Arrays.asList("correctFirst", "correctSec ond,", "correct Third")));
        StringBuilder builder = new StringBuilder();
        for (String str : listWithExceptions) {
            builder.append(str).append("\n");
        }

        InputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));


        try (CsvReader csvReader = new CsvReader(
                new BufferedReader(new InputStreamReader(inputStream)),
                ',',
                '\"'
        )) {
            while (csvReader.ready()) {
                try {
                    LinkedList<String> row = csvReader.readLine();
                    Assert.assertEquals(row, correctRows.get(0));
                } catch (BrokenCsvStructureException | IOException exception) {
                    if (exception instanceof BrokenCsvStructureException) {
                        System.out.println(((BrokenCsvStructureException) exception).getCustomMessage());
                    } else {
                        Assert.fail("Wrong exception withing While");
                    }
                }
            }
        } catch (IOException exception) {
            Assert.fail("Wrong exception");
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