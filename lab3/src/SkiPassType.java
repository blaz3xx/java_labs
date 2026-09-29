import java.time.DayOfWeek;
import java.time.LocalDate;

public enum SkiPassType {
    WEEKDAY_MORNING("Weekday, half day 9-13", DayType.WEEKDAY, 1, 0, 9, 13),
    WEEKDAY_AFTERNOON("Weekday, half day 13-17", DayType.WEEKDAY, 1, 0, 13, 17),
    WEEKDAY_1_DAY("Weekday, 1 day", DayType.WEEKDAY, 1, 0, 9, 17),
    WEEKDAY_2_DAYS("Weekday, 2 days", DayType.WEEKDAY, 2, 0, 9, 17),
    WEEKDAY_5_DAYS("Weekday, 5 days", DayType.WEEKDAY, 5, 0, 9, 17),
    WEEKDAY_10_LIFTS("Weekday, 10 lifts", DayType.WEEKDAY, 0, 10, 9, 17),
    WEEKDAY_20_LIFTS("Weekday, 20 lifts", DayType.WEEKDAY, 0, 20, 9, 17),
    WEEKDAY_50_LIFTS("Weekday, 50 lifts", DayType.WEEKDAY, 0, 50, 9, 17),
    WEEKDAY_100_LIFTS("Weekday, 100 lifts", DayType.WEEKDAY, 0, 100, 9, 17),

    WEEKEND_MORNING("Weekend, half day 9-13", DayType.WEEKEND, 1, 0, 9, 13),
    WEEKEND_AFTERNOON("Weekend, half day 13-17", DayType.WEEKEND, 1, 0, 13, 17),
    WEEKEND_1_DAY("Weekend, 1 day", DayType.WEEKEND, 1, 0, 9, 17),
    WEEKEND_2_DAYS("Weekend, 2 days", DayType.WEEKEND, 2, 0, 9, 17),
    WEEKEND_10_LIFTS("Weekend, 10 lifts", DayType.WEEKEND, 0, 10, 9, 17),
    WEEKEND_20_LIFTS("Weekend, 20 lifts", DayType.WEEKEND, 0, 20, 9, 17),
    WEEKEND_50_LIFTS("Weekend, 50 lifts", DayType.WEEKEND, 0, 50, 9, 17),
    WEEKEND_100_LIFTS("Weekend, 100 lifts", DayType.WEEKEND, 0, 100, 9, 17),

    SEASON("Season pass", DayType.ANY, 0, 0, 9, 17);

    // on which days a ski-pass can be used
    public enum DayType {
        WEEKDAY,
        WEEKEND,
        ANY;

        public boolean matches(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

            if (this == WEEKDAY) {
                return !isWeekend;
            }
            if (this == WEEKEND) {
                return isWeekend;
            }
            return true;
        }
    }

    private final String label;
    private final DayType dayType;
    private final int days;       
    private final int lifts;      
    private final int fromHour;
    private final int toHour;

    SkiPassType(String label, DayType dayType, int days, int lifts, int fromHour, int toHour) {
        this.label = label;
        this.dayType = dayType;
        this.days = days;
        this.lifts = lifts;
        this.fromHour = fromHour;
        this.toHour = toHour;
    }

    public String getLabel() {
        return label;
    }

    public DayType getDayType() {
        return dayType;
    }

    public int getDays() {
        return days;
    }

    public int getLifts() {
        return lifts;
    }

    public int getFromHour() {
        return fromHour;
    }

    public int getToHour() {
        return toHour;
    }
}
