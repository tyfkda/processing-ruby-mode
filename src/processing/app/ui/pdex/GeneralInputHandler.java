package processing.app.ui.pdex;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;
import processing.app.syntax.PdeInputHandler;
import processing.app.ui.Editor;

public class GeneralInputHandler extends PdeInputHandler {
  private static Pattern reIndent = Pattern.compile("^(\\s+)");

  public GeneralInputHandler(final Editor editor) {
    super(editor);
  }

  @Override
  public boolean handlePressed(final KeyEvent event) {
    char c = event.getKeyChar();
    JEditTextArea textarea = editor.getTextArea();

    if ((event.getModifiers() & InputEvent.META_MASK) != 0) {
      //event.consume();  // does nothing
      return false;
    }

    switch (c) {
    case 9:  // Tab
      if ((event.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
        // if shift is down, the user always expects an outdent
        // http://code.google.com/p/processing/issues/detail?id=458
        editor.handleOutdent();
      } else if (textarea.isSelectionActive()) {
        editor.handleIndent();
      } else if (Preferences.getBoolean("editor.tabs.expand")) {
        int tabSize = Preferences.getInteger("editor.tabs.size");
        textarea.setSelectedText(spaces(tabSize));
        event.consume();
      } else {  // !Preferences.getBoolean("editor.tabs.expand")
        textarea.setSelectedText("\t");
        event.consume();
      }
      break;

    case 10: case 13:  // Enter
      if (Preferences.getBoolean("editor.indent")) {
        appendSpacesToSelection();
        textarea.setSelectedText(newline());
      } else {
        // Enter/Return was being consumed by somehow even if false
        // was returned, so this is a band-aid to simply fire the event again.
        // http://dev.processing.org/bugs/show_bug.cgi?id=1073
        textarea.setSelectedText(String.valueOf(c));
      }
      // mark this event as already handled (all but ignored)
      event.consume();
      break;
    }
    return false;
  }

  @Override
  public boolean handleTyped(final KeyEvent event) {
    // Set sketch as modified here.
    Sketch sketch = editor.getSketch();
    sketch.setModified(true);

    return super.handleTyped(event);
  }

  private void appendSpacesToSelection() {
    final JEditTextArea textArea = editor.getTextArea();
    final int lineNo = textArea.getCaretLine();
    final String line = textArea.getLineText(lineNo);
    final int n = line.length();
    final int lineStart = textArea.getLineStartOffset(lineNo);
    final int caretPos = textArea.getCaretPosition();

    // Back (for trimming ending whitespaces.)
    final int start = Math.min(caretPos, textArea.getSelectionStart()) - lineStart;
    int s = start;
    while (s > 0 &&
           (line.charAt(s - 1) == ' ' || line.charAt(s - 1) == '\t'))
      --s;

    // Front (to align indent.)
    final int end = Math.max(caretPos, textArea.getSelectionStop()) - lineStart;
    int e = end;
    while (e < n &&
           (line.charAt(e) == ' ' || line.charAt(e) == '\t'))
      ++e;

    if (s < start || e > end)
      textArea.select(s + lineStart, e + lineStart);
  }

  private String newline() {
    final JEditTextArea textArea = editor.getTextArea();
    final String line = textArea.getLineText(textArea.getCaretLine());
    Matcher m = reIndent.matcher(line);
    if (!m.find())
      return "\n";
    return "\n" + m.group(1);
  }

  static private String spaces(int count) {
    char[] c = new char[count];
    Arrays.fill(c, ' ');
    return new String(c);
  }
}
