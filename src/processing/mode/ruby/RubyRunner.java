package processing.mode.ruby;

import processing.app.Base;
import processing.app.Editor;
import processing.app.Preferences;
import processing.app.RunnerListener;
import processing.app.exec.StreamRedirectThread;
import processing.core.PApplet;
import processing.mode.java.runner.MessageConsumer;
import processing.mode.java.runner.MessageSiphon;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class RubyRunner implements MessageConsumer {
  protected Process process;

  // Thread transferring remote error stream to our error stream
  protected Thread errThread = null;

  // Thread transferring remote output stream to our output stream
  protected Thread outThread = null;

  protected Editor editor;

  public RubyRunner(RunnerListener listener) {
    if (listener instanceof Editor)
      editor = (Editor) listener;
  }

  public void close() {
    if (process != null)
      sendAppMessage("close");
  }

  /**
   * @param sketchName  Sketch name.
   * @param sourcePath  Temporary script path, which is put for running the
   *                    sketch.
   * @param sketchPath  Path where the sketch is saved.
   * @param runnerScriptPath  ex. ~/Documents/Processing/modes/RubyMode/mode/run.rb
   * @param classPath  .jar files for Processing and RubyMode.
   * @param processingCoreJars  .jar files for Processing core, which are used
   *                            to `require` them in Processing::App.
   *                            Assumes separator is put at the top.
   */
  public void launchApplication(String sketchName, String sourcePath,
                                String sketchPath, String runnerScriptPath,
                                String classPath, String processingCoreJars) {
    final List<String> command = new ArrayList<String>();
    command.add(Base.getJavaPath());  // Java executable file.
    command.add("-cp");
    command.add(classPath);
    getMachineParams(sketchName, command);
    command.add("org.jruby.Main");  // JRuby as a boot class.
    command.add(runnerScriptPath);  // Script file name for JRuby.
    // Below, arguments for runner script.
    command.add(sourcePath);  // Sketch main file name.
    command.add(processingCoreJars);  // processing.core.*, concatenated with ':'
    // Options
    command.add(PApplet.ARGS_SKETCH_FOLDER + "=" + sketchPath);
    command.add(PApplet.ARGS_EDITOR_LOCATION + "=0,0");

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
                String[] errorStrings = PApplet.loadStrings(process.getErrorStream());
                String[] inputStrings = PApplet.loadStrings(process.getInputStream());
                for (String s : inputStrings)
                  System.out.println(s);
                for (String s : errorStrings)
                  System.err.println(s);
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

    errThread = new MessageSiphon(process.getErrorStream(), this).getThread();
    outThread = new StreamRedirectThread("JVM stdout Reader",
                                         process.getInputStream(),
                                         System.out);
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

    if (Base.isMacOS()) {
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

    System.err.print(s);
    System.err.flush();
  }

  public void restartSketch(String sourcePath) {
    sendAppMessage("requestRestart " + sourcePath);
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
