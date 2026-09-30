import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    static final DateTimeFormatter SHOW_FORMAT =
            DateTimeFormatter.ofPattern("EEE dd.MM.yyyy HH:mm", Locale.ENGLISH);
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static Scanner sc = new Scanner(System.in);
    static SkiPassSystem system = new SkiPassSystem();
    static Turnstile turnstile = new Turnstile(system);

    static LocalDateTime now = LocalDateTime.of(2027, 1, 11, 10, 0);

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(sc.nextLine().trim());
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Enter a number from " + min + " to " + max + ".");
        }
    }

    static void issuePass() {
        SkiPassType[] types = SkiPassType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + " - " + types[i].getLabel());
        }
        int choice = readInt("Ski-pass type: ", 1, types.length);

        SkiPass card = system.issue(types[choice - 1], now.toLocalDate());
        System.out.println("Issued " + card + ", valid "
                + card.getValidFrom().format(DATE_FORMAT) + " - " + card.getValidUntil().format(DATE_FORMAT));
    }

    static void passTurnstile() {
        int id = readInt("Card number: ", 1, Integer.MAX_VALUE);
        SkiPass card = system.findById(id);
        turnstile.tryPass(card, now);
    }

    static void blockPass() {
        int id = readInt("Card number to block: ", 1, Integer.MAX_VALUE);
        if (system.findById(id) == null) {
            System.out.println("There is no card #" + id + ".");
        } else {
            system.block(id);
            System.out.println("Card #" + id + " is blocked.");
        }
    }

    static void changeTime() {
        while (true) {
            System.out.print("New date and time (dd.mm.yyyy hh:mm): ");
            try {
                now = LocalDateTime.parse(sc.nextLine().trim(), INPUT_FORMAT);
                return;
            } catch (DateTimeParseException e) {
                System.out.println("Wrong format! Example: 16.01.2027 11:00");
            }
        }
    }

    static void showStats() {
        System.out.println("--- Total ---");
        turnstile.printTotalStats();
        System.out.println("--- By ski-pass type ---");
        turnstile.printStatsByType();
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("Now: " + now.format(SHOW_FORMAT));
            System.out.println("1 - Issue a ski-pass");
            System.out.println("2 - Pass through the turnstile");
            System.out.println("3 - Block a ski-pass");
            System.out.println("4 - Change date and time");
            System.out.println("5 - Show statistics");
            System.out.println("0 - Exit");
            System.out.print("Your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> issuePass();
                case "2" -> passTurnstile();
                case "3" -> blockPass();
                case "4" -> changeTime();
                case "5" -> showStats();
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown command, enter a number from 0 to 5.");
            }
        }
    }
}
