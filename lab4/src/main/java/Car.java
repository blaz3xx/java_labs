public abstract class Car<T extends Human> extends Vehicle<T> {

    protected Car(int maxSeats) {
        super(maxSeats);
    }
}
