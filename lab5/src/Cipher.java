import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;

public class Cipher {

    public static String encrypt(String text, char key) {
        StringWriter result = new StringWriter();
        try (EncryptWriter writer = new EncryptWriter(result, key)) {
            writer.write(text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result.toString();
    }

    public static String decrypt(String text, char key) {
        StringWriter result = new StringWriter();
        try (DecryptReader reader = new DecryptReader(new StringReader(text), key)) {
            reader.transferTo(result);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result.toString();
    }
}
