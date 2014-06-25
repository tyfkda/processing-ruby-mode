package processing.mode.ruby;

import javax.swing.text.Segment;

import processing.app.Editor;
import processing.app.syntax.KeywordMap;
import processing.app.syntax.Token;
import processing.app.syntax.TokenMarker;

public class RubyTokenMarker extends TokenMarker {
  private KeywordMap keywordColoring;

  private int lastOffset;
  private int lastKeyword;

  /**
   * Add a keyword, and the associated coloring. KEYWORD2 and KEYWORD3
   * should only be used with functions (where parens are present).
   * This is done for the extra paren handling.
   * @param coloring one of KEYWORD1, KEYWORD2, LITERAL1, etc.
   */
  public void addColoring(String keyword, String coloring) {
    if (keywordColoring == null) {
      keywordColoring = new KeywordMap(false);
    }
    // KEYWORD1 -> 0, KEYWORD2 -> 1, etc
    int num = coloring.charAt(coloring.length() - 1) - '1';
    int id = 0;
    boolean paren = false;
    switch (coloring.charAt(0)) {
      case 'K': id = Token.KEYWORD1 + num; break;
      case 'L': id = Token.LITERAL1 + num; break;
      case 'F': id = Token.FUNCTION1 + num; paren = true; break;
    }
    keywordColoring.add(keyword, (byte) id, paren);
  }


  public byte markTokensImpl(byte token, Segment line, int lineIndex) {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    int mlength = offset + line.count;
    boolean backslash = false;

    loop: for (int i = offset; i < mlength; i++) {
      int i1 = (i + 1);

      char c = array[i];
      if (c == '\\') {
        backslash = !backslash;
        continue;
      }

      switch (token) {
      case Token.NULL:
        switch (c) {
        case '#':
          addToken(i - lastOffset, token);
          addToken(mlength - i, Token.COMMENT1);
          lastOffset = lastKeyword = mlength;
          break loop;
        case '"':
          doKeyword(line, i, c);
          if (backslash)
            backslash = false;
          else {
            addToken(i - lastOffset, token);
            token = Token.LITERAL1;
            lastOffset = lastKeyword = i;
          }
          break;
        case '\'':
          doKeyword(line, i, c);
          if (backslash)
            backslash = false;
          else {
            addToken(i - lastOffset, token);
            token = Token.LITERAL2;
            lastOffset = lastKeyword = i;
          }
          break;
        case ':':
          if (lastKeyword == offset) {
            if (doKeyword(line, i, c))
              break;
            backslash = false;
            addToken(i1 - lastOffset, Token.LABEL);
            lastOffset = lastKeyword = i1;
          } else if (doKeyword(line, i, c))
            break;
          break;
        default:
          backslash = false;
          if (!Character.isLetterOrDigit(c) && c != '_') {
            doKeyword(line, i, c);
          }
          break;
        }
        break;
      case Token.COMMENT1:
      case Token.COMMENT2:
        backslash = false;
        if (c == '*' && mlength - i > 1) {
          if (array[i1] == '/') {
            i++;
            addToken((i + 1) - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i + 1;
          }
        }
        break;
      case Token.LITERAL1:
        if (backslash)
          backslash = false;
        else if (c == '"') {
          addToken(i1 - lastOffset, token);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }
        break;
      case Token.LITERAL2:
        if (backslash)
          backslash = false;
        else if (c == '\'') {
          addToken(i1 - lastOffset, Token.LITERAL1);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }
        break;
      default:
        throw new InternalError("Invalid state: " + token);
      }
    }

    if (token == Token.NULL) {
      doKeyword(line, mlength, '\0');
    }

    switch (token) {
    case Token.LITERAL1:
    case Token.LITERAL2:
      addToken(mlength - lastOffset, Token.INVALID);
      token = Token.NULL;
      break;
    case Token.KEYWORD2:
      addToken(mlength - lastOffset, token);
      if (!backslash)
        token = Token.NULL;
      addToken(mlength - lastOffset, token);
      break;
    default:
      addToken(mlength - lastOffset, token);
      break;
    }
    return token;
  }


  private boolean doKeyword(Segment line, int i, char c) {
    int i1 = i + 1;
    int len = i - lastKeyword;

    boolean paren = Editor.checkParen(line.array, i, line.array.length);

    byte id = keywordColoring.lookup(line, lastKeyword, len, paren);
    if (id != Token.NULL) {
      if (lastKeyword != lastOffset) {
        addToken(lastKeyword - lastOffset, Token.NULL);
      }
      addToken(len, id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}
