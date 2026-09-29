import java.time.LocalDateTime; // дата й час
import java.time.format.DateTimeFormatter; // форматування дати й часу у рядок
import java.util.EnumMap; // швидка мапа, де ключі – константи enum
import java.util.Locale; // мова для назв днів тижня

public class Turnstile { // турнікет: перевіряє картки, дозволяє/забороняє прохід і веде статистику

    private static final DateTimeFormatter TIME_FORMAT = // формат часу для журналу проходів
            DateTimeFormatter.ofPattern("EEE dd.MM.yyyy HH:mm", Locale.ENGLISH); // приклад: «Mon 11.01.2027 10:00»

    private SkiPassSystem system; // зв'язок із системою (реєстр карток і блокування)

    private int allowedTotal; // всього дозволених проходів
    private int deniedTotal; // всього відмов
    private int unreadable; // скільки разів картку не вдалося прочитати
    private EnumMap<SkiPassType, Integer> allowedByType = new EnumMap<>(SkiPassType.class); // дозволи по видах карток
    private EnumMap<SkiPassType, Integer> deniedByType = new EnumMap<>(SkiPassType.class); // відмови по видах карток

    public Turnstile(SkiPassSystem system) { // конструктор: турнікет одразу під'єднується до системи
        this.system = system; // зберігаємо посилання на систему
    } // кінець конструктора

    public boolean tryPass(SkiPass card, LocalDateTime time) { // основна логіка: чи пускати картку в заданий момент
        System.out.print("[" + time.format(TIME_FORMAT) + "] "); // друкуємо час події на початку рядка журналу

        if (card == null) { // системa не знайшла картку – дані не вдалося зчитати
            unreadable++; // рахуємо нечитабельні картки
            deniedTotal++; // це теж відмова
            System.out.println("DENIED: cannot read the card"); // повідомлення про відмову
            return false; // прохід заборонено
        } // кінець умови
        if (system.isBlocked(card.getId())) { // картка заблокована в системі
            return deny(card, "card is blocked"); // відмова з причиною
        } // кінець умови
        if (!card.isActiveOn(time.toLocalDate())) { // сьогодні поза терміном дії картки
            return deny(card, "card is expired or not active yet"); // відмова: прострочена або ще не діє
        } // кінець умови
        if (!card.isValidAt(time)) { // не той день тижня або година
            return deny(card, "card is not valid on this day or at this hour"); // відмова з причиною
        } // кінець умови
        if (!card.hasLiftsLeft()) { // підйоми закінчились (поліморфний виклик)
            return deny(card, "no lifts left"); // відмова: немає підйомів
        } // кінець умови

        card.useLift(); // усі перевірки пройдені – списуємо один підйом (для Unlimited нічого не відбувається)
        allowedTotal++; // збільшуємо загальний лічильник дозволів
        allowedByType.put(card.getType(), allowedByType.getOrDefault(card.getType(), 0) + 1); // +1 до дозволів цього виду картки
        System.out.println("ALLOWED: " + card); // повідомлення про дозвіл
        return true; // прохід дозволено
    } // кінець методу tryPass

    private boolean deny(SkiPass card, String reason) { // допоміжний метод: оформлення відмови (щоб не дублювати код)
        deniedTotal++; // збільшуємо загальний лічильник відмов
        deniedByType.put(card.getType(), deniedByType.getOrDefault(card.getType(), 0) + 1); // +1 до відмов цього виду картки
        System.out.println("DENIED: " + card + " - " + reason); // виводимо картку і причину
        return false; // завжди false – прохід заборонено
    } // кінець методу deny

    public void printTotalStats() { // сумарна статистика
        System.out.println("Allowed: " + allowedTotal + ", denied: " + deniedTotal); // дозволи та відмови разом
    } // кінець методу

    public void printStatsByType() { // статистика в розрізі видів карток
        for (SkiPassType type : SkiPassType.values()) { // проходимо всі види у порядку оголошення
            int allowed = allowedByType.getOrDefault(type, 0); // дозволи цього виду (0, якщо не було)
            int denied = deniedByType.getOrDefault(type, 0); // відмови цього виду (0, якщо не було)
            if (allowed > 0 || denied > 0) { // показуємо лише види, з якими щось відбувалось
                System.out.printf("%-25s allowed: %2d, denied: %2d%n", type.getLabel(), allowed, denied); // вирівняний рядок таблиці
            } // кінець умови
        } // кінець циклу
        if (unreadable > 0) { // були нечитабельні картки
            System.out.printf("%-25s allowed: %2d, denied: %2d%n", "Unreadable cards", 0, unreadable); // окремий рядок для них
        } // кінець умови
    } // кінець методу
} // кінець класу Turnstile
