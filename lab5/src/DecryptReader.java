import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class DecryptReader extends FilterReader {

    private final char key;

    public DecryptReader(Reader in, char key) {
        super(in);
        this.key = key;
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        return c == -1 ? -1 : (char) (c - key);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = super.read(cbuf, off, len);
        for (int i = off; i < off + count; i++) {
            cbuf[i] = (char) (cbuf[i] - key);
        }
        return count;
    }
}
