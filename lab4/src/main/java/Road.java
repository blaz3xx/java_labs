import java.util.ArrayList;
import java.util.List;

public class Road {

    public List<Vehicle<? extends Human>> carsInRoad = new ArrayList<>();

    public int getCountOfHumans() {
        int count = 0;
        for (Vehicle<? extends Human> car : carsInRoad) {
            count += car.getOccupiedSeats();
        }
        return count;
    }

    public void addCarToRoad(Vehicle<? extends Human> car) {
        carsInRoad.add(car);
    }
}
