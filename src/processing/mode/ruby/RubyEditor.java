package processing.mode.ruby;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.EditorToolbar;
import processing.app.Formatter;
import processing.app.Mode;
import processing.app.Toolkit;
import processing.mode.java.AutoFormat;
import processing.mode.java.JavaToolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class RubyEditor extends Editor {
  RubyMode rbmode;

  protected RubyEditor(Base base, String path, EditorState state, RubyMode mode) {
    super(base, path, state, mode);
    rbmode = mode;
  }

  @Override
  public EditorToolbar createToolbar() {
    return new RubyToolbar(this, base);
  }

  @Override
  public Formatter createFormatter() {
    return new AutoFormat();
  }

  @Override
  public JMenu buildFileMenu() {
    //Okay, this is kinda weird
    /*
    String appTitle = PythonToolbar.getTitle(PythonToolbar.EXPORT, false);  //get export string

    JMenuItem exportApplication = Toolkit.newJMenuItem(appTitle, 'E'); //set it up

    exportApplication.addActionListener(new ActionListener() { //yadda yadda
        public void actionPerformed(ActionEvent e) {
          handleExportApplication();
        }
      });
    */
    return buildFileMenu(new JMenuItem[] { /*exportApplication*/ }); //and then call the SUPERCLASS method
  }

  @Override
  public JMenu buildSketchMenu() { //the 'Sketch' menu, if that wasn't obvious
    JMenuItem runItem = Toolkit.newJMenuItem(RubyToolbar.getTitle(RubyToolbar.RUN, false), 'R');
    runItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleRun();
      }
    });

    JMenuItem presentItem = Toolkit.newJMenuItemShift(RubyToolbar.getTitle(RubyToolbar.RUN, true), 'R');
    presentItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handlePresent();
      }
    });

    JMenuItem stopItem = new JMenuItem(RubyToolbar.getTitle(RubyToolbar.STOP, false));
    stopItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleStop();
      }
    });
    return buildSketchMenu(new JMenuItem[] { runItem, presentItem, stopItem });
  }

  @Override
  public void handleImportLibrary(String arg0) {
    Base.showMessage("Sorry", "You can't do that yet."); //TODO implement
  }

  @Override
  public JMenu buildHelpMenu() {
    JMenu menu = new JMenu("Help");
    JMenuItem item = new JMenuItem("help is for weaklings");
    item.setEnabled(false);
    menu.add(item);
    return menu;
  }

  @Override
  public String getCommentPrefix() {
    return "#";
  }

  @Override
  public void internalCloseRunner() {
  }

  @Override
  public void deactivateRun() {
  }


  public void handleRun() {
    System.err.println("handleRun called");
  }

  public void handlePresent() {
    System.err.println("handlePresent called");
  }

  public void handleStop() {
    System.err.println("handleStop called");
  }

  /**
   * Handler for Sketch &rarr; Export Application
   */
  public void handleExportApplication() {
    System.err.println("handleExportApplication called");
  }
}
