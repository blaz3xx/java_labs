import java.time.LocalDate; // дата без часу

// картка з обмеженою кількістю підйомів
public class LiftsSkiPass extends SkiPass { // підклас SkiPass: веде лік підйомів

    private int liftsLeft; // скільки підйомів ще залишилось на картці

    public LiftsSkiPass(int id, SkiPassType type, LocalDate validFrom, LocalDate validUntil) { // конструктор
        super(id, type, validFrom, validUntil); // передаємо спільні дані в базовий клас
        this.liftsLeft = type.getLifts(); // початковий залишок = кількість підйомів з виду картки (10, 20, 50, 100)
    } // кінець конструктора

    public int getLiftsLeft() { // геттер залишку підйомів
        return liftsLeft; // повертаємо залишок
    } // кінець методу

    @Override // реалізація абстрактного методу з SkiPass
    public boolean hasLiftsLeft() { // чи є ще підйоми
        return liftsLeft > 0; // так, якщо залишок більше нуля
    } // кінець методу

    @Override // реалізація абстрактного методу з SkiPass
    public void useLift() { // списання одного підйому
        liftsLeft--; // зменшуємо залишок на 1
    } // кінець методу

    @Override // перевизначаємо toString()
    public String toString() { // опис картки з залишком підйомів
        return super.toString() + " [lifts left: " + liftsLeft + "]"; // базовий опис + залишок у дужках
    } // кінець методу
} // кінець класу LiftsSkiPass
