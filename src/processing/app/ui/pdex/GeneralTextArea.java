package processing.app.ui.pdex;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import processing.app.Mode;
import processing.app.syntax.PdeTextArea;
import processing.app.syntax.TextAreaDefaults;
import processing.app.syntax.TextAreaPainter;
import processing.app.ui.Editor;

@SuppressWarnings("serial")
public class GeneralTextArea extends PdeTextArea {
  /**
   * Sets default cursor (instead of text cursor) in the gutter area.
   */
  protected final MouseMotionAdapter gutterCursorMouseAdapter = new MouseMotionAdapter() {
    private int lastX; // previous horizontal position of the mouse cursor

    @Override
    public void mouseMoved(MouseEvent me) {
      if (me.getX() < Editor.LEFT_GUTTER) {
        if (lastX >= Editor.LEFT_GUTTER) {
          painter.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
      } else {
        if (lastX < Editor.LEFT_GUTTER) {
          painter.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }
      }
      lastX = me.getX();
    }
  };

  public GeneralTextArea(TextAreaDefaults defaults, Editor editor) {
    super(defaults, new GeneralInputHandler(editor), editor);

    // change cursor to pointer in the gutter area
    painter.addMouseMotionListener(gutterCursorMouseAdapter);
  }

  @Override
  protected TextAreaPainter createPainter(final TextAreaDefaults defaults) {
    return new GeneralTextAreaPainter(this, defaults);
  }

  protected GeneralTextAreaPainter getCustomPainter() {
    return (GeneralTextAreaPainter) painter;
  }

  public void setMode(Mode mode) {
    getCustomPainter().setMode(mode);
  }
}
