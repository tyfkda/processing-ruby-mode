package processing.mode.ruby;

import processing.app.Base;
import processing.app.exec.StreamRedirectThread;
import processing.core.PApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RubyRunner {
  protected Process process;

  // Thread transferring remote output stream to our output stream
  protected Thread outThread = null;

  public void launchApplication(final String runnerScriptPath, final String classPath, final String sourcePath, final String sketchPath, final String processingCoreJars) {
    final List<String> command = new ArrayList<String>();
    command.add(Base.getJavaPath());  // Java executable file.
    command.add("-cp");
    command.add(classPath);
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
              /*int result =*/ process.waitFor();
            } catch (InterruptedException ex) {
              System.err.println(ex);
            }
            System.err.println("Run finished");
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
    } catch (InterruptedException ex) {
      System.err.println(ex);
    }
  }
}
