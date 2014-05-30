package processing.mode.ruby;

import processing.app.Base;
import processing.mode.java.JavaToolbar;

import java.awt.event.MouseEvent;

public class RubyToolbar extends JavaToolbar {
  public RubyToolbar(RubyEditor editor, Base base) {
    super(editor, base);
  }

  public void handlePressed(MouseEvent e, int sel) {
    boolean shift = e.isShiftDown();
    RubyEditor rbeditor = (RubyEditor) editor;

    System.err.println("handlePressed: " + sel);

    switch (sel) {
    case RUN:
      break;

    case STOP:
      break;

    case OPEN:
      break;

    case NEW:
      break;

    case SAVE:
      break;

    case EXPORT:
      break;
    }
  }
}
