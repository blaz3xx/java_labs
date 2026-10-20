package i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @AfterEach
    void restoreEnglish() {
        Messages.setLocale(Locale.ENGLISH);
    }

    private static ResourceBundle bundle(Locale locale) {
        return ResourceBundle.getBundle(Messages.BASE_NAME, locale,
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    }

    private static Set<String> keys(Locale locale) {
        return new TreeSet<>(Collections.list(bundle(locale).getKeys()));
    }

    private static Set<String> placeholders(String text) {
        Set<String> found = new TreeSet<>();
        Matcher matcher = Pattern.compile("\\{\\d+}").matcher(text);
        while (matcher.find()) {
            found.add(matcher.group());
        }
        return found;
    }

    @Test
    void everyLanguageHasTheSameKeysAsTheBaseBundle() {
        Set<String> base = keys(Locale.ENGLISH);
        for (Locale locale : Messages.SUPPORTED) {
            assertEquals(base, keys(locale), locale.toString());
        }
    }

    @Test
    void everyLanguageUsesTheSamePlaceholders() {
        ResourceBundle english = bundle(Locale.ENGLISH);
        for (Locale locale : Messages.SUPPORTED) {
            ResourceBundle other = bundle(locale);
            for (String key : english.keySet()) {
                assertEquals(placeholders(english.getString(key)), placeholders(other.getString(key)),
                        locale + " / " + key);
            }
        }
    }

    @Test
    void switchingLocaleChangesTheTexts() {
        Messages.setLocale(Locale.ENGLISH);
        String english = Messages.get("menu.exit");

        Messages.setLocale(Locale.of("uk"));
        String ukrainian = Messages.get("menu.exit");

        Messages.setLocale(Locale.GERMAN);
        String german = Messages.get("menu.exit");

        assertEquals("Exit", english);
        assertEquals("Вихід", ukrainian);
        assertEquals("Beenden", german);
        assertNotEquals(english, german);
    }

    @Test
    void englishDoesNotFallBackToTheSystemLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.of("uk"));
            Messages.setLocale(Locale.ENGLISH);
            assertEquals("Exit", Messages.get("menu.exit"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void parametersAreInsertedIntoTheMessage() {
        Messages.setLocale(Locale.of("uk"));

        assertEquals("Файл не знайдено: data/a.txt", Messages.get("error.notfound", "data/a.txt"));
    }

    @Test
    void unknownKeyIsMarkedInsteadOfFailing() {
        assertEquals("!no.such.key!", Messages.get("no.such.key"));
    }

    @Test
    void languageNamesAreWrittenInTheirOwnLanguage() {
        assertEquals("English", Messages.languageName(Locale.ENGLISH));
        assertEquals("Українська", Messages.languageName(Locale.of("uk")));
        assertEquals("Deutsch", Messages.languageName(Locale.GERMAN));
    }

    @Test
    void germanKeepsUmlauts() {
        Messages.setLocale(Locale.GERMAN);

        assertTrue(Messages.get("lang.title").contains("wählen"));
    }
}
