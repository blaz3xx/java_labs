package reflection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringReflectionTest {

    @Test
    void reflectionIsAvailable() {
        assertTrue(StringReflection.isAvailable());
    }

    @Test
    void changesStringLiteral() throws Exception {
        String text = "lab10-literal-original";

        StringReflection.replaceValue(text, "lab10-literal-changed!");

        assertEquals("lab10-literal-changed!", text);
    }

    @Test
    void changesStringCreatedFromKeyboardInput() throws Exception {
        String typed = new String("typed by the user".toCharArray());
        String sameObject = typed;

        StringReflection.replaceValue(typed, "new value");

        assertEquals("new value", typed);
        assertSame(typed, sameObject);
    }

    @Test
    void literalChangeIsVisibleThroughEveryReferenceToTheLiteral() throws Exception {
        String first = "lab10-shared-literal";
        String second = "lab10-shared-literal";

        StringReflection.replaceValue(first, "lab10-modified-shared");

        assertSame(first, second);
        assertEquals("lab10-modified-shared", second);
    }

    @Test
    void newValueMayBeLongerOrShorter() throws Exception {
        String text = new String("medium length".toCharArray());

        StringReflection.replaceValue(text, "x");
        assertEquals("x", text);
        assertEquals(1, text.length());

        StringReflection.replaceValue(text, "a considerably longer replacement value");
        assertEquals("a considerably longer replacement value", text);
        assertEquals(39, text.length());
    }

    @Test
    void worksWithNonLatinText() throws Exception {
        String text = new String("hello".toCharArray());

        StringReflection.replaceValue(text, "Привіт, світе!");

        assertEquals("Привіт, світе!", text);
        assertEquals(14, text.length());
        assertEquals('П', text.charAt(0));

        StringReflection.replaceValue(text, "back to latin");
        assertEquals("back to latin", text);
    }

    @Test
    void cachedHashCodeIsResetSoTheStringStaysConsistent() throws Exception {
        String text = new String("original".toCharArray());
        int oldHash = text.hashCode();

        StringReflection.replaceValue(text, "replaced");

        assertNotEquals(oldHash, text.hashCode());
        assertEquals("replaced".hashCode(), text.hashCode());
    }

    @Test
    void replacementDoesNotAffectTheSourceOfTheNewValue() throws Exception {
        String source = "source value";
        String text = new String("target".toCharArray());

        StringReflection.replaceValue(text, source);
        StringReflection.replaceValue(text, "changed again");

        assertEquals("source value", source);
    }

    @Test
    void describesInternalRepresentation() throws Exception {
        String latin = new String("abc".toCharArray());
        String cyrillic = new String("абв".toCharArray());

        String type = StringReflection.valueFieldType();
        assertTrue(type.equals("byte[]") || type.equals("char[]"));
        assertTrue(StringReflection.describeInternals(latin).contains("[3]"));
        if (type.equals("byte[]")) {
            assertTrue(StringReflection.describeInternals(latin).contains("LATIN1"));
            assertTrue(StringReflection.describeInternals(cyrillic).contains("UTF16"));
        }
    }
}
