package reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class StringReflection {

    private StringReflection() {
    }

    public static boolean isAvailable() {
        try {
            valueField();
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }

    public static String valueFieldType() throws ReflectiveOperationException {
        return valueField().getType().getSimpleName();
    }

    public static void replaceValue(String target, String newValue) throws ReflectiveOperationException {
        Field value = valueField();
        String donor = new String(newValue.toCharArray());

        value.set(target, value.get(donor));

        Field coder = optionalField("coder");
        if (coder != null) {
            coder.set(target, coder.get(donor));
        }

        Field hash = optionalField("hash");
        if (hash != null) {
            hash.setInt(target, 0);
        }
        Field hashIsZero = optionalField("hashIsZero");
        if (hashIsZero != null) {
            hashIsZero.setBoolean(target, false);
        }
    }

    public static String describeInternals(String text) throws ReflectiveOperationException {
        Object array = valueField().get(text);
        String result = valueField().getType().getSimpleName().replace("[]", "") + "[" + Array.getLength(array) + "]";

        Field coder = optionalField("coder");
        if (coder != null) {
            result += ", coder=" + (coder.getByte(text) == 0 ? "LATIN1" : "UTF16");
        }
        return result;
    }

    private static Field valueField() throws ReflectiveOperationException {
        Field field = String.class.getDeclaredField("value");
        field.setAccessible(true);
        return field;
    }

    private static Field optionalField(String name) throws ReflectiveOperationException {
        try {
            Field field = String.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
