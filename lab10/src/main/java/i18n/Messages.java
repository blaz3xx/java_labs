package i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {

    public static final String BASE_NAME = "location.messages";
    public static final List<Locale> SUPPORTED = List.of(Locale.ENGLISH, Locale.of("uk"), Locale.GERMAN);

    private static final ResourceBundle.Control CONTROL =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private static Locale locale = Locale.ENGLISH;
    private static ResourceBundle bundle = load(locale);

    private Messages() {
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale newLocale) {
        bundle = load(newLocale);
        locale = newLocale;
    }

    public static String get(String key, Object... args) {
        String pattern;
        try {
            pattern = bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
        return args.length == 0 ? pattern : new MessageFormat(pattern, locale).format(args);
    }

    public static String languageName(Locale target) {
        return load(target).getString("lang.name");
    }

    private static ResourceBundle load(Locale target) {
        return ResourceBundle.getBundle(BASE_NAME, target, CONTROL);
    }
}
