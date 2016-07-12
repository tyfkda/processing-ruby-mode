package processing.mode.ruby.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import org.junit.Test;

public class StreamFilterTest {
  private static void putString(OutputStream stream, String string) throws IOException {
    for (char ch : string.toCharArray()) {
      stream.write(ch);
    }
  }

  @Test
  public void testFiltered() throws IOException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    StreamFilter filter = new StreamFilter(ostream, Pattern.compile("PATTERN"), "REPLACED");
    putString(filter, "I have a 'PATTERN' string.\n");
    assertEquals(ostream.toString(), "I have a 'REPLACED' string.\n");
  }

  @Test
  public void testVaryLength() throws IOException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    StreamFilter filter = new StreamFilter(ostream, Pattern.compile("PATTERN"), "");
    putString(filter, "short\n");
    putString(filter, "longlonglong\n");
    putString(filter, "middle\n");
    assertEquals(ostream.toString(), "short\nlonglonglong\nmiddle\n");
  }

  @Test
  public void testBufferExpand() throws IOException {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 100; ++i)
      sb.append("0123456789");
    sb.append("\n");
    String longString = sb.toString();

    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    StreamFilter filter = new StreamFilter(ostream, Pattern.compile("x"), "");
    putString(filter, longString);
    assertEquals(ostream.toString(), longString);
  }
}
