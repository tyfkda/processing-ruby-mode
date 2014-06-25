package processing.mode.ruby;

import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Segment;

import processing.app.syntax.Token;
import processing.app.syntax.TokenMarker;

public class RubyTokenMarker extends TokenMarker {
  private final Map<String, String> keywords = new HashMap<String, String>();

  public RubyTokenMarker() {
  }

  @Override
  public void addColoring(final String keyword, final String coloring) {
    System.err.println("addColoring: " + keyword + " = " + coloring);
    keywords.put(keyword, coloring);
  }

  protected byte markTokensImpl(byte token, Segment line, int lineIndex) {
    System.err.println("markTokensImpl: " + token + ", line=" + line + ", line.offset=" + line.offset + ", line.count=" + line.count + ", " + lineIndex);
    addToken(line.count, Token.COMMENT1);
    return (byte) 123;
  }
}
