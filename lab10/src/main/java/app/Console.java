package app;

import i18n.Messages;

import java.nio.charset.Charset;
import java.util.Scanner;

public final class Console {

    private static final Scanner SCANNER = new Scanner(System.in, stdinCharset());

    private Console() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt + " ");
        return SCANNER.hasNextLine() ? SCANNER.nextLine().trim() : "0";
    }

    public static String readNotEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println(Messages.get("prompt.empty"));
        }
    }

    public static char readKey() {
        while (true) {
            String value = readLine(Messages.get("prompt.key"));
            if (value.length() == 1 && value.charAt(0) < 128 && Character.isLetterOrDigit(value.charAt(0))) {
                return value.charAt(0);
            }
            System.out.println(Messages.get("prompt.key.wrong"));
        }
    }

    public static boolean isYes(String answer) {
        return answer.equalsIgnoreCase("y")
                || answer.equalsIgnoreCase("yes")
                || answer.equalsIgnoreCase(Messages.get("answer.yes"));
    }

    private static Charset stdinCharset() {
        String name = System.getProperty("stdin.encoding");
        return name == null ? Charset.defaultCharset() : Charset.forName(name);
    }
}
