import java.time.LocalDate; // дата без часу
import java.util.ArrayList; // динамічний список

// реєстр усіх виданих карток
public class SkiPassSystem { // система: випускає картки, веде реєстр і список заблокованих

    public static final LocalDate SEASON_START = LocalDate.of(2026, 12, 1); // початок лижного сезону (спільна константа)
    public static final LocalDate SEASON_END = LocalDate.of(2027, 4, 30); // кінець лижного сезону

    private ArrayList<SkiPass> issuedCards = new ArrayList<>(); // реєстр усіх виданих карток
    private ArrayList<Integer> blockedIds = new ArrayList<>(); // номери заблокованих карток
    private int nextId = 1; // лічильник для видачі унікальних номерів

    public SkiPass issue(SkiPassType type, LocalDate startDate) { // випуск нової картки заданого виду з датою початку
        SkiPass card; // змінна типу базового класу – туди потрапить будь-який підклас (поліморфізм)
        if (type == SkiPassType.SEASON) { // абонемент на сезон
            card = new UnlimitedSkiPass(nextId, type, SEASON_START, SEASON_END); // діє весь сезон, підйоми не рахуються
        } else if (type.getLifts() > 0) { // картка за кількістю підйомів
            // картки з підйомами діють до кінця сезону
            card = new LiftsSkiPass(nextId, type, startDate, SEASON_END); // з дати видачі до кінця сезону, з лічильником підйомів
        } else { // решта: картки на пів дня / N днів
            LocalDate lastDay = startDate.plusDays(type.getDays() - 1); // останній день = старт + (днів − 1)
            card = new UnlimitedSkiPass(nextId, type, startDate, lastDay); // картка без ліку підйомів на N днів
        } // кінець if/else
        nextId++; // наступна картка отримає наступний номер
        issuedCards.add(card); // заносимо картку в реєстр
        return card; // повертаємо картку тому, хто її замовив
    } // кінець методу issue

    public void block(int id) { // блокування картки за порушення правил
        if (!blockedIds.contains(id)) { // якщо номера ще немає у списку заблокованих
            blockedIds.add(id); // додаємо, щоб не було дублікатів
        } // кінець умови
    } // кінець методу

    // повертає null, якщо картки з таким номером немає
    public SkiPass findById(int id) { // пошук картки в реєстрі за номером
        for (SkiPass card : issuedCards) { // перебираємо всі видані картки
            if (card.getId() == id) { // номер збігся
                return card; // знайшли – повертаємо картку
            } // кінець умови
        } // кінець циклу
        return null; // не знайшли – null (турнікет трактує це як «картку неможливо прочитати»)
    } // кінець методу

    public boolean isBlocked(int id) { // чи заблокована картка з цим номером
        return blockedIds.contains(id); // true, якщо номер є у списку заблокованих
    } // кінець методу
} // кінець класу SkiPassSystem
