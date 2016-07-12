package processing.mode.ruby;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import processing.app.Base;
import processing.app.Formatter;
import processing.app.Language;
import processing.app.Messages;
import processing.app.Mode;
import processing.app.SketchCode;
import processing.app.syntax.JEditTextArea;
import processing.app.syntax.PdeTextAreaDefaults;
import processing.app.ui.Editor;
import processing.app.ui.EditorException;
import processing.app.ui.EditorState;
import processing.app.ui.EditorToolbar;
import processing.app.ui.Toolkit;
import processing.app.ui.pdex.GeneralTextArea;

@SuppressWarnings("serial")
public class RubyEditor extends Editor {
  private RubyMode rbmode;

  private File sketchTempFolder;
  // Runner associated with this editor window
  private RubyRunner runtime;
  private boolean prepareRunCalling;  // Flag to prevent terminating runtime when prepareRun

  protected RubyEditor(Base base, String path, EditorState state, Mode mode) throws EditorException {
    super(base, path, state, mode);
    rbmode = (RubyMode) mode;

    ((GeneralTextArea) textarea).setMode(mode);

    // prepareRun() assumes all SketchCode has a document, so make it sure.
    for (SketchCode sc : sketch.getCode())
      setCode(sc);
    setCode(sketch.getCode(0));
  }

  @Override
  public void dispose() {
    releaseRuntime();
    super.dispose();
  }

  @Override
  protected JEditTextArea createTextArea() {
    return new GeneralTextArea(new PdeTextAreaDefaults(mode), this);
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
    if (!prepareRunCalling)
      releaseRuntime();
  }

  @Override
  public void prepareRun() {
    prepareRunCalling = true;
    super.prepareRun();
    prepareRunCalling = false;
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

  private void releaseRuntime() {
    if (runtime == null)
      return;
    runtime.shutDown();
    runtime = null;
  }
}
