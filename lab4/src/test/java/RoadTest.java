import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoadTest {

    @Test
    void emptyRoadHasNoPeople() {
        assertEquals(0, new Road().getCountOfHumans());
    }

    @Test
    void countsPeopleInAllKindsOfVehicles() {
        Bus bus = new Bus();
        bus.board(new Passenger("Anna"));
        bus.board(new Policeman("Oleh"));

        FireTruck fireTruck = new FireTruck();
        fireTruck.board(new Firefighter("Ivan"));

        PoliceCar policeCar = new PoliceCar();
        Taxi taxi = new Taxi();
        taxi.board(new Firefighter("Taras"));

        Road road = new Road();
        road.addCarToRoad(bus);
        road.addCarToRoad(fireTruck);
        road.addCarToRoad(policeCar);
        road.addCarToRoad(taxi);

        assertEquals(4, road.carsInRoad.size());
        assertEquals(4, road.getCountOfHumans());
    }
}
