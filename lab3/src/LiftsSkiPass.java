import java.time.LocalDate;

// a card with a limited number of lifts
public class LiftsSkiPass extends SkiPass {

    private int liftsLeft;

    public LiftsSkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) {
        super(id, type, validFrom, validUntil);
        this.liftsLeft = type.getLifts();
    }

    public int getLiftsLeft() {
        return liftsLeft;
    }

    @Override
    public boolean hasLiftsLeft() {
        return liftsLeft > 0;
    }

    @Override
    public void useLift() {
        liftsLeft--;
    }

    @Override
    public String toString() {
        return super.toString() + " [lifts left: " + liftsLeft + "]";
    }
}
