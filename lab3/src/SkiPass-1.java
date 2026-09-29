import java.time.LocalDate; // дата без часу
import java.time.LocalDateTime; // дата разом із часом

// дані, що зберігаються на самій картці
public abstract class SkiPass { // абстрактний базовий клас картки: спільні дані й перевірки; екземпляр створити не можна

    private int id; // унікальний номер картки
    private SkiPassType type; // вид картки (з переліку SkiPassType)
    private LocalDate validFrom; // перший день дії картки
    private LocalDate validUntil; // останній день дії картки (включно)

    public SkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) { // конструктор (викликається з підкласів через super)
        this.id = id; // записуємо номер
        this.type = type; // записуємо вид
        this.validFrom = validFrom; // записуємо початок дії
        this.validUntil = validUntil; // записуємо кінець дії
    } // кінець конструктора

    public int getId() { // геттер номера
        return id; // повертаємо номер
    } // кінець методу

    public SkiPassType getType() { // геттер виду картки
        return type; // повертаємо вид
    } // кінець методу

    public LocalDate getValidFrom() { // геттер початку дії
        return validFrom; // повертаємо дату початку
    } // кінець методу

    public LocalDate getValidUntil() { // геттер кінця дії
        return validUntil; // повертаємо дату кінця
    } // кінець методу

    public boolean isActiveOn(LocalDate date) { // чи входить дата в термін дії картки
        return !date.isBefore(validFrom) && !date.isAfter(validUntil); // не раніше початку і не пізніше кінця (межі включно)
    } // кінець методу

    // перевіряє день тижня і години
    public boolean isValidAt(LocalDateTime time) { // чи дозволяє тип картки прохід у цей день і годину
        if (!type.getDayType().matches(time.toLocalDate())) { // день тижня не підходить (напр., будній талон у суботу)
            return false; // прохід не дозволено
        } // кінець умови
        int hour = time.getHour(); // беремо годину з поточного часу
        return hour >= type.getFromHour() && hour < type.getToHour(); // година у проміжку [from; to)
    } // кінець методу

    // кожен вид картки вирішує це по-своєму
    public abstract boolean hasLiftsLeft(); // чи лишились підйоми – реалізують підкласи

    public abstract void useLift(); // списати один підйом – реалізують підкласи

    @Override // перевизначаємо toString() з Object
    public String toString() { // короткий опис картки для виведення
        return "#" + id + " " + type.getLabel(); // формат: «#1 Weekday, 1 day»
    } // кінець методу
} // кінець класу SkiPass
