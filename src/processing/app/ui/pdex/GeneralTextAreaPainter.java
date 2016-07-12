package processing.app.ui.pdex;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;

import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import processing.app.Messages;
import processing.app.Mode;
import processing.app.syntax.JEditTextArea;
import processing.app.syntax.TextAreaDefaults;
import processing.app.syntax.TextAreaPainter;
import processing.app.syntax.TokenMarker;
import processing.app.ui.Editor;

@SuppressWarnings("serial")
public class GeneralTextAreaPainter extends TextAreaPainter {
  protected Font gutterTextFont;
  protected Color gutterTextColor;
  protected Image gutterGradient;

  public GeneralTextAreaPainter(final JEditTextArea textArea, TextAreaDefaults defaults) {
    super(textArea, defaults);
  }

  /**
   * Loads theme for TextAreaPainter(XQMode)
   */
  public void setMode(Mode mode) {
    //errorUnderlineColor = mode.getColor("editor.error.underline.color");
    //warningUnderlineColor = mode.getColor("editor.warning.underline.color");

    gutterTextFont = mode.getFont("editor.gutter.text.font");
    gutterTextColor = mode.getColor("editor.gutter.text.color");
    //gutterLineHighlightColor = mode.getColor("editor.gutter.linehighlight.color");

    gutterGradient = mode.makeGradient("editor", Editor.LEFT_GUTTER, 500);
  }

  /**
   * Paint a line. Paints the gutter (with background color and text) then the
   * line (background color and text).
   *
   * @param gfx
   *          the graphics context
   * @param tokenMarker
   * @param line
   *          0-based line number
   * @param x
   *          horizontal position
   */
  @Override
  protected void paintLine(Graphics gfx, int line, int x,
                           TokenMarker tokenMarker) {
    try {
      // TODO This line is causing NPEs randomly ever since I added the
      // toggle for Java Mode/Debugger toolbar. [Manindra]
      super.paintLine(gfx, line, x + Editor.LEFT_GUTTER, tokenMarker);

    } catch (Exception e) {
      Messages.log(e.getMessage());
    }

    // formerly only when in debug mode
    paintLeftGutter(gfx, line, x);
//    paintGutterBg(gfx, line, x);
//    paintGutterLine(gfx, line, x);
//    paintGutterText(gfx, line, x);

    //paintErrorLine(gfx, line, x);
  }

  @Override
  public int getScrollWidth() {
    // https://github.com/processing/processing/issues/3591
    return super.getWidth() - Editor.LEFT_GUTTER;
  }

  /**
   * Paint the gutter: draw the background, draw line numbers, break points.
   * @param gfx the graphics context
   * @param line 0-based line number
   * @param x horizontal position
   */
  protected void paintLeftGutter(Graphics gfx, int line, int x) {
    int y = textArea.lineToY(line) + fm.getLeading() + fm.getMaxDescent();
    //if (line == textArea.getSelectionStopLine()) {
    //  gfx.setColor(gutterLineHighlightColor);
    //  gfx.fillRect(0, y, Editor.LEFT_GUTTER, fm.getHeight());
    //} else {
      //gfx.setColor(getJavaTextArea().gutterBgColor);
      Shape clip = gfx.getClip();
      gfx.setClip(0, y, Editor.LEFT_GUTTER, fm.getHeight());
      gfx.drawImage(gutterGradient, 0, 0, getWidth(), getHeight(), this);
      gfx.setClip(clip);  // reset
    //}

    if (line >= textArea.getLineCount())
      return;

    String text = null;
    //if (getJavaEditor().isDebuggerEnabled()) {
    //  text = getJavaTextArea().getGutterText(line);
    //}

    gfx.setColor(gutterTextColor);
    int textRight = Editor.LEFT_GUTTER - Editor.GUTTER_MARGIN;
    int textBaseline = textArea.lineToY(line) + fm.getHeight();

    //if (text != null) {
    //  if (text.equals(JavaTextArea.BREAK_MARKER)) {
    //    drawDiamond(gfx, textRight - 8, textBaseline - 8, 8, 8);

    //  } else if (text.equals(JavaTextArea.STEP_MARKER)) {
    //    //drawRightArrow(gfx, textRight - 7, textBaseline - 7, 7, 6);
    //    drawRightArrow(gfx, textRight - 7, textBaseline - 7.5f, 7, 7);
    //  }
    //} else {

      // if no special text for a breakpoint, just show the line number
      text = String.valueOf(line + 1);
      //text = makeOSF(String.valueOf(line + 1));

      gfx.setFont(gutterTextFont);
      // Right-align the text
      char[] txt = text.toCharArray();
      int tx = textRight - gfx.getFontMetrics().charsWidth(txt, 0, txt.length);
      // Using 'fm' here because it's relative to the editor text size,
      // not the numbers in the gutter
      Utilities.drawTabbedText(new Segment(txt, 0, text.length()),
                               tx, textBaseline, gfx, this, 0);
    //}
  }
}
