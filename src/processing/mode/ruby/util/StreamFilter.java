package processing.mode.ruby.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamFilter extends OutputStream {
  private final static int INITIAL_LENGTH = 256;
  private final static Charset UTF8 = Charset.forName("UTF-8");

  private OutputStream stream;
  private Pattern pattern;
  private String replace;
  private byte[] bytes;
  private int pointer;

  public StreamFilter(OutputStream stream, Pattern pattern, String replace) {
    this.stream = stream;
    this.pattern = pattern;
    this.replace = replace;
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
      stream.write(m.replaceAll(replace).getBytes(UTF8));
    else
      stream.write(line.getBytes(UTF8));
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
