import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class EncryptWriter extends FilterWriter {

    private final char key;

    public EncryptWriter(Writer out, char key) {
        super(out);
        this.key = key;
    }

    @Override
    public void write(int c) throws IOException {
        super.write((char) (c + key));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            write(cbuf[i]);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            write(str.charAt(i));
        }
    }
}
