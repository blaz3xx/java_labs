import java.time.LocalDate;

// a card without counting lifts
public class UnlimitedSkiPass extends SkiPass {

    public UnlimitedSkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) {
        super(id, type, validFrom, validUntil);
    }

    @Override
    public boolean hasLiftsLeft() {
        return true;
    }

    @Override
    public void useLift() {
        // nothing to count
        // -_- 
    }
}
