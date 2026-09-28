import java.util.ArrayList;

// the journal stores all records
public class CuratorJournal {

    private ArrayList<JournalEntry> entries = new ArrayList<>();

    public void addEntry(JournalEntry entry) {
        entries.add(entry);
    }

    public void printAll() {
        if (entries.isEmpty()) {
            System.out.println("The journal is empty.");
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i));
        }
    }
}
