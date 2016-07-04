package processing.mode.ruby;

import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.app.syntax.JEditTextArea;
import processing.app.syntax.PdeInputHandler;
import processing.app.ui.Editor;

public class RubyInputHandler extends PdeInputHandler {
  private static Pattern reIndent = Pattern.compile("^(\\s+)");

  private final RubyEditor rbEditor;

  public RubyInputHandler(final Editor editor) {
    rbEditor = (RubyEditor) editor;
  }

  @Override
  public boolean handlePressed(final KeyEvent event) {
    final JEditTextArea textArea = getTextArea();
    final int code = event.getKeyCode();
    if (code == KeyEvent.VK_ENTER) {
      textArea.setSelectedText(newline());
    }
    return false;
  }

  private String newline() {
    final JEditTextArea textArea = getTextArea();
    final String line = textArea.getLineText(textArea.getCaretLine());
    Matcher m = reIndent.matcher(line);
    if (!m.find())
      return "\n";
    return "\n" + m.group(1);
  }

  private JEditTextArea getTextArea() {
    return rbEditor.getTextArea();
  }
}
