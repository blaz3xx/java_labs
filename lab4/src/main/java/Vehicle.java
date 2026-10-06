import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Vehicle<T extends Human> {

    private final int maxSeats;
    private final List<T> passengers = new ArrayList<>();

    protected Vehicle(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getOccupiedSeats() {
        return passengers.size();
    }

    public List<T> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    public void board(T passenger) {
        if (passengers.size() >= maxSeats) {
            throw new NoFreeSeatsException(getClass().getSimpleName() + " is full (" + maxSeats + " seats)");
        }
        passengers.add(passenger);
    }

    public void boardAll(Collection<? extends T> group) {
        if (passengers.size() + group.size() > maxSeats) {
            throw new NoFreeSeatsException("Not enough seats in " + getClass().getSimpleName()
                    + " for " + group.size() + " people");
        }
        passengers.addAll(group);
    }

    public void getOff(T passenger) {
        if (!passengers.remove(passenger)) {
            throw new PassengerNotFoundException(passenger + " is not in the " + getClass().getSimpleName());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + getOccupiedSeats() + "/" + maxSeats + "] " + passengers;
    }
}
