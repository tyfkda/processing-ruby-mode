package processing.mode.ruby;

//import processing.app.Base;
import processing.mode.java.JavaToolbar;

import java.awt.Image;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class RubyToolbar extends JavaToolbar {
  //static protected final int RUN    = JavaToolbar.RUN;
  //static protected final int STOP   = JavaToolbar.STOP;

  //static protected final int NEW    = JavaToolbar.NEW;
  //static protected final int OPEN   = JavaToolbar.OPEN;
  //static protected final int SAVE   = JavaToolbar.SAVE;

  public RubyToolbar(RubyEditor editor /*, Base base*/) {
    super(editor /*, base*/);
  }

  /*
  public void init() {
    Image[][] images = loadImages();
    for (int i = 0; i < 2; ++i)
      addButton(getTitle(i, false), getTitle(i, false), images[i], false);
  }
  */

/*
  public void handlePressed(MouseEvent e, int sel) {
    boolean shift = e.isShiftDown();
    RubyEditor rbeditor = (RubyEditor) editor;

    switch (sel) {
    case RUN:
      rbeditor.handleRun();
      break;

    case STOP:
      rbeditor.handleStop();
      break;

      /*
    case OPEN:
//      popup = menu.getPopupMenu();
      // TODO I think we need a longer chain of accessors here.
      JPopupMenu popup = editor.getMode().getToolbarMenu().getPopupMenu();
      popup.show(this, e.getX(), e.getY());
      break;

    case NEW:
      base.handleNew();
      break;

    case SAVE:
      rbeditor.handleSave(false);
      break;
      * /
    }
  }
  */
}
