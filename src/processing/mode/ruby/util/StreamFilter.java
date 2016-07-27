package processing.mode.ruby.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Replace messages and output them to ostream.
public class StreamFilter extends OutputStream {
  private final static int INITIAL_LENGTH = 256;
  private final static Charset UTF8 = Charset.forName("UTF-8");

  private Pattern pattern;
  private String replace;
  private OutputStream ostream;
  private byte[] bytes;
  private int pointer;

  public StreamFilter(Pattern pattern, String replace, OutputStream ostream) {
    this.pattern = pattern;
    this.replace = replace;
    this.ostream = ostream;
    bytes = new byte[INITIAL_LENGTH];
    clear();
  }

  @Override
  public void close() throws IOException {
    output();
    super.close();
  }

  @Override
  public void write(int b) throws IOException {
    bytes[pointer++] = (byte) b;
    if (pointer >= bytes.length)
      expand();
    if (b == '\n')
      output();
  }

  public void print(String line) throws IOException {
    Matcher m = pattern.matcher(line);
    if (m.find())
      ostream.write(m.replaceAll(replace).getBytes(UTF8));
    else
      ostream.write(line.getBytes(UTF8));
  }

  private void output() throws IOException {
    String line = new String(bytes, 0, pointer, UTF8);
    print(line);
    clear();
  }

  private void clear() {
    pointer = 0;
  }

  private void expand() {
    int newLength = bytes.length * 2;
    byte[] newBytes = Arrays.copyOf(bytes, newLength);
    bytes = newBytes;
  }
}
