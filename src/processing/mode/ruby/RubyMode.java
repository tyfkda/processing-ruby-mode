package processing.mode.ruby;

import java.awt.Point;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import processing.app.Base;
import processing.app.Library;
import processing.app.Mode;
import processing.app.Platform;
import processing.app.RunnerListener;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.SketchException;
import processing.app.syntax.TokenMarker;
import processing.app.ui.Editor;
import processing.app.ui.EditorException;
import processing.app.ui.EditorState;
import processing.core.PApplet;

public class RubyMode extends Mode {
  public RubyMode(Base base, File folder) {
    super(base, folder);
  }

  /**
   * Return the pretty/printable/menu name for this mode. This is separate from
   * the single word name of the folder that contains this mode. It could even
   * have spaces, though that might result in sheer madness or total mayhem.
   */
  @Override
  public String getTitle() {
    return "Ruby";
  }

  /**
   * Create a new editor associated with this mode.
   */
  @Override
  public Editor createEditor(Base base, String path, EditorState state) throws EditorException {
    return new RubyEditor(base, path, state, this);
  }

  /**
   * Returns the default extension for this editor setup. NOTE: no '.' at the
   * beginning, that causes problems!
   */
  @Override
  public String getDefaultExtension() {
    return "rpde";
  }

  /**
   * Returns the module extension.
   */
  @Override
  public String getModuleExtension() {
    return "rb";
  }

  /**
   * Returns a String[] array of proper extensions.
   */
  @Override
  public String[] getExtensions() {
    return new String[] { getDefaultExtension(), getModuleExtension() };
  }

  /**
   * Get array of file/directory names that needn't be copied during "Save As".
   */
  @Override
  public String[] getIgnorable() {
    return new String[] {};
  }

  public Library getCoreLibrary() {
    if (coreLibrary == null) {
      File coreFolder = Platform.getContentFile("core");
      coreLibrary = new Library(coreFolder);
    }
    return coreLibrary;
  }


  /**
   * Runs current sketch.
   */
  public RubyRunner handleRun(final Sketch sketch, final RunnerListener listener, final File sketchTempFolder) throws SketchException {
    final RubyRunner runner = new RubyRunner(listener);

    final List<String> params = new ArrayList<String>();
    if (listener instanceof Editor) {
      Editor editor = (Editor) listener;
      Point windowLocation = editor.getSketchLocation();
      if (windowLocation != null) {
        params.add(PApplet.ARGS_LOCATION + "=" +
                   windowLocation.x + "," + windowLocation.y);
      } else {
        Point editorLocation = editor.getLocation();
        params.add(PApplet.ARGS_EDITOR_LOCATION + "=" +
                   editorLocation.x + "," + editorLocation.y);
      }
    }

    new Thread(new Runnable() {
        @Override
        public void run() {
          File sourceFile = dumpSketchToTemporary(sketch, sketchTempFolder);
          String classPath = getClassPasses(sketch, sketchTempFolder);
          String sketchPath = sketch.getFolder().getAbsolutePath();

          File modeFile = sketch.getMode().getContentFile("mode");
          File runnerScriptPath = new File(modeFile, "run.rb");

          runner.launchApplication(sketch.getName(), sourceFile.getAbsolutePath(),
                                   sketchPath, runnerScriptPath.getAbsolutePath(),
                                   classPath, params);
        }
      }).start();
    return runner;
  }

  /**
   * Restart sketch.
   */
  public void restartSketch(Sketch sketch, File sketchTempFolder, RubyRunner runner) {
    File sourceFile = dumpSketchToTemporary(sketch, sketchTempFolder);
    runner.restartSketch(sourceFile.getAbsolutePath());
  }

  /**
   * Outputs sketch codes into temporary directory, and returns its file.
   */
  private File dumpSketchToTemporary(Sketch sketch, File sketchTempFolder) {
    File mainFile = null;
    for (int i = 0, n = sketch.getCodeCount(); i < n; ++i) {
      SketchCode sc = sketch.getCode(i);
      final File out = new File(sketchTempFolder, sc.getFileName());
      if (i == 0)
        mainFile = out;
      try {
        final PrintWriter stream = new PrintWriter(new FileWriter(out));
        try {
          stream.write(sc.getProgram());
        } finally {
          stream.close();
        }
      } catch (Exception ex) {
        System.err.println("Uncaught exception type:" + ex.getClass());
        ex.printStackTrace();
        return null;
      }
    }
    return mainFile;
  }

  static private String getClassPasses(Sketch sketch, File sketchTempFolder) {
    // Processing libraries.
    Library core = sketch.getMode().getCoreLibrary();
    StringBuffer sb = new StringBuffer();
    sb.append(sketchTempFolder.getAbsolutePath());
    sb.append(core.getClassPath());

    // Runtime .jar files for Ruby mode.
    final File runtimeFolder = new File(sketch.getMode().getFolder(), "runtime");
    File[] jars = runtimeFolder.listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith(".jar") && !name.startsWith(".");
        }
      });
    for (File jar : jars) {
      sb.append(File.pathSeparator);
      sb.append(jar.getAbsolutePath());
    }
    return sb.toString();
  }

  @Override
  public TokenMarker createTokenMarker() {
    return new RubyTokenMarker();
  }
}
