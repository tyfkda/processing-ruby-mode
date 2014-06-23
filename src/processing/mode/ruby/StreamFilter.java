package processing.mode.ruby;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamFilter extends OutputStream {
  private OutputStream stream;
  private Pattern pattern;
  private String replace;
  private ByteBuffer buffer;

  public StreamFilter(OutputStream stream, Pattern pattern, String replace) {
    this.stream = stream;
    this.pattern = pattern;
    this.replace = replace;
    buffer = ByteBuffer.allocate(1024);
  }

  @Override
  public void close() throws IOException {
    output();
    super.close();
  }

  @Override
  public void write(int b) throws IOException {
    buffer.put((byte) b);
    if (b == '\n')
      output();
  }

  private void output() throws IOException {
    byte[] bytes = buffer.array();
    String line = new String(bytes, "UTF-8");
    Matcher m = pattern.matcher(line);
    if (m.find())
      stream.write(m.replaceAll(replace).getBytes("UTF-8"));
    else
      stream.write(bytes);
    buffer.clear();
  }
}
