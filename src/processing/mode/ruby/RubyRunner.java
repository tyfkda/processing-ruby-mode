package processing.mode.ruby;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import processing.app.Platform;
import processing.app.Preferences;
import processing.app.RunnerListener;
import processing.app.exec.StreamRedirectThread;
import processing.app.ui.Editor;
import processing.core.PApplet;
import processing.mode.java.runner.MessageConsumer;
import processing.mode.java.runner.MessageSiphon;
import processing.mode.ruby.util.StreamFilter;

public class RubyRunner implements MessageConsumer {
  /**
   * When run externally to a PdeEditor,
   * this is sent by the sketch when it closed.
   */
  static private final String EXTERNAL_DEACTIVATE_RUN = "__DEACTIVATE_RUN__";

  protected Process process;

  // Thread transferring remote error stream to our error stream
  protected Thread errThread = null;

  // Thread transferring remote output stream to our output stream
  protected Thread outThread = null;

  protected Editor editor;

  private StreamFilter errorStreamWithFilter;

  protected PrintStream sketchErr;
  protected PrintStream sketchOut;

  public RubyRunner(RunnerListener listener) {
    if (listener instanceof Editor) {
      this.editor = (Editor) listener;
      sketchErr = editor.getConsole().getErr();
      sketchOut = editor.getConsole().getOut();
    } else {
      sketchErr = System.err;
      sketchOut = System.out;
    }
  }

  // Close app window, but process keeps running.
  public void close() {
    if (process != null)
      sendAppMessage("close");
  }

  // Kill app window and process.
  public void shutDown() {
    if (process != null) {
      sendAppMessage("close");
      process.destroy();
      process = null;
    }
  }

  /**
   * @param sketchName  Sketch name.
   * @param sourcePath  Temporary script path, which is put for running the
   *                    sketch.
   * @param sketchPath  Path where the sketch is saved.
   * @param runnerScriptPath  ex. ~/Documents/Processing/modes/RubyMode/mode/run.rb
   * @param classPath  .jar files for Processing and RubyMode.
   */
  public void launchApplication(String sketchName, String sourcePath,
                                String sketchPath, String runnerScriptPath,
                                String classPath, List<String> params) {
    String processingRoot = Platform.getContentFile("").getAbsolutePath();

    final List<String> command = new ArrayList<String>();
    command.add(Platform.getJavaPath());  // Java executable file.
    command.add("-cp");
    command.add(classPath);
    getMachineParams(sketchName, command);
    command.add("org.jruby.Main");  // JRuby as a boot class.
    command.add(runnerScriptPath);  // Script file name for JRuby.
    // Below, arguments for runner script.
    command.add(sourcePath);  // Sketch main file name.
    command.add(processingRoot);
    // Options
    command.add(PApplet.ARGS_SKETCH_FOLDER + "=" + sketchPath);
    for (String param : params)
      command.add(param);

    process = null;
    new Thread(new Runnable() {
        @Override
        public void run() {
          ProcessBuilder pb = new ProcessBuilder(command);
          try {
            process = pb.start();

            try {
              int result = process.waitFor();
              if (result != 0) {
                if (process != null) {
                  String[] errorStrings = PApplet.loadStrings(process.getErrorStream());
                  String[] inputStrings = PApplet.loadStrings(process.getInputStream());
                  for (String s : inputStrings)
                    System.out.println(s);
                  for (String s : errorStrings)
                    System.err.println(s);
                }
              }
            } catch (InterruptedException ex) {
              System.err.println(ex);
            }
            process = null;
          } catch (IOException ex) {
            System.err.println("IOException: " + ex);
          }
        }
      }).start();

    try {
      while (process == null)
        Thread.sleep(100);
    } catch (InterruptedException ex) {
      System.err.println(ex);
    }

    int lastSeparator = sourcePath.lastIndexOf(File.separatorChar);
    String dirname = sourcePath.substring(0, lastSeparator + 1);
    Pattern pattern = Pattern.compile(Pattern.quote(dirname));
    errorStreamWithFilter = new StreamFilter(System.err, pattern, "");
    errThread = new MessageSiphon(process.getErrorStream(), this).getThread();
    outThread = new StreamRedirectThread("JVM stdout Reader",
                                         process.getInputStream(),
                                         new StreamFilter(System.out, pattern, ""));
    errThread.start();
    outThread.start();
    try {
      errThread.join(); // Make sure output is forwarded
      outThread.join(); // before we exit

      // At this point, disable the run button.
      // This happens when the sketch is exited by hitting ESC,
      // or the user manually closes the sketch window.
      // TODO this should be handled better, should it not?
      if (editor != null) {
        editor.deactivateRun();
      }
    } catch (InterruptedException ex) {
      System.err.println(ex);
    }
  }

