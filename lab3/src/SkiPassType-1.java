import java.time.DayOfWeek; // перелік днів тижня (MONDAY … SUNDAY)
import java.time.LocalDate; // дата без часу

public enum SkiPassType { // перелік усіх видів ski-pass; кожна константа зберігає свої параметри
    WEEKDAY_MORNING("Weekday, half day 9-13", DayType.WEEKDAY, 1, 0, 9, 13), // будні, пів дня до обіду: 1 день, без ліку підйомів, години 9–13
    WEEKDAY_AFTERNOON("Weekday, half day 13-17", DayType.WEEKDAY, 1, 0, 13, 17), // будні, пів дня після обіду: години 13–17
    WEEKDAY_1_DAY("Weekday, 1 day", DayType.WEEKDAY, 1, 0, 9, 17), // будні, 1 день, 9–17
    WEEKDAY_2_DAYS("Weekday, 2 days", DayType.WEEKDAY, 2, 0, 9, 17), // будні, 2 дні
    WEEKDAY_5_DAYS("Weekday, 5 days", DayType.WEEKDAY, 5, 0, 9, 17), // будні, 5 днів
    WEEKDAY_10_LIFTS("Weekday, 10 lifts", DayType.WEEKDAY, 0, 10, 9, 17), // будні, 10 підйомів (днів немає, лічильник підйомів = 10)
    WEEKDAY_20_LIFTS("Weekday, 20 lifts", DayType.WEEKDAY, 0, 20, 9, 17), // будні, 20 підйомів
    WEEKDAY_50_LIFTS("Weekday, 50 lifts", DayType.WEEKDAY, 0, 50, 9, 17), // будні, 50 підйомів
    WEEKDAY_100_LIFTS("Weekday, 100 lifts", DayType.WEEKDAY, 0, 100, 9, 17), // будні, 100 підйомів

    WEEKEND_MORNING("Weekend, half day 9-13", DayType.WEEKEND, 1, 0, 9, 13), // вихідні, пів дня до обіду
    WEEKEND_AFTERNOON("Weekend, half day 13-17", DayType.WEEKEND, 1, 0, 13, 17), // вихідні, пів дня після обіду
    WEEKEND_1_DAY("Weekend, 1 day", DayType.WEEKEND, 1, 0, 9, 17), // вихідні, 1 день
    WEEKEND_2_DAYS("Weekend, 2 days", DayType.WEEKEND, 2, 0, 9, 17), // вихідні, 2 дні (за умовою 5 днів на вихідні немає)
    WEEKEND_10_LIFTS("Weekend, 10 lifts", DayType.WEEKEND, 0, 10, 9, 17), // вихідні, 10 підйомів
    WEEKEND_20_LIFTS("Weekend, 20 lifts", DayType.WEEKEND, 0, 20, 9, 17), // вихідні, 20 підйомів
    WEEKEND_50_LIFTS("Weekend, 50 lifts", DayType.WEEKEND, 0, 50, 9, 17), // вихідні, 50 підйомів
    WEEKEND_100_LIFTS("Weekend, 100 lifts", DayType.WEEKEND, 0, 100, 9, 17), // вихідні, 100 підйомів

    SEASON("Season pass", DayType.ANY, 0, 0, 9, 17); // абонемент на сезон: будь-який день, без ліку підйомів

    // у які дні можна користуватися ski-pass
    public enum DayType { // вкладений перелік: тип днів, у які діє картка
        WEEKDAY, // лише робочі дні (пн–пт)
        WEEKEND, // лише вихідні (сб–нд)
        ANY; // будь-який день

        public boolean matches(LocalDate date) { // чи підходить дата під цей тип днів
            DayOfWeek day = date.getDayOfWeek(); // визначаємо день тижня заданої дати
            boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY; // true, якщо субота або неділя

            if (this == WEEKDAY) { // якщо картка «на робочі дні»
                return !isWeekend; // підходить лише коли дата НЕ вихідна
            } // кінець умови
            if (this == WEEKEND) { // якщо картка «на вихідні»
                return isWeekend; // підходить лише коли дата вихідна
            } // кінець умови
            return true; // тип ANY – підходить будь-яка дата
        } // кінець методу matches
    } // кінець вкладеного переліку DayType

    private final String label; // назва виду картки для виведення користувачу
    private final DayType dayType; // у які дні діє (будні / вихідні / будь-які)
    private final int days; // на скільки днів видається (0 для карток за підйомами)
    private final int lifts; // кількість підйомів (0 для карток без ліку підйомів)
    private final int fromHour; // година, з якої можна проходити (включно)
    private final int toHour; // година, до якої можна проходити (не включно)

    SkiPassType(String label, DayType dayType, int days, int lifts, int fromHour, int toHour) { // конструктор переліку (завжди private, викликається для кожної константи)
        this.label = label; // зберігаємо назву
        this.dayType = dayType; // зберігаємо тип днів
        this.days = days; // зберігаємо кількість днів
        this.lifts = lifts; // зберігаємо кількість підйомів
        this.fromHour = fromHour; // зберігаємо початок години доступу
        this.toHour = toHour; // зберігаємо кінець години доступу
    } // кінець конструктора

    public String getLabel() { // геттер назви
        return label; // повертаємо назву
    } // кінець методу

    public DayType getDayType() { // геттер типу днів
        return dayType; // повертаємо тип днів
    } // кінець методу

    public int getDays() { // геттер кількості днів
        return days; // повертаємо кількість днів
    } // кінець методу

    public int getLifts() { // геттер кількості підйомів
        return lifts; // повертаємо кількість підйомів
    } // кінець методу

    public int getFromHour() { // геттер початку доступу
        return fromHour; // повертаємо годину початку
    } // кінець методу

    public int getToHour() { // геттер кінця доступу
        return toHour; // повертаємо годину кінця
    } // кінець методу
} // кінець переліку SkiPassType
