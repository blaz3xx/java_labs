package reflection;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReflectionApp {

    private static final String LITERAL = "Hello, Java!";
    private static final String SAME_LITERAL = "Hello, Java!";
    private static final String DEFAULT_REPLACEMENT = "Changed by reflection";
    private static final String CHILD_FLAG = "reflection.child";

    private static Scanner scanner;

    public static void main(String[] args) throws Exception {
        if (!StringReflection.isAvailable()) {
            if (System.getProperty(CHILD_FLAG) == null) {
                System.exit(relaunchWithOpenedModule());
            }
            System.err.println("Cannot access String.value. Run with: --add-opens=java.base/java.lang=ALL-UNNAMED");
            return;
        }

        scanner = new Scanner(System.in, stdinCharset());
        System.out.println("String.value field type in this JVM: " + StringReflection.valueFieldType());

        while (true) {
            System.out.println();
            System.out.println("1 - String literal");
            System.out.println("2 - String typed from the keyboard");
            System.out.println("0 - Exit");
            String choice = readLine("Your choice: ");

            switch (choice) {
                case "1" -> changeLiteral();
                case "2" -> changeTypedString();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Unknown command.");
            }
        }
    }

    private static void changeLiteral() throws ReflectiveOperationException {
        String text = LITERAL;
        String replacement = askReplacement();

        System.out.println("Before: " + text + "   (" + StringReflection.describeInternals(text) + ")");
        StringReflection.replaceValue(text, replacement);
        System.out.println("After:  " + text + "   (" + StringReflection.describeInternals(text) + ")");
        System.out.print("The same literal written elsewhere in the code: ");
        System.out.println(SAME_LITERAL);
        System.out.println("(literals are interned, so every use of this literal now has the new value)");
    }

    private static void changeTypedString() throws ReflectiveOperationException {
        String text = readNotEmpty("Enter a string: ");
        String replacement = askReplacement();

        System.out.println("Before: " + text + "   (" + StringReflection.describeInternals(text) + ")");
        StringReflection.replaceValue(text, replacement);
        System.out.println("After:  " + text + "   (" + StringReflection.describeInternals(text) + ")");
    }

    private static String askReplacement() {
        String value = readLine("New value (empty = \"" + DEFAULT_REPLACEMENT + "\"): ");
        return value.isEmpty() ? DEFAULT_REPLACEMENT : value;
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "0";
    }

    private static String readNotEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("The value cannot be empty.");
        }
    }

    private static Charset stdinCharset() {
        String name = System.getProperty("stdin.encoding");
        return name == null ? Charset.defaultCharset() : Charset.forName(name);
    }

    private static int relaunchWithOpenedModule() throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        command.add("--add-opens=java.base/java.lang=ALL-UNNAMED");
        if (Runtime.version().feature() >= 26) {
            command.add("--enable-final-field-mutation=ALL-UNNAMED");
        }
        command.add("-D" + CHILD_FLAG + "=true");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(ReflectionApp.class.getName());

        System.out.println("Restarting the JVM with access to java.lang (the module system hides String.value)...");
        return new ProcessBuilder(command).inheritIO().start().waitFor();
    }
}
