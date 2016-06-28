package processing.mode.ruby;

import processing.app.Base;
import processing.app.Formatter;
import processing.app.Language;
import processing.app.Messages;
import processing.app.Mode;
import processing.app.ui.Editor;
import processing.app.ui.EditorException;
import processing.app.ui.EditorState;
import processing.app.ui.EditorToolbar;
import processing.app.ui.Toolkit;
import processing.mode.java.AutoFormat;
//import processing.mode.java.JavaToolbar;
//import processing.mode.java.PdeKeyListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class RubyEditor extends Editor {
  RubyMode rbmode;
  //PdeKeyListener listener;

  private File sketchTempFolder;
  // Runner associated with this editor window
  private RubyRunner runtime;

  protected RubyEditor(Base base, String path, EditorState state, Mode mode) throws EditorException {
    super(base, path, state, mode);
    rbmode = (RubyMode) mode;

    //listener = new PdeKeyListener(this, textarea);
  }

  @Override
  public EditorToolbar createToolbar() {
    return new RubyToolbar(this);
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
    JMenuItem runItem = Toolkit.newJMenuItem(Language.text("menu.sketch.run"), 'R');
    runItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleRun();
      }
    });

    JMenuItem stopItem = new JMenuItem(Language.text("menu.sketch.stop"));
    stopItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleStop();
      }
    });
    return buildSketchMenu(new JMenuItem[] { runItem, stopItem });
  }

  @Override
  public void handleImportLibrary(String arg0) {
    Messages.showMessage("Sorry", "You can't do that yet."); //TODO implement
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

  /**
   * Deactivate the Run button. This is called by Runner to notify that the
   * sketch has stopped running, usually in response to an error (or maybe
   * the sketch completing and exiting?) Tools should not call this function.
   * To initiate a "stop" action, call handleStop() instead.
   */
  @Override
  public void deactivateRun() {
    toolbar.deactivateRun();
    if (runtime != null) {
      runtime.shutDown();
      runtime = null;
    }
  }


  public void handleRun() {
    if (sketchTempFolder == null)
      sketchTempFolder = sketch.makeTempFolder();

    prepareRun();
    toolbar.activateRun();
    try {
      if (runtime == null)
        runtime = rbmode.handleRun(sketch, this, sketchTempFolder);
      else
        rbmode.restartSketch(sketch, sketchTempFolder, runtime);
    } catch (Exception e) {
      statusError(e);
    }
  }

  public void handleStop() {
    toolbar.activateStop();

    try {
      if (runtime != null) {
        runtime.close();  // kills the window (process keeps running).
      }
    } catch (Exception e) {
      statusError(e);
    }

    toolbar.deactivateStop();
    toolbar.deactivateRun();

    toFront();
  }
}
