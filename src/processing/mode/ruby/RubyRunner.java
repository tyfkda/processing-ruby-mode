package processing.mode.ruby;

import processing.app.Base;
import processing.app.Editor;
import processing.app.Preferences;
import processing.app.RunnerListener;
import processing.app.exec.StreamRedirectThread;
import processing.core.PApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RubyRunner {
  protected Process process;

  // Thread transferring remote output stream to our output stream
  protected Thread outThread = null;

  protected Editor editor;

  public RubyRunner(RunnerListener listener) {
    if (listener instanceof Editor)
      editor = (Editor) listener;
  }

  public void close() {
    if (process != null) {
      process.destroy();
    }
  }

  /**
   * @param sketchName  Sketch name.
   * @param sourcePath  Temporary script path, which is put for running the
                        sketch.
   * @param sketchPath  Path where the sketch is saved.
   * @param runnerScriptPath  ex. ~/Documents/Processing/modes/RubyMode/mode/run.rb
   * @param classPath  .jar files for Processing and RubyMode.
   * @param processingCoreJars  .jar files for Processing core, which are used
                                to `require` them in Processing::App.
                                Assumes separator is put at the top.
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

    process = null;
    new Thread(new Runnable() {
        @Override
        public void run() {
          ProcessBuilder pb = new ProcessBuilder(command);
          try {
            process = pb.start();

            try {
              int result = process.waitFor();
              if (result == 0) {
                System.err.println("Run finished");
              } else {
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

    outThread = new StreamRedirectThread("JVM stdout Reader",
                                         process.getInputStream(),
                                         System.out);
    outThread.start();
    try {
      outThread.join();

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
}
