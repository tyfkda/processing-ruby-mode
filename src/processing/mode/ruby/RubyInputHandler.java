package processing.mode.ruby;

import java.awt.event.KeyEvent;

import processing.app.syntax.JEditTextArea;
import processing.app.syntax.PdeInputHandler;
import processing.app.ui.Editor;

public class RubyInputHandler extends PdeInputHandler {
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
    return "\n";
  }

  private JEditTextArea getTextArea() {
    return rbEditor.getTextArea();
  }
}
