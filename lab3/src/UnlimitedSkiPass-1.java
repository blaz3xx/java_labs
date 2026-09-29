import java.time.LocalDate; // дата без часу

// картка без ліку підйомів (пів дня, N днів, сезон)
public class UnlimitedSkiPass extends SkiPass { // підклас SkiPass: підйоми не рахуються

    public UnlimitedSkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) { // конструктор
        super(id, type, validFrom, validUntil); // передаємо всі дані в конструктор базового класу
    } // кінець конструктора

    @Override // реалізація абстрактного методу з SkiPass
    public boolean hasLiftsLeft() { // чи є ще підйоми
        return true; // ліку немає, тому підйомів «завжди вистачає»
    } // кінець методу

    @Override // реалізація абстрактного методу з SkiPass
    public void useLift() { // списання підйому
        // нічого рахувати
    } // кінець методу
} // кінець класу UnlimitedSkiPass
