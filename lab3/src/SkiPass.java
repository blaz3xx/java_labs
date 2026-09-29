import java.time.LocalDate;
import java.time.LocalDateTime;

// data stored on the card itself
public abstract class SkiPass {

    private int id;
    private SkiPassType type;
    private LocalDate validFrom;
    private LocalDate validUntil;

    public SkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) {
        this.id = id;
        this.type = type;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public int getId() {
        return id;
    }

    public SkiPassType getType() {
        return type;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validUntil);
    }

    // checks the day of the week and the hours
    public boolean isValidAt(LocalDateTime time) {
        if (!type.getDayType().matches(time.toLocalDate())) {
            return false;
        }
        int hour = time.getHour();
        return hour >= type.getFromHour() && hour < type.getToHour();
    }

    // each kind of card decides this in its own way
    public abstract boolean hasLiftsLeft();

    public abstract void useLift();

    @Override
    public String toString() {
        return "#" + id + " " + type.getLabel();
    }
}