  // Taken from processing.mode.java.runner.Runner#getMachineParams
  protected void getMachineParams(String sketchName, List<String> params) {
    //params.add("-Xint"); // interpreted mode
    //params.add("-Xprof");  // profiler
    //params.add("-Xaprof");  // allocation profiler
    //params.add("-Xrunhprof:cpu=samples");  // old-style profiler

    // TODO change this to use run.args = true, run.args.0, run.args.1, etc.
    // so that spaces can be included in the arg names
    String options = Preferences.get("run.options");
    if (options.length() > 0) {
      String pieces[] = PApplet.split(options, ' ');
      for (int i = 0; i < pieces.length; i++) {
        String p = pieces[i].trim();
        if (p.length() > 0) {
          params.add(p);
        }
      }
    }

//    params.add("-Djava.ext.dirs=nuffing");

    if (Preferences.getBoolean("run.options.memory")) {
      params.add("-Xms" + Preferences.get("run.options.memory.initial") + "m");
      params.add("-Xmx" + Preferences.get("run.options.memory.maximum") + "m");
    }

    if (Platform.isMacOS()) {
      params.add("-Xdock:name=" + sketchName);
//      params.add("-Dcom.apple.mrj.application.apple.menu.about.name=" +
//                 sketch.getMainClassName());
    }
    // sketch.libraryPath might be ""
    // librariesClassPath will always have sep char prepended
    params.add("-Djava.library.path=" +
               //build.getJavaLibraryPath() +
               //File.pathSeparator +
               System.getProperty("java.library.path"));

    //params.add("-cp");
    //params.add(build.getClassPath());
//    params.add(sketch.getClassPath() +
//        File.pathSeparator +
//        Base.librariesClassPath);

    // enable assertions
    // http://dev.processing.org/bugs/show_bug.cgi?id=1188
    params.add("-ea");
    //PApplet.println(PApplet.split(sketch.classPath, ':'));
  }

  synchronized public void message(String s) {
    // this is the PApplet sending us a message that the applet
    // is being moved to a new window location
    if (s.indexOf(PApplet.EXTERNAL_MOVE) == 0) {
      String nums = s.substring(s.indexOf(' ') + 1).trim();
      int space = nums.indexOf(' ');
      int left = Integer.parseInt(nums.substring(0, space));
      int top = Integer.parseInt(nums.substring(space + 1));
      // this is only fired when connected to an editor
      editor.setSketchLocation(new Point(left, top));
      return;
    }
    if (s.indexOf(EXTERNAL_DEACTIVATE_RUN) == 0) {
      editor.deactivateRun();
      return;
    }

    try {
      errorStreamWithFilter.print(s);
      errorStreamWithFilter.flush();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public void restartSketch(String sourcePath) {
    List<String> params = new ArrayList<String>();
    if (editor != null) {
      Point location = editor.getSketchLocation();
      if (location != null) {
        params.add("x=" + location.x);
        params.add("y=" + location.y);
      }
    }
    params.add("sourcePath=" + sourcePath);

    sendAppMessage("requestRestart?" + String.join("&", params));
  }

  private void sendAppMessage(String message) {
    if (process == null)
      return;

    OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
    try {
      writer.write(message + "\n");
      writer.flush();
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
