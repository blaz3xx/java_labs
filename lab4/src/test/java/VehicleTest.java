import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {

    @Test
    void busTakesAnyKindOfPeople() {
        Bus bus = new Bus();
        bus.board(new Passenger("Anna"));
        bus.board(new Firefighter("Ivan"));
        bus.board(new Policeman("Oleh"));

        assertEquals(3, bus.getOccupiedSeats());
    }

    @Test
    void taxiTakesAnyKindOfPeople() {
        Taxi taxi = new Taxi();
        taxi.board(new Passenger("Anna"));
        taxi.board(new Firefighter("Ivan"));
        taxi.board(new Policeman("Oleh"));

        assertEquals(3, taxi.getOccupiedSeats());
    }

    @Test
    void fireTruckTakesFirefighters() {
        FireTruck fireTruck = new FireTruck();
        fireTruck.board(new Firefighter("Ivan"));
        fireTruck.board(new Firefighter("Taras"));

        assertEquals(2, fireTruck.getOccupiedSeats());
    }

    @Test
    void policeCarTakesPolicemen() {
        PoliceCar policeCar = new PoliceCar();
        policeCar.board(new Policeman("Oleh"));

        assertEquals(1, policeCar.getOccupiedSeats());
    }

    @Test
    void eachVehicleHasItsOwnNumberOfSeats() {
        assertEquals(30, new Bus().getMaxSeats());
        assertEquals(4, new Taxi().getMaxSeats());
        assertEquals(6, new FireTruck().getMaxSeats());
        assertEquals(4, new PoliceCar().getMaxSeats());
    }

    @Test
    void boardingFullVehicleThrowsException() {
        Taxi taxi = new Taxi();
        for (int i = 1; i <= 4; i++) {
            taxi.board(new Passenger("Passenger " + i));
        }

        assertThrows(NoFreeSeatsException.class, () -> taxi.board(new Passenger("Extra")));
        assertEquals(4, taxi.getOccupiedSeats());
    }

    @Test
    void getOffRemovesPassenger() {
        PoliceCar policeCar = new PoliceCar();
        Policeman oleh = new Policeman("Oleh");
        policeCar.board(oleh);

        policeCar.getOff(oleh);

        assertEquals(0, policeCar.getOccupiedSeats());
    }

    @Test
    void getOffPassengerWhoIsNotInsideThrowsException() {
        Bus bus = new Bus();
        bus.board(new Passenger("Anna"));

        assertThrows(PassengerNotFoundException.class, () -> bus.getOff(new Passenger("Petro")));
        assertEquals(1, bus.getOccupiedSeats());
    }

    @Test
    void busCanTakeAWholeTeamOfFirefighters() {
        Bus bus = new Bus();
        List<Firefighter> team = List.of(new Firefighter("Ivan"), new Firefighter("Taras"));

        bus.boardAll(team);

        assertEquals(2, bus.getOccupiedSeats());
        assertTrue(bus.getPassengers().containsAll(team));
    }

    @Test
    void groupThatDoesNotFitIsNotBoardedAtAll() {
        Taxi taxi = new Taxi();
        taxi.boardAll(List.of(new Passenger("A"), new Passenger("B"), new Passenger("C")));

        assertThrows(NoFreeSeatsException.class,
                () -> taxi.boardAll(List.of(new Passenger("D"), new Passenger("E"))));
        assertEquals(3, taxi.getOccupiedSeats());
    }
}
