package processing.app.ui.pdex;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import processing.app.Mode;
import processing.app.syntax.JEditTextArea;
import processing.app.syntax.TextAreaDefaults;
import processing.app.syntax.TextAreaPainter;
import processing.app.ui.Editor;

public class GeneralTextArea extends JEditTextArea {
  /**
   * Sets default cursor (instead of text cursor) in the gutter area.
   */
  protected final MouseMotionAdapter gutterCursorMouseAdapter = new MouseMotionAdapter() {
    private int lastX; // previous horizontal positon of the mouse cursor

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
    super(defaults, new GeneralInputHandler(editor));

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

  /**
   * Convert a character offset to a horizontal pixel position inside the text
   * area. Overridden to take gutter width into account.
   *
   * @param line
   *          the 0-based line number
   * @param offset
   *          the character offset (0 is the first character on a line)
   * @return the horizontal position
   */
  @Override
  public int _offsetToX(int line, int offset) {
    return super._offsetToX(line, offset) + Editor.LEFT_GUTTER;
  }

  /**
   * Convert a horizontal pixel position to a character offset. Overridden to
   * take gutter width into account.
   *
   * @param line
   *          the 0-based line number
   * @param x
   *          the horizontal pixel position
   * @return he character offset (0 is the first character on a line)
   */
  @Override
  public int xToOffset(int line, int x) {
    return super.xToOffset(line, x - Editor.LEFT_GUTTER);
  }
}
