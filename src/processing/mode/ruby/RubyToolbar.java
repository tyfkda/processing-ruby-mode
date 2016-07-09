package processing.mode.ruby;

import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

public class RubyToolbar extends EditorToolbar {
  private RubyEditor rbeditor;

  public RubyToolbar(Editor editor) {
    super(editor);
    rbeditor = (RubyEditor) editor;
  }

  @Override
  public void handleRun(int modifiers) {
    rbeditor.handleRun();
  }

  @Override
  public void handleStop() {
    rbeditor.handleStop();
  }
}
