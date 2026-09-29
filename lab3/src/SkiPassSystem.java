import java.time.LocalDate;
import java.util.ArrayList;

// the register of all issued cards
public class SkiPassSystem {

    public static final LocalDate SEASON_START = LocalDate.of(2026, 12, 1);
    public static final LocalDate SEASON_END = LocalDate.of(2027, 4, 30);

    private ArrayList<SkiPass> issuedCards = new ArrayList<>();
    private ArrayList<Integer> blockedIds = new ArrayList<>();
    private int nextId = 1;

    public SkiPass issue(SkiPassType type, LocalDate startDate) {
        SkiPass card;
        if (type == SkiPassType.SEASON) {
            card = new UnlimitedSkiPass(nextId, type, SEASON_START, SEASON_END);
        } else if (type.getLifts() > 0) {
            // lift cards can be used until the end of the season
            card = new LiftsSkiPass(nextId, type, startDate, SEASON_END);
        } else {
            LocalDate lastDay = startDate.plusDays(type.getDays() - 1);
            card = new UnlimitedSkiPass(nextId, type, startDate, lastDay);
        }
        nextId++;
        issuedCards.add(card);
        return card;
    }

    public void block(int id) {
        if (!blockedIds.contains(id)) {
            blockedIds.add(id);
        }
    }

    // returns null if there is no card with this number
    public SkiPass findById(int id) {
        for (SkiPass card : issuedCards) {
            if (card.getId() == id) {
                return card;
            }
        }
        return null;
    }

    public boolean isBlocked(int id) {
        return blockedIds.contains(id);
    }
}
