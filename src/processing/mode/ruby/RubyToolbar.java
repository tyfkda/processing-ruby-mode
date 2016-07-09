package processing.mode.ruby;

import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

public class RubyToolbar extends EditorToolbar {
  private RubyEditor rbEditor;

  public RubyToolbar(RubyEditor editor) {
    super(editor);
    rbEditor = editor;
  }

  @Override
  public void handleRun(int modifiers) {
    rbEditor.handleRun();
  }

  @Override
  public void handleStop() {
    rbEditor.handleStop();
  }
}
