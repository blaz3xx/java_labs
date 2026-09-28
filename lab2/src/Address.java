public class Address {

    private String street;
    private String house;
    private int apartment;

    public Address(String street, String house, int apartment) {
        this.street = street;
        this.house = house;
        this.apartment = apartment;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public int getApartment() {
        return apartment;
    }

    @Override
    public String toString() {
        return street + " St., bld. " + house + ", apt. " + apartment;
    }
}
