package processing.app.ui.pdex;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;
import processing.app.syntax.PdeInputHandler;
import processing.app.ui.Editor;

public class GeneralInputHandler extends PdeInputHandler {
  private static Pattern reIndent = Pattern.compile("^(\\s+)");

  private final Editor editor;

  public GeneralInputHandler(final Editor editor) {
    this.editor = editor;
  }

  @Override
  public boolean handlePressed(final KeyEvent event) {
    char c = event.getKeyChar();
    int code = event.getKeyCode();

    Sketch sketch = editor.getSketch();
    JEditTextArea textarea = editor.getTextArea();

    if ((event.getModifiers() & InputEvent.META_MASK) != 0) {
      //event.consume();  // does nothing
      return false;
    }

    if ((code == KeyEvent.VK_BACK_SPACE) || (code == KeyEvent.VK_TAB) ||
        (code == KeyEvent.VK_ENTER) || ((c >= 32) && (c < 128))) {
      sketch.setModified(true);
    }

    if (code == KeyEvent.VK_ENTER) {
      textarea.setSelectedText(newline());
    }
    return false;
  }

  private String newline() {
    final JEditTextArea textArea = editor.getTextArea();
    final String line = textArea.getLineText(textArea.getCaretLine());
    Matcher m = reIndent.matcher(line);
    if (!m.find())
      return "\n";
    return "\n" + m.group(1);
  }
}
