package processing.mode.ruby;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.Library;
import processing.app.Mode;
import processing.app.RunnerListener;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.SketchException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.FileWriter;

public class RubyMode extends Mode {
  private static final FilenameFilter JARS = new FilenameFilter() {
    @Override
    public boolean accept(final File dir, final String name) {
      return name.endsWith(".jar");
    }
  };

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
  public Editor createEditor(Base base, String path, EditorState state) {
    return new RubyEditor(base, path, state, this);
  }

  /**
   * Returns the default extension for this editor setup. NOTE: no '.' at the
   * beginning, that causes problems!
   */
  @Override
  public String getDefaultExtension() {
    return "pde";
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
      File coreFolder = Base.getContentFile("core");
      coreLibrary = new Library(coreFolder);
    }
    return coreLibrary;
  }





  /**
   * Runs current sketch.
   */
  public void handleRun(Sketch sketch, RunnerListener listener) throws SketchException {
    File outputFolder = sketch.makeTempFolder();
    File sourceFile = dumpSketchToTemporary(sketch, outputFolder);
    String classPath = getClassPasses(sketch, outputFolder);

    File modeFile = sketch.getMode().getContentFile("mode");
    File runnerScriptPath = new File(modeFile, "run.rb");

    RubyRunner runner = new RubyRunner();
    runner.launchApplication(runnerScriptPath.getAbsolutePath(), classPath, sourceFile.getAbsolutePath());
  }

  /**
   * Outputs sketch codes into temporary directory, and returns its file.
   */
  private File dumpSketchToTemporary(Sketch sketch, File outputFolder) {
    StringBuffer bigCode = new StringBuffer();
    for (SketchCode sc : sketch.getCode())
      bigCode.append(sc.getProgram());

    try {
      final File out = new File(outputFolder, sketch.getName() + ".rb");
      final PrintWriter stream = new PrintWriter(new FileWriter(out));
      try {
        stream.write(bigCode.toString());
      } finally {
        stream.close();
      }
      return out;
    } catch (Exception ex) {
      System.err.println("Uncaught exception type:" + ex.getClass());
      ex.printStackTrace();
      return null;
    }
  }

  static private String getClassPasses(Sketch sketch, File outputFolder) {
    Library core = sketch.getMode().getCoreLibrary();
    StringBuffer sb = new StringBuffer();
    sb.append(outputFolder.getAbsolutePath());
    sb.append(core.getClassPath());

    String javaClassPath = System.getProperty("java.class.path");
    // Remove quotes if any.. A messy (and frequent) Windows problem
    if (javaClassPath.startsWith("\"") && javaClassPath.endsWith("\"")) {
      javaClassPath = javaClassPath.substring(1, javaClassPath.length() - 1);
    }
    sb.append(File.pathSeparator);
    sb.append(javaClassPath);

    File[] libJars = sketch.getMode().getContentFile("mode").listFiles(JARS);
    if (libJars != null) {
      for (final File jar : libJars) {
        sb.append(File.pathSeparator);
        sb.append(jar.getAbsolutePath());
      }
    }
    return sb.toString();
  }
}
