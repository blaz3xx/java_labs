import java.util.List;

public class Main {

    public static void main(String[] args) {
        Bus bus = new Bus();
        Taxi taxi = new Taxi();
        FireTruck fireTruck = new FireTruck();
        PoliceCar policeCar = new PoliceCar();

        Passenger anna = new Passenger("Anna");

        bus.board(anna);
        bus.board(new Firefighter("Ivan"));
        bus.board(new Policeman("Oleh"));
        taxi.board(new Passenger("Petro"));

        fireTruck.boardAll(List.of(new Firefighter("Taras"), new Firefighter("Maksym")));
        policeCar.board(new Policeman("Andrii"));
        // fireTruck.board(anna);

        Road road = new Road();
        road.addCarToRoad(bus);
        road.addCarToRoad(taxi);
        road.addCarToRoad(fireTruck);
        road.addCarToRoad(policeCar);

        for (Vehicle<? extends Human> car : road.carsInRoad) {
            System.out.println(car);
        }
        System.out.println("People on the road: " + road.getCountOfHumans());

        System.out.println();
        try {
            taxi.boardAll(List.of(new Passenger("A"), new Passenger("B"), new Passenger("C"), new Passenger("D")));
        } catch (NoFreeSeatsException e) {
            System.out.println("Error: " + e.getMessage());
        }
        try {
            taxi.getOff(anna);
        } catch (PassengerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
