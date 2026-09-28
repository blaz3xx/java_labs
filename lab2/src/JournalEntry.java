import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// one record in the curator's journal
public class JournalEntry {

    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String phone;
    private Address address;

    public JournalEntry(String lastName, String firstName, LocalDate birthDate, String phone, Address address) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        String date = birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return lastName + " " + firstName
                + " | born: " + date
                + " | phone: " + phone
                + " | address: " + address;
    }
}
