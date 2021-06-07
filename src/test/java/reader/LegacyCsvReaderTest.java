package reader;

import exceptions.LegacyBrokenCsvStructureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class LegacyCsvReaderTest {

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

            try (LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', '\"')
            ) {
                LinkedList<String> strings = legacyCsvReader.readLine();
                LinkedList<String> expected = positiveResults.get(str);
                if (strings.size() != 3) {
                    Assert.fail("wrong elements count");
                }

                Assert.assertEquals(strings, expected);
            } catch (LegacyBrokenCsvStructureException | IOException | NullPointerException e) {
                if (e instanceof LegacyBrokenCsvStructureException) {
                    String message = ((LegacyBrokenCsvStructureException) e).getCustomMessage();
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
                    LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                            new BufferedReader(
                                    new StringReader(str)
                            ), ',', '\"')
            ) {
                LinkedList<String> strings = legacyCsvReader.readLine();
                Assert.fail("No exception thrown\n" + strings.toString());
            } catch (LegacyBrokenCsvStructureException | IOException e) {
                if (!(e instanceof LegacyBrokenCsvStructureException)) {
                    Assert.fail("Wrong Exception");
                } else {
                    System.out.println(((LegacyBrokenCsvStructureException) e).getCustomMessage());
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

            try (LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', null)
            ) {
                LinkedList<String> strings = legacyCsvReader.readLine();
                LinkedList<String> expected = positiveResults.get(str);
                if (strings.size() != 3) {
                    Assert.fail("wrong elements count");
                }

                Assert.assertEquals(strings, expected);

            } catch (LegacyBrokenCsvStructureException | IOException | NullPointerException e) {
                if (e instanceof LegacyBrokenCsvStructureException) {
                    String message = ((LegacyBrokenCsvStructureException) e).getCustomMessage();
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
            try (LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                    new BufferedReader(
                            new StringReader(str)
                    ), ',', null)
            ) {
                legacyCsvReader.symmetryCheck = 3;
                LinkedList<String> strings = legacyCsvReader.readLine();
                Assert.fail("No Exception thrown\n" + strings.toString());
            } catch (LegacyBrokenCsvStructureException | IOException e) {
                if (!(e instanceof LegacyBrokenCsvStructureException)) {
                    Assert.fail("Wrong exception");
                } else {
                    System.out.println(((LegacyBrokenCsvStructureException) e).getCustomMessage());
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


        try (LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                new BufferedReader(new InputStreamReader(inputStream)),
                ',',
                '\"'
        )) {
            while (legacyCsvReader.ready()) {
                try {
                    LinkedList<String> row = legacyCsvReader.readLine();
                    Assert.assertEquals(row, correctRows.get(0));
                } catch (LegacyBrokenCsvStructureException | IOException exception) {
                    if (exception instanceof LegacyBrokenCsvStructureException) {
                        System.out.println(((LegacyBrokenCsvStructureException) exception).getCustomMessage());
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
        LegacyCsvReader legacyCsvReader = new LegacyCsvReader(
                new BufferedReader(new StringReader("test,string")),
                ',',
                '\"');
        legacyCsvReader.close();
        try {
            System.out.println(legacyCsvReader.ready());
        } catch (IOException ioException) {
            Assert.assertEquals(ioException.getMessage(), "Stream closed");
        }
    }
}