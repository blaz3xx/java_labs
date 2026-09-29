import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Locale;

public class Turnstile {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("EEE dd.MM.yyyy HH:mm", Locale.ENGLISH);

    private SkiPassSystem system;

    private int allowedTotal;
    private int deniedTotal;
    private int unreadable;
    private EnumMap<SkiPassType, Integer> allowedByType = new EnumMap<>(SkiPassType.class);
    private EnumMap<SkiPassType, Integer> deniedByType = new EnumMap<>(SkiPassType.class);

    public Turnstile(SkiPassSystem system) {
        this.system = system;
    }

    public boolean tryPass(SkiPass card, LocalDateTime time) {
        System.out.print("[" + time.format(TIME_FORMAT) + "] ");

        if (card == null) {
            unreadable++;
            deniedTotal++;
            System.out.println("DENIED: cannot read the card");
            return false;
        }
        if (system.isBlocked(card.getId())) {
            return deny(card, "card is blocked");
        }
        if (!card.isActiveOn(time.toLocalDate())) {
            return deny(card, "card is expired or not active yet");
        }
        if (!card.isValidAt(time)) {
            return deny(card, "card is not valid on this day or at this hour");
        }
        if (!card.hasLiftsLeft()) {
            return deny(card, "no lifts left");
        }

        card.useLift();
        allowedTotal++;
        allowedByType.put(card.getType(), allowedByType.getOrDefault(card.getType(), 0) + 1);
        System.out.println("ALLOWED: " + card);
        return true;
    }

    private boolean deny(SkiPass card, String reason) {
        deniedTotal++;
        deniedByType.put(card.getType(), deniedByType.getOrDefault(card.getType(), 0) + 1);
        System.out.println("DENIED: " + card + " - " + reason);
        return false;
    }

    public void printTotalStats() {
        System.out.println("Allowed: " + allowedTotal + ", denied: " + deniedTotal);
    }

    public void printStatsByType() {
        for (SkiPassType type : SkiPassType.values()) {
            int allowed = allowedByType.getOrDefault(type, 0);
            int denied = deniedByType.getOrDefault(type, 0);
            if (allowed > 0 || denied > 0) {
                System.out.printf("%-25s allowed: %2d, denied: %2d%n", type.getLabel(), allowed, denied);
            }
        }
        if (unreadable > 0) {
            System.out.printf("%-25s allowed: %2d, denied: %2d%n", "Unreadable cards", 0, unreadable);
        }
    }
}
