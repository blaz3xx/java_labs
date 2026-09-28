import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Main {

    static final String NAME_REGEX = "[A-ZА-ЯІЇЄҐ][a-zа-яіїєґ'-]+";
    static final String PHONE_REGEX = "\\+380\\d{9}";
    static final String STREET_REGEX = "[A-Za-zА-Яа-яІіЇїЄєҐґ0-9 .'-]{2,}";
    static final String HOUSE_REGEX = "\\d+[A-Za-zА-Яа-яІіЇїЄєҐґ]?";
    static final String APARTMENT_REGEX = "[1-9]\\d*";

    static Scanner sc = new Scanner(System.in);

    static String readValid(String prompt, String regex, String hint) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches(regex)) {
                return input;
            }
            System.out.println("Wrong format! " + hint + " Try again.");
        }
    }

    static LocalDate readBirthDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            System.out.print("Birth date (dd.mm.yyyy): ");
            String input = sc.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (date.isAfter(LocalDate.now()) || date.getYear() < 1900) {
                    System.out.println("Date must be between 1900 and today. Try again.");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Wrong date! Example: 05.09.2006. Try again.");
            }
        }
    }

    static void addEntry(CuratorJournal journal) {
        System.out.println("--- New record ---");
        String lastName = readValid("Last name: ", NAME_REGEX,
                "Use letters only, starting with a capital letter.");
        String firstName = readValid("First name: ", NAME_REGEX,
                "Use letters only, starting with a capital letter.");
        LocalDate birthDate = readBirthDate();
        String phone = readValid("Phone (+380XXXXXXXXX): ", PHONE_REGEX,
                "Example: +380671234567.");
        String street = readValid("Street: ", STREET_REGEX,
                "Use letters, digits, spaces (at least 2 characters).");
        String house = readValid("House: ", HOUSE_REGEX,
                "Example: 12 or 12A.");
        int apartment = Integer.parseInt(readValid("Apartment: ", APARTMENT_REGEX,
                "Enter a positive number."));

        Address address = new Address(street, house, apartment);
        journal.addEntry(new JournalEntry(lastName, firstName, birthDate, phone, address));
        System.out.println("Record added!");
    }

    public static void main(String[] args) {
        CuratorJournal journal = new CuratorJournal();

        while (true) {
            System.out.println();
            System.out.println("1 - Add record");
            System.out.println("2 - Show all records");
            System.out.println("0 - Exit");
            System.out.print("Your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> addEntry(journal);
                case "2" -> journal.printAll();
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown command, enter 1, 2 or 0.");
            }
        }
    }
}
